package com.lightark.sounderivative.audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer implements Runnable
{
    private static final int BUFFER_SIZE = 4096;

    private boolean isPlaying = false;

    private WavData wavData;
    private Thread activeThread;
    private SourceDataLine activeSoundLine;

    public AudioPlayer()
    {

    }

    public boolean isPlaying()
    {
        return isPlaying;
    }

    public void playAudio(WavData wavData) throws LineUnavailableException
    {
        this.isPlaying = true;
        this.wavData = wavData;
        this.activeThread = new Thread(this);

        //Prepare audio format
        float sampleRate = wavData.getSampleRate();
        int sampleSizeInBits = wavData.getBytesPerSample() * 8;
        int channels = wavData.getNumChannels();
        boolean signed = true;
        boolean bigEndian = false;

        AudioFormat audioFormat = new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian
        );

        // Retrieve sound line
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, BUFFER_SIZE);
        activeSoundLine = (SourceDataLine) AudioSystem.getLine(info);

        // Open sound line
        activeSoundLine.open(audioFormat, BUFFER_SIZE);
        activeSoundLine.start();

        // Begin writing data into sound line buffer
        this.activeThread.start();
    }

    public void stopAudio()
    {
        if(isPlaying)
        {
            if(activeSoundLine != null)
            {
                activeSoundLine.stop();
            }
            if(activeThread != null && activeThread.isAlive())
            {
                activeThread.interrupt();
            }
        }
    }

    public long getFrameNumber()
    {
        if(!isPlaying) return 0;

        return activeSoundLine.getLongFramePosition();
    }

    @Override
    public void run()
    {
        InputStream is = wavData.asInputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        try
        {
            while((bytesRead = is.read(buffer)) != 0)
            {
                if(Thread.currentThread().isInterrupted())
                {
                    return;
                }
                activeSoundLine.write(buffer, 0, bytesRead);
            }
        }
        catch(IOException e1)
        {
            e1.printStackTrace();
        }
        finally
        {
            cleanup();
            try
            {
                is.close();
            }
            catch(IOException e1)
            {
                e1.printStackTrace();
            }
        }
    }

    private void cleanup()
    {
        activeSoundLine.flush();
        activeSoundLine = null;
        activeThread = null;
        wavData = null;
        this.isPlaying = false;
    }
}
