package com.helospark.tactview.core.render.ffmpeg;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class FFmpegInitEncoderRequest extends Structure implements Structure.ByReference {
    public String fileName;
    public int width;
    public int height;
    public double framerate;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("fileName", "width", "height", "framerate");
    }
}