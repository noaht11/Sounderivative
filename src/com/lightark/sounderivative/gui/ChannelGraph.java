package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.audio.Transformer;
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
    private boolean autoAmplify = false;
    boolean rightEdgeOffScreen = false;

    private long cursorPosition = -1;

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

    public void setAutoAmplify(boolean autoAmplify)
    {
        this.autoAmplify = autoAmplify;
    }

    public void setCursorPosition(long frameNumber)
    {
        this.cursorPosition = frameNumber;
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setColor(graphColor);

        // x
        int pixelWidth = getWidth();
        int scaledWidth = (int)(getWidth() * zoom);
        if(scaledWidth == 0)
        {
            return;
        }
        long pixelsPerFrame = scaledWidth / wavData.getNumFrames();
        long framesPerPixel = wavData.getNumFrames() / scaledWidth;

        // y
        int pixelHeight = getHeight();

        WavData.WavDataIterator iterator = wavData.createIterator();
        int xValueCount = 0;

        double yEquals0 = pixelHeight / 2;
        double yScale = autoAmplify ? Transformer.Amplifier.getScaleFactor(wavData) : 1.0;

        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(0, (int)yEquals0, pixelWidth, (int)yEquals0);
        g2.setColor(graphColor);

        GeneralPath path = new GeneralPath();
        boolean moved = false;

        boolean cursorDrawn = false;
        while(iterator.hasNext())
        {
            double[] sample = iterator.nextFrame();

            if(pixelsPerFrame == 0)
            {
                // We can't even fit one pixel per frame so we have to use multiple frames for each pixel
                // Only capture every (framesPerPixel)th value
                if(iterator.currentIndex() % framesPerPixel != 0)
                {
                    continue;
                }
            }

            double channelValue = sample[channelIndex];

            // x
            long xValue = xValueCount;
            int xCoord = (int)xValue + scroll;

            // Skip over x coordinates that are off-screen to the left
            if(xCoord < 0)
            {
                xValueCount++;
                continue;
            }
            // Stop drawing as soon as we're past the right edge of the screen
            if(xCoord > pixelWidth)
            {
                break;
            }

            // Draw cursor if required
            if(!cursorDrawn && cursorPosition != -1 && iterator.currentIndex() >= cursorPosition)
            {
                g2.drawLine(xCoord, 0, xCoord, pixelHeight);
                cursorDrawn = true;
            }

            // y
            double yValue = (channelValue * (double)(pixelHeight / 2));
            double yCoord = yEquals0 + -(yValue / yScale);

            if(!moved)
            {
                path.moveTo(xCoord, yCoord);
                moved = true;
            }
            else
            {
                path.lineTo(xCoord, yCoord);
            }

            xValueCount++;
        }

        g2.draw(path);


    }
}
