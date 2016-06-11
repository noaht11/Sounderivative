package com.lightark.sounderivative.gui.utils;

import javax.swing.*;
import java.awt.*;

public class ProgressDialog extends JDialog
{
    private static final int PROGRESS_SCALE = 100000;

    private JLabel descriptionLabel;
    private JProgressBar progressBar;

    public ProgressDialog(JFrame frame)
    {
        super(frame, false);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setSize(300, 65);
        setLocationRelativeTo(frame);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        contentPanel.setBackground(Color.WHITE);

        descriptionLabel = new JLabel();
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.add(BorderLayout.PAGE_START, descriptionLabel);

        progressBar = new JProgressBar(0, PROGRESS_SCALE);
        progressBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.add(BorderLayout.CENTER, progressBar);

        add(contentPanel, BorderLayout.CENTER);
    }

    public void setDescription(String description)
    {
        descriptionLabel.setText(description);
    }

    public void setPercentage(double decimal)
    {
        progressBar.setValue((int)(decimal * (double)PROGRESS_SCALE));
    }
}
