package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.audio.Transformer;
import com.lightark.sounderivative.audio.WavData;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class WavAnalysisPanel extends JPanel
{
    private File file;
    private WavData srcData;
    private WavData derivativeData;
    private WavData integralData;

    private WavDataPanel srcWav;
    private WavDataPanel derivativeWav;
    private WavDataPanel integralWav;

    public WavAnalysisPanel(File file)
    {
        this.file = file;

        try
        {
            this.srcData = WavData.fromFile(file);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }

        try
        {
            this.derivativeData = WavData.fromExisting(new Transformer.Differentiator(srcData));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            this.integralData = WavData.fromExisting(new Transformer.Integrator(srcData));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());

        // HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        JLabel pathLabel = new JLabel(file.getAbsolutePath());
        pathLabel.setFont(pathLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(pathLabel);

        add(headerPanel, BorderLayout.PAGE_START);

        // GRAPHS
        JPanel graphs = new JPanel();
        graphs.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;

        this.srcWav = new WavDataPanel(srcData, "Original", Color.BLACK);
        constraints.gridy = 0;
        graphs.add(this.srcWav, constraints);

        this.derivativeWav = new WavDataPanel(derivativeData, "Derivative", Color.BLUE);
        constraints.gridy = 1;
        graphs.add(this.derivativeWav, constraints);

        this.integralWav = new WavDataPanel(integralData, "Integral", Color.RED);
        constraints.gridy = 2;
        graphs.add(this.integralWav, constraints);

        add(graphs, BorderLayout.CENTER);
    }
}
