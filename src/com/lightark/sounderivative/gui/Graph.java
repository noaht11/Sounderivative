package com.lightark.sounderivative.gui;

public class Graph
{
    private int pixelWidth;
    private int pixelHeight;
    private double maxValue;
    private double minValue;
    private long numFrames;
    private long sampleRate;
    private boolean autoAmplify;

    private double zoom = 1.0;
    private int scroll = 0;

    private double framesPerPixel = 1.0;
    private double yScale;
    private double graphHeight;

    public Graph(int pixelWidth, int pixelHeight, double maxValue, double minValue, long numFrames, long sampleRate, boolean autoAmplify)
    {
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.numFrames = numFrames;
        this.sampleRate = sampleRate;
        this.autoAmplify = autoAmplify;

        recalculate();
    }

    public void setZoom(double zoom)
    {
        this.zoom = zoom;
        recalculate();
    }

    public void setScroll(int scroll)
    {
        this.scroll = scroll;
        recalculate();
    }

    private void recalculate()
    {
        double scaledWidth = pixelWidth * zoom;

        framesPerPixel = numFrames / scaledWidth;
        yScale = Math.max(Math.abs(maxValue), Math.abs(minValue));
        graphHeight = pixelHeight / 2.0;
    }

    public double getXCoordForFrameNumber(long frameNumber)
    {
        return (frameNumber / framesPerPixel) + scroll;
    }

    public double getXCoordForTime(long milliseconds)
    {
        double framesPerMillisecond = (double)sampleRate / 1000.0;
        return getXCoordForFrameNumber((long)(framesPerMillisecond * (double)milliseconds));
    }

    public double getYEqualsZero()
    {
        return graphHeight;
    }

    public double getYCoordForValue(double value)
    {
        return getYEqualsZero() + -(value * graphHeight / yScale);
    }
}
