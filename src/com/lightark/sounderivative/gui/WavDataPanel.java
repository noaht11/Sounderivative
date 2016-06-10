package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.audio.WavData;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class WavDataPanel extends JPanel
{
    private WavData wavData;

    private GraphPanel graphPanel;

    public WavDataPanel(final WavData wavData, String title, Color graphColor)
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

        infoPanel.add(Box.createVerticalStrut(10));

        JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                /*try
                {
                    SourceDataLine line = AudioSystem.getSourceDataLine(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, wavData.getSampleRate(), wavData.getBytesPerSample() * 2, wavData.getNumChannels(), 1, 1, true));
                    line.start();

                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("C:\\Users\\Noah\\Documents\\Test.wav"));
                    clip.open(inputStream);
                    clip.start();
                }
                catch(LineUnavailableException e1)
                {
                    e1.printStackTrace();
                }
                catch(IOException e1)
                {
                    e1.printStackTrace();
                }
                catch(UnsupportedAudioFileException e1)
                {
                    e1.printStackTrace();
                }*/
            }
        });
        infoPanel.add(playButton);

        infoPanel.add(Box.createVerticalStrut(5));

        JButton exportButton = new JButton("Export...");
        playButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // TODO
            }
        });
        infoPanel.add(exportButton);

        add(infoPanel, BorderLayout.LINE_START);

        this.graphPanel = new GraphPanel(wavData, graphColor);
        add(this.graphPanel, BorderLayout.CENTER);
    }

    public void setZoom(float zoom)
    {
        graphPanel.setZoom(zoom);
    }

    public void setScroll(int scroll)
    {
        graphPanel.setScroll(scroll);
    }
}
