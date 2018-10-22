package com.helospark.tactview.core.decoder.framecache;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;

import com.helospark.lightdi.annotation.Component;
import com.helospark.lightdi.annotation.Value;
import com.helospark.tactview.core.util.ThreadSleep;
import com.helospark.tactview.core.util.logger.Slf4j;

@Component
public class MemoryManager {
    private static final boolean DEBUG = false;
    private static final byte[] EMPTY_PIXEL = new byte[]{0, 0, 0, 0};
    private Map<Integer, BufferInformation> freeBuffersMap = new ConcurrentHashMap<>();
    private Map<ByteBuffer, String> debugTrace = Collections.synchronizedMap(new IdentityHashMap<>());
    private Long maximumSizeHint;
    @Slf4j
    private Logger logger;

    private AtomicLong currentSize = new AtomicLong(0);
    private boolean running = true;

    public MemoryManager(@Value("${memory.manager.size}") Long maximumSizeHint) {
        this.maximumSizeHint = maximumSizeHint;
    }

    @PostConstruct
    public void init() {
        new Thread(() -> {
            while (running) {
                try {
                    ThreadSleep.sleep(1000);

                    cleanupOfOldBuffers(10000L);

                    if (currentSize.get() >= maximumSizeHint * 0.8) {
                        doForcefulCleanup();
                    }
                } catch (Exception e) {
                    logger.error("Error while cleaning buffer", e);
                }
            }
        }).start();
    }

    private void doForcefulCleanup() {
        double target = maximumSizeHint * 0.6;

        // step1 clean buffers not accessed in the last 2 secs
        if (currentSize.get() > target) {
            cleanupOfOldBuffers(2000);
        }
        // step2 clean 50% of buffers that have not needed an increase in size in 10 secs
        if (currentSize.get() > target) {
            clearPartOfBuffersThatNotNeededAnUpdate(10000L, 0.5);
        }
        // step3 clean 70% of buffers that have not needed an increase in size in 2 secs
        if (currentSize.get() > target) {
            clearPartOfBuffersThatNotNeededAnUpdate(2000L, 0.7);
        }
        // step4: profit???
    }

    private void clearPartOfBuffersThatNotNeededAnUpdate(long time, double percent) {
        long now = System.currentTimeMillis();
        freeBuffersMap.values()
                .stream()
                .filter(a -> now - a.lastNeededAnUpdate > time)
                .forEach(a -> {
                    int approximateSize = a.buffers.size();
                    int i = 0;
                    ByteBuffer buffer;
                    while (i <= approximateSize * percent && (buffer = a.buffers.poll()) != null) {
                        removeBuffer(buffer);
                    }
                });
    }

    private void cleanupOfOldBuffers(long oldThreashold) {
        long now = System.currentTimeMillis();
        freeBuffersMap.values()
                .stream()
                .filter(e -> now - e.lastRequestedAccessed > oldThreashold)
                .forEach(e -> {
                    ByteBuffer buffer;

                    int counter = 0;
                    while (counter < 10 && (buffer = e.buffers.poll()) != null) {
                        removeBuffer(buffer);
                        ++counter;
                    }
                });
    }

    private void removeBuffer(ByteBuffer buffer) {
        int capacity = buffer.capacity();
        currentSize.addAndGet(-capacity);
        logger.debug("Buffer removed with size {} current size {}", capacity, currentSize.get());
        // TODO: maybe we can force deallocation here
    }

    @PreDestroy
    public void destroy() {
        if (DEBUG) {
            for (var entry : debugTrace.entrySet()) {
                logger.error("Buffer {} never removed, allocated by {}", entry.getKey().capacity(), entry.getValue());
            }
        }
        running = false;
    }

    public ByteBuffer requestBuffer(Integer bytes) {
        return requestBuffers(bytes, 1).get(0);
    }

    public List<ByteBuffer> requestBuffers(Integer bytes, int count) {
        List<ByteBuffer> result = new ArrayList<>(count);
        BufferInformation freeBuffers = freeBuffersMap.get(bytes);
        if (freeBuffers == null) {
            freeBuffers = new BufferInformation(new ConcurrentLinkedQueue<>());
            freeBuffersMap.put(bytes, freeBuffers);
        }
        freeBuffers.lastRequestedAccessed = System.currentTimeMillis();
        ByteBuffer element;
        while (result.size() < count && (element = freeBuffers.buffers.poll()) != null) {
            result.add(element);
        }
        logger.debug("{} {} buffer requested, from this {} was server from free buffer map, rest are newly allocated", count, bytes, result.size());
        int remainingElements = count - result.size();
        if (remainingElements > 0) {
            freeBuffers.lastNeededAnUpdate = System.currentTimeMillis();
        }
        for (int i = 0; i < remainingElements; ++i) {
            currentSize.addAndGet(bytes);
            result.add(ByteBuffer.allocateDirect(bytes));
        }

        if (DEBUG) {
            for (ByteBuffer a : result) {
                debugTrace.put(a, Arrays.toString(Thread.currentThread().getStackTrace()));
            }
        }

        return result;
    }

    public void returnBuffer(ByteBuffer buffer) {
        returnBuffers(Collections.singletonList(buffer));
    }

    public void returnBuffers(List<ByteBuffer> buffers) {
        for (ByteBuffer buffer : buffers) {
            clearBuffer(buffer);
            freeBuffersMap.compute(buffer.capacity(), (k, value) -> {
                if (value == null) {
                    value = new BufferInformation(new ConcurrentLinkedQueue<>());
                }
                value.buffers.offer(buffer);
                if (DEBUG) {
                    debugTrace.remove(buffer);
                }
                logger.debug("{} returned", buffer.capacity());
                return value;
            });
        }
    }

    private void clearBuffer(ByteBuffer buffer) {
        // TODO: more efficiency can be gained here
        buffer.position(0);
        for (int i = 0; i < buffer.capacity(); i += 4) {
            buffer.put(EMPTY_PIXEL);
        }
    }

    static class BufferInformation implements Comparable<BufferInformation> {
        public Queue<ByteBuffer> buffers;
        public volatile long lastRequestedAccessed = System.currentTimeMillis();
        public volatile long lastNeededAnUpdate = System.currentTimeMillis();

        public BufferInformation(Queue<ByteBuffer> buffers) {
            this.buffers = buffers;
        }

        @Override
        public int compareTo(BufferInformation o) {
            return Long.compare(lastNeededAnUpdate, o.lastNeededAnUpdate);
        }

    }

}
