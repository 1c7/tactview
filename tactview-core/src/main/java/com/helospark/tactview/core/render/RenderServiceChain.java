package com.helospark.tactview.core.render;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.helospark.lightdi.annotation.Component;
import com.helospark.tactview.core.timeline.effect.interpolation.provider.ValueListElement;
import com.helospark.tactview.core.util.logger.Slf4j;

@Component
public class RenderServiceChain {
    private List<RenderService> renderServiceChainItem;
    @Slf4j
    private Logger logger;

    public RenderServiceChain(List<RenderService> renderServiceChainItem) {
        this.renderServiceChainItem = renderServiceChainItem;
    }

    public CompletableFuture<Void> render(RenderRequest renderRequest) {
        RenderService chainItem = getRenderer(renderRequest);

        return CompletableFuture
                .runAsync(() -> chainItem.render(renderRequest));
    }

    public RenderService getRenderer(RenderRequest renderRequest) {
        return renderServiceChainItem.stream()
                .filter(a -> a.supports(renderRequest))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No renderer supports format " + renderRequest.getFileName()));
    }

    public List<ValueListElement> getCommonHandledExtensions() {
        List<ValueListElement> elements = renderServiceChainItem.stream()
                .flatMap(renderService -> renderService.handledExtensions().stream())
                .collect(Collectors.toList());
        List<ValueListElement> reversedList = new ArrayList<>();

        for (int i = elements.size() - 1; i >= 0; --i) {
            reversedList.add(elements.get(i));
        }

        return reversedList;
    }

}
