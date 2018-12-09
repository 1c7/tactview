package com.helospark.tactview.core.timeline.effect;

import java.util.List;

import com.helospark.lightdi.annotation.Bean;
import com.helospark.lightdi.annotation.Configuration;
import com.helospark.tactview.core.timeline.TimelineClipType;
import com.helospark.tactview.core.timeline.TimelineInterval;
import com.helospark.tactview.core.timeline.TimelineLength;
import com.helospark.tactview.core.timeline.effect.blur.BlurEffect;
import com.helospark.tactview.core.timeline.effect.blur.BlurService;
import com.helospark.tactview.core.timeline.effect.blur.opencv.OpenCVBasedGaussianBlur;
import com.helospark.tactview.core.timeline.effect.cartoon.CartoonEffect;
import com.helospark.tactview.core.timeline.effect.cartoon.opencv.OpenCVCartoonEffectImplementation;
import com.helospark.tactview.core.timeline.effect.colorchannelchange.ColorChannelChangeEffect;
import com.helospark.tactview.core.timeline.effect.colorize.ColorBalanceEffect;
import com.helospark.tactview.core.timeline.effect.colorize.ColorizeEffect;
import com.helospark.tactview.core.timeline.effect.colorize.ColorizeService;
import com.helospark.tactview.core.timeline.effect.contractbrightness.BrightnessContrassEffect;
import com.helospark.tactview.core.timeline.effect.contractbrightness.BrignessContrastService;
import com.helospark.tactview.core.timeline.effect.crop.CropEffect;
import com.helospark.tactview.core.timeline.effect.denoise.DenoiseEffect;
import com.helospark.tactview.core.timeline.effect.denoise.opencv.OpenCVBasedDenoiseEffect;
import com.helospark.tactview.core.timeline.effect.desaturize.DesaturizeEffect;
import com.helospark.tactview.core.timeline.effect.displacementmap.DisplacementMapEffect;
import com.helospark.tactview.core.timeline.effect.edgedetect.EdgeDetectEffect;
import com.helospark.tactview.core.timeline.effect.edgedetect.opencv.OpenCVEdgeDetectImplementation;
import com.helospark.tactview.core.timeline.effect.erodedilate.ErodeDilateEffect;
import com.helospark.tactview.core.timeline.effect.erodedilate.opencv.OpenCVErodeDilateImplementation;
import com.helospark.tactview.core.timeline.effect.gamma.GammaEffect;
import com.helospark.tactview.core.timeline.effect.glow.LightGlowEffect;
import com.helospark.tactview.core.timeline.effect.greenscreen.GreenScreenEffect;
import com.helospark.tactview.core.timeline.effect.greenscreen.opencv.OpenCVGreenScreenImplementation;
import com.helospark.tactview.core.timeline.effect.histogramequization.HistogramEquizationEffect;
import com.helospark.tactview.core.timeline.effect.histogramequization.opencv.OpenCVHistogramEquizerImplementation;
import com.helospark.tactview.core.timeline.effect.invert.InvertEffect;
import com.helospark.tactview.core.timeline.effect.layermask.LayerMaskEffect;
import com.helospark.tactview.core.timeline.effect.layermask.impl.LayerMaskAlphaCalculator;
import com.helospark.tactview.core.timeline.effect.layermask.impl.LayerMaskApplier;
import com.helospark.tactview.core.timeline.effect.lut.LutEffect;
import com.helospark.tactview.core.timeline.effect.lut.LutProviderService;
import com.helospark.tactview.core.timeline.effect.mirror.MirrorEffect;
import com.helospark.tactview.core.timeline.effect.motionblur.GhostingEffect;
import com.helospark.tactview.core.timeline.effect.pencil.PencilSketchEffect;
import com.helospark.tactview.core.timeline.effect.pencil.opencv.OpenCVPencilSketchImplementation;
import com.helospark.tactview.core.timeline.effect.pixelize.PixelizeEffect;
import com.helospark.tactview.core.timeline.effect.rotate.OpenCVRotateEffectImplementation;
import com.helospark.tactview.core.timeline.effect.rotate.RotateEffect;
import com.helospark.tactview.core.timeline.effect.scale.ScaleEffect;
import com.helospark.tactview.core.timeline.effect.scale.service.ScaleService;
import com.helospark.tactview.core.timeline.effect.shadow.DropShadowEffect;
import com.helospark.tactview.core.timeline.effect.television.TelevisionRgbLinesEffect;
import com.helospark.tactview.core.timeline.effect.threshold.AdaptiveThresholdEffect;
import com.helospark.tactview.core.timeline.effect.threshold.SimpleThresholdEffect;
import com.helospark.tactview.core.timeline.effect.threshold.opencv.OpenCVThresholdImplementation;
import com.helospark.tactview.core.timeline.effect.vignette.VignetteEffect;
import com.helospark.tactview.core.timeline.effect.warp.TrigonometricWrapEffect;
import com.helospark.tactview.core.util.IndependentPixelOperation;
import com.helospark.tactview.core.util.messaging.MessagingService;

@Configuration
public class StandardEffectConfiguration {

    @Bean
    public StandardEffectFactory blurEffect(BlurService blurService, MessagingService messagingService) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new BlurEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(10000)), blurService))
                .withRestoreFactory(node -> new BlurEffect(node, blurService))
                .withName("Gaussian blur")
                .withSupportedEffectId("gaussianblur")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory desaturizeEffect(OpenCVBasedGaussianBlur gaussianBlur, IndependentPixelOperation independentPixelOperations) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new DesaturizeEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperations))
                .withRestoreFactory(node -> new DesaturizeEffect(node, independentPixelOperations))
                .withName("Desaturize")
                .withSupportedEffectId("desaturize")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory scaleEffect(ScaleService scaleService) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new ScaleEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), scaleService))
                .withRestoreFactory(node -> new ScaleEffect(node, scaleService))
                .withName("Scale")
                .withSupportedEffectId("scale")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory rotateEffect(OpenCVRotateEffectImplementation implementation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new RotateEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), implementation))
                .withRestoreFactory(node -> new RotateEffect(node, implementation))
                .withName("Rotate")
                .withSupportedEffectId("rotate")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory brightnessContrastEffect(BrignessContrastService brignessContrastService) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new BrightnessContrassEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), brignessContrastService))
                .withRestoreFactory(node -> new BrightnessContrassEffect(node, brignessContrastService))
                .withName("Brightness")
                .withSupportedEffectId("brightesscontrast")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory gammaEffect(IndependentPixelOperation independentPixelOperations) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new GammaEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperations))
                .withRestoreFactory(node -> new GammaEffect(node, independentPixelOperations))
                .withName("Gamma")
                .withSupportedEffectId("gamma")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory invertEffect(IndependentPixelOperation independentPixelOperations) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new InvertEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperations))
                .withRestoreFactory(node -> new InvertEffect(node, independentPixelOperations))
                .withName("Invert")
                .withSupportedEffectId("invert")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory denoiseEffect(OpenCVBasedDenoiseEffect openCVBasedDenoiseEffect) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new DenoiseEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), openCVBasedDenoiseEffect))
                .withRestoreFactory(node -> new DenoiseEffect(node, openCVBasedDenoiseEffect))
                .withName("Denoise")
                .withSupportedEffectId("denoise")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory adaptiveThresholdEffect(OpenCVThresholdImplementation openCVThresholdImplementation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new AdaptiveThresholdEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), openCVThresholdImplementation))
                .withRestoreFactory(node -> new AdaptiveThresholdEffect(node, openCVThresholdImplementation))
                .withName("Adaptive threshold")
                .withSupportedEffectId("adaptivethreshold")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory thresholdEffect(IndependentPixelOperation independentPixelOperation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new SimpleThresholdEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperation))
                .withRestoreFactory(node -> new SimpleThresholdEffect(node, independentPixelOperation))
                .withName("Threshold")
                .withSupportedEffectId("threshold")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory mirrorEffect(IndependentPixelOperation independentPixelOperation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new MirrorEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperation))
                .withRestoreFactory(node -> new MirrorEffect(node, independentPixelOperation))
                .withName("Mirror")
                .withSupportedEffectId("mirror")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory colorize(ColorizeService colorizeService) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new ColorizeEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), colorizeService))
                .withRestoreFactory(node -> new ColorizeEffect(node, colorizeService))
                .withName("Colorize")
                .withSupportedEffectId("colorize")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory pixelize(IndependentPixelOperation independentPixelOperation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new PixelizeEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperation))
                .withRestoreFactory(node -> new PixelizeEffect(node, independentPixelOperation))
                .withName("Pixelize")
                .withSupportedEffectId("pixelize")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory erodeDilate(OpenCVErodeDilateImplementation openCVErodeDilateImplementation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new ErodeDilateEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), openCVErodeDilateImplementation))
                .withRestoreFactory(node -> new ErodeDilateEffect(node, openCVErodeDilateImplementation))
                .withName("Erode/Dilate")
                .withSupportedEffectId("erodedilate")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory edgeDetect(OpenCVEdgeDetectImplementation openCVEdgeDetectImplementation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new EdgeDetectEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), openCVEdgeDetectImplementation))
                .withRestoreFactory(node -> new EdgeDetectEffect(node, openCVEdgeDetectImplementation))
                .withName("Edge detect")
                .withSupportedEffectId("edgedetect")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory greenScreen(OpenCVGreenScreenImplementation openCVGreenScreenImplementation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new GreenScreenEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), openCVGreenScreenImplementation))
                .withRestoreFactory(node -> new GreenScreenEffect(node, openCVGreenScreenImplementation))
                .withName("Green screen")
                .withSupportedEffectId("greenscreen")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory equizeHistogram(OpenCVHistogramEquizerImplementation implementation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new HistogramEquizationEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), implementation))
                .withRestoreFactory(node -> new HistogramEquizationEffect(node, implementation))
                .withName("Equize histogram")
                .withSupportedEffectId("equize histogram")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory cartoonEffect(OpenCVCartoonEffectImplementation implementation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new CartoonEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), implementation))
                .withRestoreFactory(node -> new CartoonEffect(node, implementation))
                .withName("Cartoon")
                .withSupportedEffectId("cartoon")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory pencilSketch(OpenCVPencilSketchImplementation implementation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new PencilSketchEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), implementation))
                .withRestoreFactory(node -> new PencilSketchEffect(node, implementation))
                .withName("Pencil")
                .withSupportedEffectId("pencil")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory warpEffect(IndependentPixelOperation independentPixelOperation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new TrigonometricWrapEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperation))
                .withRestoreFactory(node -> new TrigonometricWrapEffect(node, independentPixelOperation))
                .withName("Warp")
                .withSupportedEffectId("warp")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory televisionRgbLinesEffect(IndependentPixelOperation independentPixelOperation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new TelevisionRgbLinesEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperation))
                .withRestoreFactory(node -> new TelevisionRgbLinesEffect(node, independentPixelOperation))
                .withName("Television RGB")
                .withSupportedEffectId("televisionrgb")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory lightGlowEffect(IndependentPixelOperation independentPixelOperation, OpenCVBasedGaussianBlur blueImplementation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new LightGlowEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), blueImplementation, independentPixelOperation))
                .withRestoreFactory(node -> new LightGlowEffect(node, blueImplementation, independentPixelOperation))
                .withName("Light glow")
                .withSupportedEffectId("lightglow")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory vignetteEffect(IndependentPixelOperation independentPixelOperation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new VignetteEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperation))
                .withRestoreFactory(node -> new VignetteEffect(node, independentPixelOperation))
                .withName("Vignette")
                .withSupportedEffectId("vignette")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory colorChannelChangeEffect(IndependentPixelOperation independentPixelOperation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new ColorChannelChangeEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperation))
                .withRestoreFactory(node -> new ColorChannelChangeEffect(node, independentPixelOperation))
                .withName("Colorchannel change")
                .withSupportedEffectId("colorchannelchange")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory displacementMapEffect(ScaleService scaleService, IndependentPixelOperation independentPixelOperation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new DisplacementMapEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), scaleService, independentPixelOperation))
                .withRestoreFactory(node -> new DisplacementMapEffect(node, scaleService, independentPixelOperation))
                .withName("Displacement map")
                .withSupportedEffectId("displacementmap")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory layerMaskEffect(LayerMaskApplier layerMaskApplier, List<LayerMaskAlphaCalculator> calculators) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new LayerMaskEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), layerMaskApplier, calculators))
                .withRestoreFactory(node -> new LayerMaskEffect(node, layerMaskApplier, calculators))
                .withName("Layer mask")
                .withSupportedEffectId("layermaskeffect")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory cropEffect(IndependentPixelOperation independentPixelOperation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new CropEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperation))
                .withRestoreFactory(node -> new CropEffect(node, independentPixelOperation))
                .withName("Crop")
                .withSupportedEffectId("cropeffect")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory colorBalanceEffect(IndependentPixelOperation independentPixelOperation, BrignessContrastService brignessContrastService, ColorizeService colorizeService) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new ColorBalanceEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperation, brignessContrastService,
                        colorizeService))
                .withRestoreFactory(node -> new ColorBalanceEffect(node, independentPixelOperation, brignessContrastService, colorizeService))
                .withName("Color balance")
                .withSupportedEffectId("colorbalance")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory lutEffect(IndependentPixelOperation independentPixelOperation, LutProviderService lutProviderService) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new LutEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperation, lutProviderService))
                .withRestoreFactory(node -> new LutEffect(node, independentPixelOperation, lutProviderService))
                .withName("LUT")
                .withSupportedEffectId("lut")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory ghostingEffect(IndependentPixelOperation independentPixelOperation) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new GhostingEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperation))
                .withRestoreFactory(node -> new GhostingEffect(node, independentPixelOperation))
                .withName("ghosting")
                .withSupportedEffectId("ghosting")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

    @Bean
    public StandardEffectFactory dropShadowEffect(IndependentPixelOperation independentPixelOperation, BlurService blurService) {
        return StandardEffectFactory.builder()
                .withFactory(request -> new DropShadowEffect(new TimelineInterval(request.getPosition(), TimelineLength.ofMillis(5000)), independentPixelOperation, blurService))
                .withRestoreFactory(node -> new DropShadowEffect(node, independentPixelOperation, blurService))
                .withName("Drop Shadow")
                .withSupportedEffectId("dropshadow")
                .withSupportedClipTypes(List.of(TimelineClipType.VIDEO, TimelineClipType.IMAGE))
                .build();
    }

}
