package com.lightark.sounderivative.audio;

public interface WavProcessingListener
{
    void progressUpdate(long framesProcessed, long numFrames);
}