package com.helospark.tactview.core.timeline.proceduralclip;

import com.fasterxml.jackson.databind.JsonNode;
import com.helospark.tactview.core.timeline.AddClipRequest;
import com.helospark.tactview.core.timeline.TimelineClip;

public interface ProceduralClipFactoryChainItem {

    public ProceduralVisualClip create(AddClipRequest request);

    public TimelineClip restoreClip(JsonNode node);

    public boolean doesSupport(AddClipRequest request);

    public String getProceduralClipName();

    public String getProceduralClipId();

    public String getId();
}
