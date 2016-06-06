package com.lightark.sounderivative;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;

public class Sounderivative
{
    private static final int MODE_DERIVATIVE = 1;
    private static final int MODE_INTEGRAL = 2;

    private static final int RESPECT_TIME = 1;
    private static final int RESPECT_SAMPLE_NUMBER = 2;

    // Config
    private static final boolean DEBUG = false;
    private static int MODE;
    private static final int WITH_RESPECT_TO = RESPECT_SAMPLE_NUMBER;

    public static void main(String[] args)
    {
        MODE = Integer.parseInt(args[0]);

        try
        {
            File inputFile = new File(args[1]);
            File outputFile = new File(args[2]);

            // Prepare the input file
            WavFile inputWavFile = WavFile.openWavFile(inputFile);
            System.out.println("Reading file: " + inputFile.getName());
            inputWavFile.display();
            System.out.println("");
            int numChannels = inputWavFile.getNumChannels();
            long numFrames = inputWavFile.getNumFrames();
            int validBits = inputWavFile.getValidBits();
            long sampleRate = inputWavFile.getSampleRate();
            double[][] inputBuffer = new double[numChannels][100];
            int framesRead;

            // Prepare the output file
            WavFile outputWavFile = WavFile.newWavFile(outputFile, numChannels, numFrames - 1, validBits, sampleRate);
            double[][] outputBuffer = new double[numChannels][100];

            System.out.println("Beginning Processing...");
            int iterationCount = 0;

            double deltaT = 1.0 / sampleRate;

            double[] previousSamples = new double[numChannels];
            Arrays.fill(previousSamples, Double.NaN);

            double accumulatedArea = 0;
            int totalOutputFrameCount = 0;
            do
            {
                System.out.println("Processing frames: " + iterationCount*100 + "-" + (iterationCount+1)*100);
                System.out.println("");

                // Read frames into buffer
                framesRead = inputWavFile.readFrames(inputBuffer, 100);

                int outputFrameCount = 0;

                for(int f = 0;f < framesRead;f++)
                {
                    boolean outputWritten = false;
                    for(int c = 0;c < numChannels;c++)
                    {
                        double previousSample = previousSamples[c];
                        double sample = inputBuffer[c][f];
                        if(!Double.isNaN(previousSample))
                        {
                            double dx;
                            switch(WITH_RESPECT_TO)
                            {
                                case RESPECT_TIME:
                                    dx = deltaT;
                                    break;
                                case RESPECT_SAMPLE_NUMBER:
                                default:
                                    dx = 1.0;
                                    break;
                            }

                            double dy = sample - previousSample;

                            double outputSample = sample;
                            switch(MODE)
                            {
                                case MODE_DERIVATIVE:
                                    outputSample = dy / dx;
                                    break;
                                case MODE_INTEGRAL:
                                    accumulatedArea += dy * dx;
                                    outputSample = accumulatedArea;
                                    break;
                            }

                            if(DEBUG)
                            {
                                System.out.println("Previous sample: " + new BigDecimal(previousSample).toPlainString());
                                System.out.println("Sample: " + new BigDecimal(sample).toPlainString());
                                System.out.println("dx: " + new BigDecimal(dx).toPlainString());
                                System.out.println("dy: " + new BigDecimal(dy).toPlainString());
                                System.out.println("Output sample: " + new BigDecimal(outputSample).toPlainString());
                                System.out.println("");
                            }

                            outputBuffer[c][outputFrameCount] = outputSample;
                            outputWritten = true;
                        }

                        previousSamples[c] = sample;
                    }

                    if(outputWritten)
                    {
                        outputFrameCount++;
                    }
                }

                outputWavFile.writeFrames(outputBuffer, outputFrameCount);
                totalOutputFrameCount += outputFrameCount;
                iterationCount++;

                if(DEBUG)
                {
                    break;
                }
            }
            while(framesRead != 0);
            System.out.println("Processing complete");

            System.out.println("Frames read: " + numFrames);
            System.out.println("Frames written: " + totalOutputFrameCount);

            // Close the files
            inputWavFile.close();
            outputWavFile.close();

            System.out.println("Output saved to: " + outputFile.getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
