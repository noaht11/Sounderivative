package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.audio.WavData;

import javax.swing.*;
import java.awt.*;

public class WavDataPanel extends JPanel
{
    private WavData wavData;

    private GraphPanel graphPanel;

    public WavDataPanel(WavData wavData, String title, Color graphColor)
    {
        this.wavData = wavData;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(graphColor);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        infoPanel.add(titleLabel);

        infoPanel.add(Box.createVerticalStrut(5));

        JLabel channelsPanel = new JLabel("Channels: " + wavData.getNumChannels());
        JLabel framesPanel = new JLabel("Frames: " + wavData.getNumFrames());
        JLabel sampleRatePanel = new JLabel("Sample Rate: " + wavData.getSampleRate());
        JLabel blockAlignPanel = new JLabel("Block Align: " + wavData.getBlockAlign());
        JLabel validBitsPanel = new JLabel("Valid Bits: " + wavData.getValidBits());
        JLabel bytesPerSamplePanel = new JLabel("Bytes/Sample: " + wavData.getBytesPerSample());

        channelsPanel.setForeground(graphColor);
        framesPanel.setForeground(graphColor);
        sampleRatePanel.setForeground(graphColor);
        blockAlignPanel.setForeground(graphColor);
        validBitsPanel.setForeground(graphColor);
        bytesPerSamplePanel.setForeground(graphColor);

        infoPanel.add(channelsPanel);
        infoPanel.add(framesPanel);
        infoPanel.add(sampleRatePanel);
        infoPanel.add(blockAlignPanel);
        infoPanel.add(validBitsPanel);
        infoPanel.add(bytesPerSamplePanel);

        add(infoPanel, BorderLayout.LINE_START);

        this.graphPanel = new GraphPanel(wavData, graphColor);
        add(this.graphPanel, BorderLayout.CENTER);
    }
}
