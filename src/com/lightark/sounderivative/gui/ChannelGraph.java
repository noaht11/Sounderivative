package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.audio.WavData;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;

public class ChannelGraph extends JPanel
{
    private static final Color CURSOR_COLOR = Color.GREEN;

    private WavData wavData;
    private int channelIndex;
    private Graph graph;

    private Color graphColor;
    boolean rightEdgeOffScreen = false;

    private long cursorPosition = -1;
    private AutoScrollListener autoScrollListener;

    public ChannelGraph(WavData wavData, int channelIndex, Color graphColor)
    {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));

        this.wavData = wavData;
        this.channelIndex = channelIndex;
        this.graphColor = graphColor;

        graph = new Graph(getWidth(), getHeight(), wavData.getMaxValue(), wavData.getMinValue(), wavData.getNumFrames(), wavData.getSampleRate(), true);
    }

    public void setZoom(float zoom)
    {
        graph.setZoom(zoom);
    }

    public void setScroll(int scroll)
    {
        graph.setScroll(scroll);
    }

    public void setAutoAmplify(boolean autoAmplify)
    {
        graph.setAutoAmplify(autoAmplify);
    }

    public void setCursorPosition(long frameNumber)
    {
        this.cursorPosition = frameNumber;
    }

    public void setAutoScrollListener(AutoScrollListener listener)
    {
        this.autoScrollListener = listener;
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
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2.setColor(graphColor);

        int pixelWidth = getWidth();
        int pixelHeight = getHeight();

        graph.setPixelWidth(pixelWidth);
        graph.setPixelHeight(pixelHeight);

        int yEqualsZero = (int)graph.getYEqualsZero();
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(0, yEqualsZero, pixelWidth, yEqualsZero);
        g2.setColor(graphColor);

        // Auto-scroll for cursor
        if(cursorPosition != -1)
        {
            int xCoord = (int) graph.getXCoordForFrameNumber(cursorPosition);

            if(xCoord < 0 || xCoord > pixelWidth)
            {
                int newScroll = (int)(-graph.getAbsoluteXCoordForFrameNumber(cursorPosition));
                graph.setScroll(newScroll);
                if(autoScrollListener != null)
                {
                    autoScrollListener.graphAutoScrolled(newScroll);
                }
            }
        }

        GeneralPath path = new GeneralPath();

        boolean moved = false;

        int prevXCoord = -1;
        double maxSampleForXCoord = Double.NaN;
        double minSampleForYCoord = Double.NaN;

        WavData.WavDataIterator iterator = wavData.createIterator();
        while(iterator.hasNext())
        {
            double[] frame = iterator.nextFrame();
            double sample = frame[channelIndex];

            int xCoord = (int)graph.getXCoordForFrameNumber(iterator.currentIndex());
            double yCoord = graph.getYCoordForValue(sample);

            if(xCoord < 0)
            {
                continue;
            }
            else if(xCoord > pixelWidth)
            {
                break;
            }

            if(xCoord == prevXCoord)
            {
                if(Double.isNaN(maxSampleForXCoord) || sample > maxSampleForXCoord)
                {
                    maxSampleForXCoord = sample;
                }
                if(Double.isNaN(minSampleForYCoord) || sample < minSampleForYCoord)
                {
                    minSampleForYCoord = sample;
                }
            }
            else
            {
                // Draw or move to graph location
                if(!moved)
                {
                    path.moveTo(xCoord, yCoord);
                    moved = true;
                }
                else
                {
                    double maxYCoord = Double.isNaN(maxSampleForXCoord) ? yCoord : graph.getYCoordForValue(maxSampleForXCoord);
                    double minYCoord = Double.isNaN(minSampleForYCoord) ? yCoord : graph.getYCoordForValue(minSampleForYCoord);
                    path.lineTo(xCoord, maxYCoord);
                    path.lineTo(xCoord, minYCoord);
                }

                prevXCoord = xCoord;
                maxSampleForXCoord = Double.NaN;
                minSampleForYCoord = Double.NaN;
            }
        }

        g2.draw(path);

        // Draw cursor if required
        if(cursorPosition != -1)
        {
            int xCoord = (int)graph.getXCoordForFrameNumber(cursorPosition);

            g2.setColor(CURSOR_COLOR);
            g2.fillRect(xCoord - 1, 0, 2, pixelHeight);
            g2.setColor(graphColor);
        }
    }
}
