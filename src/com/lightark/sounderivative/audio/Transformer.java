package com.lightark.sounderivative.audio;

import java.util.Arrays;

public abstract class Transformer implements BufferedInput
{
    private WavData source;
    private WavData.WavDataIterator iterator;

    private Transformer(WavData data)
    {
        this.source = data;
        this.iterator = source.createIterator();
    }

    protected WavData getSource()
    {
        return source;
    }

    protected WavData.WavDataIterator getIterator()
    {
        return iterator;
    }

    public long getNumFrames()
    {
        return source.getNumFrames();
    }

    public int getNumChannels()
    {
        return source.getNumChannels();
    }

    public long getSampleRate()
    {
        return source.getSampleRate();
    }

    public int getValidBits()
    {
        return source.getValidBits();
    }

    public static class Differentiator extends Transformer
    {
        protected double deltaT;
        protected int numChannels;

        protected double[] previousSamples;

        public Differentiator(WavData data)
        {
            super(data);

            deltaT = 1.0 / getSource().getSampleRate();
            numChannels = getSource().getNumChannels();

            previousSamples = new double[numChannels];
            Arrays.fill(previousSamples, Double.NaN);
        }

        @Override
        public long getNumFrames()
        {
            return super.getNumFrames() - 1;
        }

        @Override
        public int read(double[][] buffer) throws Exception
        {
            int outputFrameCount = 0;

            WavData.WavDataIterator iterator = getIterator();

            while(iterator.hasNext() && outputFrameCount < buffer.length)
            {
                double[] frame = iterator.nextFrame();

                boolean outputWritten = false;
                for(int c = 0;c < numChannels;c++)
                {
                    double previousSample = previousSamples[c];
                    double sample = frame[c];
                    if(!Double.isNaN(previousSample))
                    {
                        double outputSample = calculateOutput(sample, previousSample);
                        buffer[c][outputFrameCount] = outputSample;
                        outputWritten = true;
                    }

                    previousSamples[c] = sample;
                }

                if(outputWritten)
                {
                    outputFrameCount++;
                }
            }

            return outputFrameCount;
        }

        protected double calculateOutput(double sample, double previousSample)
        {
            double dx = deltaT;
            double dy = sample - previousSample;

            return dy / dx;
        }
    }

    public static class Integrator extends Differentiator
    {
        private double accumulatedArea = 0;

        public Integrator(WavData data)
        {
            super(data);
        }

        @Override
        protected double calculateOutput(double sample, double previousSample)
        {
            double dx = deltaT;
            accumulatedArea += ((sample + previousSample) / 2) * dx;
            return accumulatedArea;
        }
    }
}
