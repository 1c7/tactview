package com.helospark.tactview.ui.javafx.repository.drag;

import com.helospark.tactview.core.timeline.TimelinePosition;

import javafx.scene.Parent;

public class ClipDragInformation {
    private Parent node;
    private TimelinePosition originalPosition;
    private String clipId;
    private String originalChannelId;
    private double anchorPointX;

    public ClipDragInformation(Parent node, TimelinePosition originalPosition, String clipId, String originalChannelId, double anchorPointX) {
        this.node = node;
        this.originalPosition = originalPosition;
        this.clipId = clipId;
        this.originalChannelId = originalChannelId;
        this.anchorPointX = anchorPointX;

    }

    public Parent getNode() {
        return node;
    }

    public TimelinePosition getOriginalPosition() {
        return originalPosition;
    }

    public String getClipId() {
        return clipId;
    }

    public String getOriginalChannelId() {
        return originalChannelId;
    }

    public double getAnchorPointX() {
        return anchorPointX;
    }

}
