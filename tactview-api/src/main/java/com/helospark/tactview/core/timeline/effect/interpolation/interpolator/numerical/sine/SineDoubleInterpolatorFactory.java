package com.helospark.tactview.core.timeline.effect.interpolation.interpolator.numerical.sine;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.helospark.tactview.core.save.LoadMetadata;
import com.helospark.tactview.core.save.SaveMetadata;
import com.helospark.tactview.core.util.DesSerFactory;
import com.helospark.tactview.core.util.SavedContentAddable;

public class SineDoubleInterpolatorFactory implements DesSerFactory<SineDoubleInterpolator> {

    @Override
    public void serializeInto(SineDoubleInterpolator instance, Map<String, Object> data, SaveMetadata saveMetadata) {
        data.put("frequency", instance.frequency);
        data.put("minValue", instance.minValue);
        data.put("maxValue", instance.maxValue);
        data.put("startOffset", instance.startOffset);

        data.put("initialFrequency", instance.initialFrequency);
        data.put("initialStartOffset", instance.initialStartOffset);
        data.put("initialMinValue", instance.initialMinValue);
        data.put("initialMaxValue", instance.initialMaxValue);
    }

    @Override
    public SineDoubleInterpolator deserialize(JsonNode data, SavedContentAddable<?> currentFieldValue, LoadMetadata loadMetadata) {
        try {
            double frequency = data.get("frequency").asDouble();
            double minValue = data.get("minValue").asDouble();
            double maxValue = data.get("maxValue").asDouble();
            double startOffset = data.get("startOffset").asDouble();

            SineDoubleInterpolator result = SineDoubleInterpolator.builder()
                    .withFrequency(frequency)
                    .withMaxValue(maxValue)
                    .withMinValue(minValue)
                    .withStartOffset(startOffset)
                    .build();

            result.initialFrequency = data.get("initialFrequency").asDouble();
            result.initialStartOffset = data.get("initialStartOffset").asDouble();
            result.initialMinValue = data.get("initialMinValue").asDouble();
            result.initialMaxValue = data.get("initialMaxValue").asDouble();

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
