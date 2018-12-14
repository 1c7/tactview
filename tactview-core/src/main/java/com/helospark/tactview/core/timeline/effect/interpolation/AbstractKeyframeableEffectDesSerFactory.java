package com.helospark.tactview.core.timeline.effect.interpolation;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.helospark.tactview.core.api.LoadMetadata;
import com.helospark.tactview.core.util.DesSerFactory;
import com.helospark.tactview.core.util.SavedContentAddable;

public abstract class AbstractKeyframeableEffectDesSerFactory<T extends KeyframeableEffect> implements DesSerFactory<T> {

    @Override
    public void addDataForDeserialize(T instance, Map<String, Object> data) {
        data.put("id", instance.getId());
        addDataForDeserializeInternal(instance, data);
    }

    @Override
    public T deserialize(JsonNode data, SavedContentAddable<?> currentFieldValue, LoadMetadata loadMetadata) {
        T result = deserializeInternal(data, (T) currentFieldValue, loadMetadata);
        KeyframeableEffect current = (KeyframeableEffect) currentFieldValue;
        result.id = data.get("id").asText();
        return result;
    }

    protected abstract void addDataForDeserializeInternal(T instance, Map<String, Object> data);

    protected abstract T deserializeInternal(JsonNode data, T currentFieldValue, LoadMetadata loadMetadata);
}
