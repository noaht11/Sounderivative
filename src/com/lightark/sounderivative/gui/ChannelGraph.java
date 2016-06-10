package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.audio.WavData;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

public class ChannelGraph extends JPanel
{
    private WavData wavData;
    private int channelIndex;

    private Color graphColor;
    private float zoom = 1.0f;
    private int scroll = 0;

    public ChannelGraph(WavData wavData, int channelIndex, Color graphColor)
    {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));

        this.wavData = wavData;
        this.channelIndex = channelIndex;
        this.graphColor = graphColor;
    }

    public void setZoom(float zoom)
    {
        this.zoom = zoom;
    }

    public void setScroll(int scroll)
    {
        this.scroll = scroll;
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
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2.setColor(graphColor);

        // x
        int pixelWidth = (int)(getWidth() * zoom);
        long pixelsPerFrame = pixelWidth / wavData.getNumFrames();
        long framesPerPixel = wavData.getNumFrames() / pixelWidth;

        // y
        int pixelHeight = getHeight();

        if(pixelsPerFrame == 0)
        {
            // We can't even fit one pixel per frame so we have to use multiple frames for each pixel
        }

        WavData.WavDataIterator iterator = wavData.createIterator();
        int xValueCount = 0;

        double yEquals0 = pixelHeight / 2;

        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(0, (int)yEquals0, pixelWidth, (int)yEquals0);
        g2.setColor(graphColor);

        GeneralPath path = new GeneralPath();
        path.moveTo(0, yEquals0);

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

            double channelValue = sample[channelIndex];

            // x
            long xValue = xValueCount;
            int xCoord = (int)xValue + scroll;

            // y
            double yValue = (channelValue * (double)pixelHeight);
            double yCoord = yEquals0 + -yValue;

            path.lineTo(xCoord, yCoord);

            xValueCount++;
        }

        g2.draw(path);
    }
}
