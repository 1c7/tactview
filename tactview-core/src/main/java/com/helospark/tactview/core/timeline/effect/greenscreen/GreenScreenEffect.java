package com.helospark.tactview.core.timeline.effect.greenscreen;

import java.util.Collections;
import java.util.List;

import com.helospark.tactview.core.timeline.StatelessEffect;
import com.helospark.tactview.core.timeline.StatelessVideoEffect;
import com.helospark.tactview.core.timeline.TimelineInterval;
import com.helospark.tactview.core.timeline.effect.StatelessEffectRequest;
import com.helospark.tactview.core.timeline.effect.greenscreen.opencv.OpenCVGreenScreenImplementation;
import com.helospark.tactview.core.timeline.effect.greenscreen.opencv.OpenCVGreenScreenRequest;
import com.helospark.tactview.core.timeline.effect.interpolation.ValueProviderDescriptor;
import com.helospark.tactview.core.timeline.image.ClipImage;
import com.helospark.tactview.core.timeline.image.ReadOnlyClipImage;
import com.helospark.tactview.core.util.ReflectionUtil;

public class GreenScreenEffect extends StatelessVideoEffect {
    private OpenCVGreenScreenImplementation implementation;

    public GreenScreenEffect(TimelineInterval interval, OpenCVGreenScreenImplementation implementation) {
        super(interval);
        this.implementation = implementation;
    }

    public GreenScreenEffect(GreenScreenEffect cloneFrom) {
        super(cloneFrom);
        ReflectionUtil.copyOrCloneFieldFromTo(cloneFrom, this);
    }

    @Override
    public ClipImage createFrame(StatelessEffectRequest request) {
        ReadOnlyClipImage currentFrame = request.getCurrentFrame();
        ClipImage result = ClipImage.sameSizeAs(currentFrame);

        OpenCVGreenScreenRequest nativeRequest = new OpenCVGreenScreenRequest();
        nativeRequest.input = currentFrame.getBuffer();
        nativeRequest.output = result.getBuffer();
        nativeRequest.width = currentFrame.getWidth();
        nativeRequest.height = currentFrame.getHeight();

        implementation.greenScreen(nativeRequest);

        return result;
    }

    @Override
    public List<ValueProviderDescriptor> getValueProviders() {
        return Collections.emptyList();
    }

    @Override
    public StatelessEffect cloneEffect() {
        return new GreenScreenEffect(this);
    }

}
