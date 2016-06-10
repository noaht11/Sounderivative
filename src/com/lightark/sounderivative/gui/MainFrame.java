package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.gui.utils.filechoosers.OpenFileChooser;
import com.lightark.sounderivative.gui.utils.tabs.ClosableTab;
import com.lightark.sounderivative.gui.utils.tabs.TabCloseListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class MainFrame extends JFrame implements ChangeListener, TabCloseListener
{
    private JTabbedPane tabs;

    public MainFrame()
    {
        super("Sounderivative");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLayout(new BorderLayout());

        setupMenu();
        setupTabs();
    }

    private void setupMenu()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open...");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                OpenFileChooser ofc = new OpenFileChooser(MainFrame.this, new String[]{".wav"}, "WAV files (*.wav)")
                {
                    @Override
                    public void chosen(Object obj)
                    {
                        addTab((File)obj);
                    }
                };
                ofc.showChooser();
            }
        });
        fileMenu.add(openItem);

        menuBar.add(fileMenu);

        this.setJMenuBar(menuBar);
    }

    private void setupTabs()
    {
        tabs = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.addChangeListener(this);

        this.getContentPane().add(tabs, BorderLayout.CENTER);
    }

    private void addTab(File source)
    {
        WavAnalysisPanel wavAnalysisPanel = new WavAnalysisPanel(source, this);
        tabs.addTab(source.getName(), wavAnalysisPanel);
        tabs.setTabComponentAt((tabs.getTabCount() - 1), new ClosableTab(tabs, this));
        tabs.setToolTipTextAt(tabs.getTabCount() - 1, source.getName());

        tabs.setSelectedIndex(tabs.getTabCount() - 1);
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {

    }

    @Override
    public void tabClosed(int index)
    {
        if(index < tabs.getTabCount() && index > -1)
        {
            tabs.removeTabAt(index);
        }
    }
}
