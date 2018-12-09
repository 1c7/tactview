package com.helospark.tactview.core.timeline.clipfactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.fasterxml.jackson.databind.JsonNode;
import com.helospark.lightdi.annotation.Component;
import com.helospark.tactview.core.decoder.ImageMetadata;
import com.helospark.tactview.core.decoder.opencv.OpenCvImageDecorderDecorator;
import com.helospark.tactview.core.timeline.AddClipRequest;
import com.helospark.tactview.core.timeline.ClipFactory;
import com.helospark.tactview.core.timeline.TimelineClip;
import com.helospark.tactview.core.timeline.TimelinePosition;
import com.helospark.tactview.core.timeline.VisualMediaSource;
import com.helospark.tactview.core.timeline.image.ImageClip;

@Component
public class OpencvBasedImageClipFactory implements ClipFactory {
    private OpenCvImageDecorderDecorator mediaDecoder;

    public OpencvBasedImageClipFactory(OpenCvImageDecorderDecorator mediaDecoder) {
        this.mediaDecoder = mediaDecoder;
    }

    @Override
    public boolean doesSupport(AddClipRequest request) {
        try {
            return request.containsFile() &&
                    Files.probeContentType(request.getFile().toPath()).contains("image/");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public TimelineClip createClip(AddClipRequest request) {
        File file = request.getFile();
        TimelinePosition position = request.getPosition();
        VisualMediaSource mediaSource = new VisualMediaSource(file, mediaDecoder);
        ImageMetadata metadata = readMetadata(request.getFile());
        ImageClip result = new ImageClip(mediaSource, metadata, position, metadata.getLength());
        result.setCreatorFactoryId(getId());
        return result;
    }

    @Override
    public TimelineClip restoreClip(JsonNode savedClip) {
        File file = new File(savedClip.get("backingFile").asText());
        ImageMetadata metadata = readMetadata(file);
        VisualMediaSource videoSource = new VisualMediaSource(file, mediaDecoder);

        return new ImageClip(metadata, videoSource, savedClip);
    }

    @Override
    public ImageMetadata readMetadata(AddClipRequest request) {
        return readMetadata(request.getFile());
    }

    private ImageMetadata readMetadata(File file) {
        return mediaDecoder.readMetadata(file);
    }

    @Override
    public String getId() {
        return "opencvImageFactory";
    }

}
