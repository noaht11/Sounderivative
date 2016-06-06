package com.lightark.sounderivative;

import java.io.File;
import java.math.BigDecimal;

public class Sounderivative
{
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
            do
            {
                // Read frames into buffer
                System.out.println("Processing frames: " + iterationCount*100 + "-" + (iterationCount+1)*100);
                framesRead = inputWavFile.readFrames(inputBuffer, 100);

                for(int f = 0;f < framesRead;f++)
                {
                    for(int c = 0;c < numChannels;c++)
                    {
                        double sample = inputBuffer[c][f];
                        outputBuffer[c][f] = sample/6;
                    }
                }

                outputWavFile.writeFrames(outputBuffer, framesRead);
                iterationCount++;
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
            System.err.println(e);
        }
    }
}
