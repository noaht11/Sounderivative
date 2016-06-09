package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.audio.WavData;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

public class GraphPanel extends JPanel
{
    private WavData wavData;

    private Color graphColor;

    public GraphPanel(WavData wavData, Color graphColor)
    {
        setBackground(Color.WHITE);

        this.wavData = wavData;
        this.graphColor = graphColor;
    }

    private int calculateYEquals0(int channelIndex, int heightPerChannel)
    {
        return (2 * channelIndex + 1) * (heightPerChannel / 2);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);

        if(wavData == null)
        {
            return;
        }

        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(graphColor);

        // x
        int pixelWidth = getWidth();
        long pixelsPerFrame = pixelWidth / wavData.getNumFrames();
        long framesPerPixel = wavData.getNumFrames() / pixelWidth;

        // y
        int pixelHeight = getHeight();
        int heightPerChannel = pixelHeight / wavData.getNumChannels();

        if(pixelsPerFrame == 0)
        {
            // We can't even fit one pixel per frame so we have to use multiple frames for each pixel
        }

        WavData.WavDataIterator iterator = wavData.createIterator();
        int xValueCount = 0;

        GeneralPath[] paths = new GeneralPath[wavData.getNumChannels()];
        for(int i = 0;i < paths.length;i++)
        {
            paths[i] = new GeneralPath();
            paths[i].moveTo(0, calculateYEquals0(i, heightPerChannel));
        }

        while(iterator.hasNext())
        {
            double[] sample = iterator.nextFrame();

            // Only capture every (framesPerPixel)th value
            if(iterator.currentIndex() % framesPerPixel != 0)
            {
                continue;
            }

            /*if(iterator.currentIndex() > 1000)
            {
                break;
            }*/

            int channelIndex = 0;
            for(double channelValue : sample)
            {
                // x
                long xValue = xValueCount;
                int xCoord = (int)xValue;

                // y
                int yValue = (int)(channelValue * (double)heightPerChannel);
                int yCoord = calculateYEquals0(channelIndex, heightPerChannel) + -yValue;

                paths[channelIndex].lineTo(xCoord, yCoord);

                channelIndex++;
            }
            xValueCount++;
        }

        for(GeneralPath path : paths)
        {
            g2.draw(path);
        }
    }
}
