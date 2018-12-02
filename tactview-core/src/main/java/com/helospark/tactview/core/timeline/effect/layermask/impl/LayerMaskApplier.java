package com.helospark.tactview.core.timeline.effect.layermask.impl;

import com.helospark.lightdi.annotation.Component;
import com.helospark.tactview.core.decoder.framecache.GlobalMemoryManagerAccessor;
import com.helospark.tactview.core.timeline.effect.scale.OpenCVScaleEffectImplementation;
import com.helospark.tactview.core.timeline.effect.scale.OpenCVScaleRequest;
import com.helospark.tactview.core.timeline.image.ClipImage;
import com.helospark.tactview.core.timeline.image.ReadOnlyClipImage;
import com.helospark.tactview.core.util.IndependentPixelOperation;

@Component
public class LayerMaskApplier {
    private IndependentPixelOperation independentPixelOperation;
    private OpenCVScaleEffectImplementation scaleImplementation;

    public LayerMaskApplier(IndependentPixelOperation independentPixelOperation, OpenCVScaleEffectImplementation scaleImplementation) {
        this.independentPixelOperation = independentPixelOperation;
        this.scaleImplementation = scaleImplementation;
    }

    public ClipImage createNewImageWithLayerMask(LayerMaskApplyRequest layerMaskRequest) {
        ReadOnlyClipImage mask = layerMaskRequest.getMask();
        ReadOnlyClipImage input = layerMaskRequest.getCurrentFrame();
        LayerMaskAlphaCalculator calculator = layerMaskRequest.getCalculator();

        ReadOnlyClipImage scaledMask = null;
        if (mask.getWidth() != input.getWidth() || mask.getHeight() != input.getHeight()) { // TODO: scale
            scaledMask = ClipImage.sameSizeAs(input);

            OpenCVScaleRequest request = new OpenCVScaleRequest();
            request.input = mask.getBuffer();
            request.output = scaledMask.getBuffer();
            request.originalWidth = mask.getWidth();
            request.originalHeight = mask.getHeight();
            request.newWidth = input.getWidth();
            request.newHeight = input.getHeight();

            scaleImplementation.scaleImage(request);
        }

        ReadOnlyClipImage maskToUse = (scaledMask == null ? mask : scaledMask);

        ClipImage result = ClipImage.sameSizeAs(input);

        independentPixelOperation.executePixelTransformation(input.getWidth(), input.getHeight(), (x, y) -> {
            int intensity = calculator.calculateAlpha(maskToUse, x, y);
            if (layerMaskRequest.isInvert()) {
                intensity = 255 - intensity;
            }
            for (int i = 0; i < 3; ++i) {
                int color = input.getColorComponentWithOffset(x, y, i);
                result.setColorComponentByOffset(color, x, y, i);
            }
            result.setAlpha(intensity, x, y);
        });
        if (scaledMask != null) {
            GlobalMemoryManagerAccessor.memoryManager.returnBuffer(scaledMask.getBuffer());
        }
        return result;
    }

}