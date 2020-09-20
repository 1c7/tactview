package com.helospark.tactview.ui.javafx.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;

import com.helospark.lightdi.annotation.Component;
import com.helospark.tactview.core.util.logger.Slf4j;
import com.helospark.tactview.ui.javafx.PlaybackController;

@Component
public class AudioStreamService {
    private DataLine.Info dataLineInfo;
    private SourceDataLine sourceDataLine = null;

    @Slf4j
    private Logger logger;

    private int initializedFrameSize;

    public void streamAudio(byte[] data) {
        logger.debug("Streaming " + data.length + " at " + System.currentTimeMillis() + " with length "
                + (1000 * data.length / (PlaybackController.BYTES * PlaybackController.CHANNELS * PlaybackController.SAMPLE_RATE)));

        if (data.length > 0) {
            if (sourceDataLine == null) { // TODO: reinit
                try {
                    logger.info("Initializing sourceDataLine");
                    AudioFormat format = new AudioFormat(PlaybackController.SAMPLE_RATE, PlaybackController.BYTES * 8, PlaybackController.CHANNELS, true, true);
                    dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
                    sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                    sourceDataLine.open(format, data.length);
                    sourceDataLine.start();
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
                initializedFrameSize = data.length;
            }

            logger.debug("There is still " + (sourceDataLine.getBufferSize() - sourceDataLine.available()) + " bytes in the buffer at " + System.currentTimeMillis());

            int bytesWritten = sourceDataLine.write(data, 0, data.length);

            logger.debug("Bytes written: " + bytesWritten + " " + data.length + " at " + System.currentTimeMillis());
        }
    }

    public int numberOfBytesThatCanBeWritten() {
        return sourceDataLine.available();
    }

    public void clearBuffer() {
        sourceDataLine.flush();
    }
}
