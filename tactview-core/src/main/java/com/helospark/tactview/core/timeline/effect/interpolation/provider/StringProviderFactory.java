package com.helospark.tactview.core.timeline.effect.interpolation.provider;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.helospark.tactview.core.api.LoadMetadata;
import com.helospark.tactview.core.timeline.effect.interpolation.AbstractKeyframeableEffectDesSerFactory;
import com.helospark.tactview.core.timeline.effect.interpolation.interpolator.StringInterpolator;
import com.helospark.tactview.core.util.ReflectionUtil;

public class StringProviderFactory extends AbstractKeyframeableEffectDesSerFactory<StringProvider> {

    @Override
    public void addDataForDeserializeInternal(StringProvider instance, Map<String, Object> data) {
        data.put("interpolator", instance.stringInterpolator);
    }

    @Override
    public StringProvider deserializeInternal(JsonNode data, StringProvider currentFieldValue, LoadMetadata loadMetadata) {
        return new StringProvider(ReflectionUtil.deserialize(data.get("interpolator"), StringInterpolator.class, currentFieldValue.stringInterpolator, loadMetadata));
    }

}
