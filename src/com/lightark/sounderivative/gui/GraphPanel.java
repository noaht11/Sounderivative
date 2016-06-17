package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.audio.WavData;

import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel
{
    private WavData wavData;

    private Color graphColor;

    private ChannelGraph[] channels;

    public GraphPanel(WavData wavData, Color graphColor)
    {
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());

        this.wavData = wavData;
        this.graphColor = graphColor;

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;

        channels = new ChannelGraph[wavData.getNumChannels()];
        for(int i = 0;i < channels.length;i++)
        {
            ChannelGraph theChannel = new ChannelGraph(wavData, i, graphColor);
            channels[i] = theChannel;
            constraints.gridy = i;
            add(theChannel, constraints);
        }
    }

    public void setZoom(float zoom)
    {
        for(ChannelGraph channel : channels)
        {
            channel.setZoom(zoom);
        }
    }

    public void setScroll(int scroll)
    {
        for(ChannelGraph channel : channels)
        {
            channel.setScroll(scroll);
        }
    }

    public void setAutoAmplify(boolean autoAmplify)
    {
        for(ChannelGraph channel : channels)
        {
            channel.setAutoAmplify(autoAmplify);
        }
    }

    public void setCursorPosition(long frameNumber)
    {
        for(ChannelGraph channel : channels)
        {
            channel.setCursorPosition(frameNumber);
        }
    }
}
