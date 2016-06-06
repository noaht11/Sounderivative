package com.lightark.sounderivative;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;

public class Sounderivative
{
    private static final boolean DEBUG = false;

    private static final int RESPECT_TIME = 1;
    private static final int RESPECT_SAMPLE_NUMBER = 2;

    private static final int WITH_RESPECT_TO = RESPECT_SAMPLE_NUMBER;

    public static void main(String[] args)
    {
        try
        {
            File inputFile = new File(args[0]);
            File outputFile = new File(args[1]);

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
            WavFile outputWavFile = WavFile.newWavFile(outputFile, numChannels, numFrames, validBits, sampleRate);
            double[][] outputBuffer = new double[numChannels][100];

            System.out.println("Beginning Processing...");
            int iterationCount = 0;

            double[] previousSamples = new double[numChannels];
            Arrays.fill(previousSamples, Double.NaN);
            double deltaT = 1.0 / sampleRate;
            do
            {
                System.out.println("Processing frames: " + iterationCount*100 + "-" + (iterationCount+1)*100);

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
                                case RESPECT_SAMPLE_NUMBER:
                                default:
                                    dx = 1.0;
                            }

                            double derivative = (sample - previousSample) / dx;
                            outputBuffer[c][outputFrameCount] = derivative;
                            outputWritten = true;

                            if(DEBUG)
                            {
                                System.out.println("Previous sample: " + new BigDecimal(previousSample).toPlainString());
                                System.out.println("Sample: " + new BigDecimal(sample).toPlainString());
                                System.out.println("Delta T: " + new BigDecimal(deltaT).toPlainString());
                                System.out.println("Derivative: " + new BigDecimal(derivative).toPlainString());
                                System.out.println("");
                            }
                        }
                        previousSamples[c] = sample;
                    }

                    if(outputWritten)
                    {
                        outputFrameCount++;
                    }
                }

                outputWavFile.writeFrames(outputBuffer, outputFrameCount);
                iterationCount++;

                if(DEBUG)
                {
                    break;
                }
            }
            while(framesRead != 0);
            System.out.println("Processing complete");

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
