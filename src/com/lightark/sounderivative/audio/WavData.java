package com.lightark.sounderivative.audio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class WavData
{
    private static final int BUFFER_SIZE = 4096;

    private int numChannels;
    private long numFrames;
    private long sampleRate;
    private int validBits;
    private int bytesPerSample;
    private int blockAlign;

    private double[][][] mainWavData;
    private double[][] overflowWavData;

    private double maxValue;
    private double minValue;

    public static WavData fromFile(File file, WavProcessingListener listener) throws Exception
    {
        return new WavData(file, listener);
    }

    public static WavData fromExisting(Transformer transformer, WavProcessingListener listener) throws Exception
    {
        return new WavData(
                transformer.getNumChannels(),
                transformer.getNumFrames(),
                transformer.getSampleRate(),
                transformer.getValidBits(),
                transformer,
                listener
        );
    }

    private WavData(int numChannels, long numFrames, long sampleRate, int validBits, BufferedInput input, WavProcessingListener listener) throws Exception
    {
        this.numChannels = numChannels;
        this.numFrames = numFrames;
        this.sampleRate = sampleRate;
        this.validBits = validBits;
        this.bytesPerSample = (validBits + 7) / 8;
        this.blockAlign = bytesPerSample * numChannels;

        allocateArrays();

        System.out.println("------------------------------------------------------------------------------");
        System.out.println("WAV DATA");
        System.out.println("");
        System.out.println("Using input: " + input.getClass().getName());
        System.out.println("");
        System.out.println("Beginning Processing...");

        readData(input, listener);

        System.out.println("...Finished Processing");
        System.out.println("------------------------------------------------------------------------------");
    }

    private WavData(File file, WavProcessingListener listener) throws Exception
    {
        final WavFile wavFile = WavFile.openWavFile(file);

        System.out.println("------------------------------------------------------------------------------");
        System.out.println("WAV FILE");
        System.out.println("");
        wavFile.display();
        System.out.println("");

        numChannels = wavFile.getNumChannels();
        numFrames = wavFile.getNumFrames();
        sampleRate = wavFile.getSampleRate();
        validBits = wavFile.getValidBits();
        bytesPerSample = (validBits + 7) / 8;
        blockAlign = bytesPerSample * numChannels;

        allocateArrays();

        System.out.println("Beginning Processing...");

        readData(new BufferedInput()
        {
            @Override
            public int read(double[][] buffer) throws Exception
            {
                return wavFile.readFrames(buffer, buffer.length);
            }
        }, listener);

        wavFile.close();

        System.out.println("...Finished Processing");
        System.out.println("------------------------------------------------------------------------------");
    }

    private void allocateArrays()
    {
        // Allocate memory for the sound data
        int requiredArrays = (int)Math.ceil((double)numFrames / (double)Integer.MAX_VALUE);
        int mainArrayCount = requiredArrays - 1;
        mainWavData = new double[mainArrayCount][numChannels][(requiredArrays > 1 ? Integer.MAX_VALUE : 0)];
        int overflowFrameCount = (int)(numFrames - (long)(mainArrayCount * Integer.MAX_VALUE));
        overflowWavData = new double[numChannels][overflowFrameCount];
    }

    private void readData(BufferedInput input, WavProcessingListener listener) throws Exception
    {
        double[][] inputBuffer = new double[numChannels][BUFFER_SIZE];
        int framesRead;
        int totalFramesRead = 0;
        int arrayIndex = 0;
        int frameIndex = 0;
        do
        {
            // Read frames into buffer
            framesRead = input.read(inputBuffer);

            for(int f = 0;f < framesRead;f++)
            {
                for(int c = 0;c < numChannels;c++)
                {
                    double sample = inputBuffer[c][f];

                    // Check max/min
                    if(Double.isNaN(maxValue) || sample > maxValue)
                    {
                        maxValue = sample;
                    }
                    if(Double.isNaN(minValue) || sample < minValue)
                    {
                        minValue = sample;
                    }

                    if(arrayIndex >= mainWavData.length)
                    {
                        overflowWavData[c][frameIndex] = sample;
                    }
                    else
                    {
                        mainWavData[arrayIndex][c][frameIndex] = sample;
                    }
                }
                frameIndex++;
                if(frameIndex == Integer.MAX_VALUE)
                {
                    frameIndex = 0;
                    arrayIndex++;
                }
            }

            totalFramesRead += framesRead;
            listener.progressUpdate(totalFramesRead, numFrames);
        }
        while(framesRead != 0);
    }

    public long getNumFrames()
    {
        return numFrames;
    }

    public int getNumChannels()
    {
        return numChannels;
    }

    public long getSampleRate()
    {
        return sampleRate;
    }

    public int getValidBits()
    {
        return validBits;
    }

    public int getBytesPerSample()
    {
        return bytesPerSample;
    }

    public int getBlockAlign()
    {
        return blockAlign;
    }

    public double getMaxValue()
    {
        return maxValue;
    }

    public double getMinValue()
    {
        return minValue;
    }

    public void exportToFile(File file, WavProcessingListener listener) throws IOException, WavFileException
    {
        WavFile outputWavFile = WavFile.newWavFile(file, numChannels, numFrames, validBits, sampleRate);

        int outputBufferSize = BUFFER_SIZE;
        double[][] outputBuffer = new double[numChannels][outputBufferSize];

        int outputBufferCount = 0;
        long totalOutputFrameCount = 0;
        WavDataIterator iterator = createIterator();
        while(iterator.hasNext())
        {
            double[] frame = iterator.nextFrame();
            for(int c = 0;c < frame.length;c++)
            {
                outputBuffer[c][outputBufferCount] = frame[c];
            }
            outputBufferCount++;

            if(outputBufferCount == outputBufferSize)
            {
                totalOutputFrameCount += outputBufferCount;

                outputWavFile.writeFrames(outputBuffer, outputBufferCount);

                outputBufferCount = 0;
                listener.progressUpdate(totalOutputFrameCount, numFrames);
            }
        }

        outputWavFile.writeFrames(outputBuffer, outputBufferCount);

        totalOutputFrameCount += outputBufferCount;
        listener.progressUpdate(totalOutputFrameCount, numFrames);

        outputWavFile.close();
    }

    public InputStream asInputStream()
    {
        return new WavDataInputStream();
    }

    public class WavDataInputStream extends InputStream
    {
        private WavDataIterator iterator;
        double floatOffset;
        double floatScale;

        public WavDataInputStream()
        {
            iterator = createIterator();

            // Calculate conversion factors
            if (validBits > 8)
            {
                // If more than 8 validBits, data is signed
                // Conversion required multiplying by magnitude of max positive value
                floatOffset = 0;
                floatScale = Long.MAX_VALUE >> (64 - validBits);
            }
            else
            {
                // Else if 8 or less validBits, data is unsigned
                // Conversion required dividing by max positive value
                floatOffset = 1;
                floatScale = 0.5 * ((1 << validBits) - 1);
            }
        }

        @Override
        public int read() throws IOException
        {
            return 0;
        }

        @Override
        public int read(byte[] b) throws IOException
        {
            if(b.length % blockAlign != 0)
            {
                throw new IOException("Buffer must have a size that is a multiple of blockAlign: " + blockAlign);
            }

            int framesToRead = b.length / blockAlign;
            int framesRead = 0;
            int bufferPointer = 0;
            while(iterator.hasNext() && framesRead < framesToRead)
            {
                double[] frame = iterator.nextFrame();

                for(double sample : frame)
                {
                    long longSample = (long) (floatScale * (floatOffset + sample));

                    for (int byteIndex = 0;byteIndex < bytesPerSample;byteIndex++)
                    {
                        b[bufferPointer] = (byte)(longSample & 0xFF);
                        longSample >>= 8;
                        bufferPointer++;
                    }
                }

                framesRead++;
            }

            return framesRead * blockAlign;
        }
    }

    public WavDataIterator createIterator()
    {
        return this.new WavDataIterator();
    }

    public class WavDataIterator
    {
        private int arrayIndex = 0;
        private long frameIndex = 0;

        private long count;

        private WavDataIterator()
        {
            //count = mainWavData.length * Integer.MAX_VALUE + overflowWavData[0].length;
            count = getNumFrames();
        }

        public boolean hasNext()
        {
            return frameIndex < count;
        }

        private boolean isOnOverflow()
        {
            return arrayIndex >= mainWavData.length;
        }

        private int getIndexInCurrentArray()
        {
            return (int)(frameIndex % (long)Integer.MAX_VALUE);
        }

        public double[] nextFrame()
        {
            if(!hasNext()) throw new RuntimeException("No more frames");

            int indexInCurrentArray = getIndexInCurrentArray();
            double[] sample = new double[numChannels];
            if(isOnOverflow())
            {
                for(int c = 0;c < numChannels;c++)
                {
                    sample[c] = overflowWavData[c][indexInCurrentArray];
                }
            }
            else
            {
                for(int c = 0;c < numChannels;c++)
                {
                    sample[c] = mainWavData[arrayIndex][c][indexInCurrentArray];
                }
            }

            frameIndex++;
            if(getIndexInCurrentArray() == 0)
            {
                arrayIndex++;
            }

            return sample;
        }

        public long currentIndex()
        {
            return frameIndex;
        }

        public long getCount()
        {
            return count;
        }
    }
}
