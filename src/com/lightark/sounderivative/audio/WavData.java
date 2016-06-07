package com.lightark.sounderivative.audio;

import java.io.File;
import java.io.IOException;

public class WavData
{
    private File file;

    private int numChannels;
    private long numFrames;
    private int validBits;
    private long sampleRate;

    private double[][][] mainWavData;
    private double[][] overflowWavData;

    public static WavData loadFromFile(File file)
    {
        return new WavData(file);
    }

    private WavData(File file)
    {
        this.file = file;
        try
        {
            WavFile wavFile = WavFile.openWavFile(file);

            System.out.println("------------------------------------------------------------------------------");
            System.out.println("WAV FILE");
            System.out.println("");
            wavFile.display();
            System.out.println("");

            numChannels = wavFile.getNumChannels();
            numFrames = wavFile.getNumFrames();
            validBits = wavFile.getValidBits();
            sampleRate = wavFile.getSampleRate();

            // Allocate memory for the sound data
            int requiredArrays = (int)Math.ceil((double)numFrames / (double)Integer.MAX_VALUE);
            int mainArrayCount = requiredArrays - 1;
            mainWavData = new double[mainArrayCount][numChannels][(requiredArrays > 1 ? Integer.MAX_VALUE : 0)];
            int overflowFrameCount = (int)(numFrames - (long)(mainArrayCount * Integer.MAX_VALUE));
            overflowWavData = new double[numChannels][overflowFrameCount];

            System.out.println("Beginning Processing...");
            long iterationCount = 0;

            double[][] inputBuffer = new double[numChannels][100];
            int framesRead;
            int arrayIndex = 0;
            int frameIndex = 0;
            do
            {
                // Read frames into buffer
                framesRead = wavFile.readFrames(inputBuffer, 100);

                for(int f = 0;f < framesRead;f++)
                {
                    for(int c = 0;c < numChannels;c++)
                    {
                        double sample = inputBuffer[c][f];

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

                iterationCount++;
            }
            while(framesRead != 0);
            System.out.println("...Finished Processing");
            System.out.println("------------------------------------------------------------------------------");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(WavFileException e)
        {
            e.printStackTrace();
        }
    }

    public long getNumFrames()
    {
        return numFrames;
    }

    public int getNumChannels()
    {
        return numChannels;
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
            count = mainWavData.length * Integer.MAX_VALUE + overflowWavData[0].length;
        }

        public boolean hasNext()
        {
            return frameIndex < (count - 1);
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
