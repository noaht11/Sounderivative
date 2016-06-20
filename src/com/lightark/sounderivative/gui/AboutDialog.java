package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.Resources;

import javax.swing.*;
import java.awt.*;

public class AboutDialog extends JDialog
{
    private JPanel basePanel = new JPanel();
    private JLabel logoLabel;

    private JPanel contentPanel = new JPanel();
    private JLabel titleLabel = new JLabel();
    private JPanel flowContainer = new JPanel();
    private JLabel contentLabel = new JLabel();

    private JFrame frame;

    public AboutDialog(JFrame frame)
    {
        super(frame,("About Sounderivative"));
        this.frame = frame;
        this.setSize(780, 380);
        this.setResizable(false);
        this.setLocationRelativeTo(frame);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        basePanel.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 10));
        basePanel.setLayout(new BorderLayout());

        ImageIcon logo = Resources.loadIcon("app_icon_256.png");
        logoLabel = new JLabel(logo);

        basePanel.add(logoLabel, BorderLayout.LINE_START);

        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 35, 0, 0));

        titleLabel.setText("Sounderivative");
        titleLabel.setFont(getFont().deriveFont(Font.BOLD, 40));
        contentPanel.add(titleLabel, BorderLayout.PAGE_START);

        contentLabel.setText(
                "<html>" +
                        "<i>version 1.0</i>" +
                        "<br><br>" +
                        "Sounderivative" +
                        " is a java application for calculating,<br/>playing and exporting the derivative and integral<br/>of an audio file (currently only supporting WAV files)." +
                        "<br><br>" +
                        "The WAV files are processed using the Java Wav File IO<br/>library created by Dr. Andrew Greensted." +
                        "<br><br>" +
                        "Copyright &copy; Noah Tajwar 2016. All rights reserved." +
                        "</html>");
        contentLabel.setFont(getFont().deriveFont(Font.PLAIN, 17));
        contentLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        flowContainer.add(contentLabel);
        contentPanel.add(flowContainer, BorderLayout.CENTER);


        basePanel.add(contentPanel, BorderLayout.CENTER);

        this.getContentPane().add(basePanel, BorderLayout.LINE_START);
    }

    @Override
    public void setVisible(boolean b)
    {
        this.setLocationRelativeTo(frame);
        super.setVisible(true);
    }

}