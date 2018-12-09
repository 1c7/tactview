package com.helospark.tactview.core.timeline;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.helospark.lightdi.annotation.Component;

@Component
public class ClipFactoryChain {
    private List<ClipFactory> clipFactoryChain;

    public ClipFactoryChain(List<ClipFactory> clipFactoryChain) {
        this.clipFactoryChain = clipFactoryChain;
    }

    public List<TimelineClip> createClips(AddClipRequest request) {
        List<ClipFactory> factories = findFactory(request);
        return factories
                .stream()
                .map(factory -> factory.createClip(request))
                .collect(Collectors.toList());
    }

    private List<ClipFactory> findFactory(AddClipRequest request) {
        return clipFactoryChain
                .parallelStream()
                .filter(factory -> factory.doesSupport(request))
                .collect(Collectors.toList());
    }

    public TimelineClip restoreClip(JsonNode savedClip) {
        String factoryId = savedClip.get("creatorFactoryId").asText();
        ClipFactory foundFactory = clipFactoryChain.stream()
                .filter(factory -> factory.getId().equals(factoryId))
                .findFirst()
                .orElseThrow();
        TimelineClip result = foundFactory.restoreClip(savedClip);
        return result;
    }

}
