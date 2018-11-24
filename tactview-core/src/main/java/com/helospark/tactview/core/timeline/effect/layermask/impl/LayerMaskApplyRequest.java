package com.helospark.tactview.core.timeline.effect.layermask.impl;

import javax.annotation.Generated;

import com.helospark.tactview.core.timeline.ClipFrameResult;

public class LayerMaskApplyRequest {
    private ClipFrameResult currentFrame;
    private ClipFrameResult mask;
    private LayerMaskAlphaCalculator calculator;
    private boolean scaleLayerMask;
    private boolean invert;

    @Generated("SparkTools")
    private LayerMaskApplyRequest(Builder builder) {
        this.currentFrame = builder.currentFrame;
        this.mask = builder.mask;
        this.calculator = builder.calculator;
        this.scaleLayerMask = builder.scaleLayerMask;
        this.invert = builder.invert;
    }

    public boolean isInvert() {
        return invert;
    }

    public ClipFrameResult getCurrentFrame() {
        return currentFrame;
    }

    public ClipFrameResult getMask() {
        return mask;
    }

    public LayerMaskAlphaCalculator getCalculator() {
        return calculator;
    }

    public boolean isScaleLayerMask() {
        return scaleLayerMask;
    }

    @Override
    public String toString() {
        return "LayerMaskApplyRequest [currentFrame=" + currentFrame + ", mask=" + mask + ", calculator=" + calculator + ", scaleLayerMask=" + scaleLayerMask + ", invert=" + invert + "]";
    }

    @Generated("SparkTools")
    public static Builder builder() {
        return new Builder();
    }

    @Generated("SparkTools")
    public static final class Builder {
        private ClipFrameResult currentFrame;
        private ClipFrameResult mask;
        private LayerMaskAlphaCalculator calculator;
        private boolean scaleLayerMask;
        private boolean invert;

        private Builder() {
        }

        public Builder withCurrentFrame(ClipFrameResult currentFrame) {
            this.currentFrame = currentFrame;
            return this;
        }

        public Builder withMask(ClipFrameResult mask) {
            this.mask = mask;
            return this;
        }

        public Builder withCalculator(LayerMaskAlphaCalculator calculator) {
            this.calculator = calculator;
            return this;
        }

        public Builder withScaleLayerMask(boolean scaleLayerMask) {
            this.scaleLayerMask = scaleLayerMask;
            return this;
        }

        public Builder withInvert(boolean invert) {
            this.invert = invert;
            return this;
        }

        public LayerMaskApplyRequest build() {
            return new LayerMaskApplyRequest(this);
        }
    }

}
