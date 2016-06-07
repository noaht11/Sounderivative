package com.lightark.sounderivative.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainFrame extends JFrame
{
    public MainFrame()
    {
        super("Sounderivative");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(500, 500));
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLayout(new BorderLayout());

        GraphPanel panel1 = new GraphPanel(new File("C:\\Users\\Noah\\Documents\\Java\\Sounderivative\\Test.wav"));
        this.add(panel1, BorderLayout.CENTER);
    }
}
