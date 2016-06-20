package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.Resources;
import com.lightark.sounderivative.gui.utils.filechoosers.OpenFileChooser;
import com.lightark.sounderivative.gui.utils.tabs.ClosableTab;
import com.lightark.sounderivative.gui.utils.tabs.TabCloseListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;

public class MainFrame extends JFrame implements ChangeListener, TabCloseListener
{
    private JTabbedPane tabs;

    public MainFrame()
    {
        super("Sounderivative");

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(500, 500);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setLayout(new BorderLayout());

        setupIcon();
        setupMenu();
        setupTabs();
    }

    private void setupIcon()
    {
        List<Image> icons = new ArrayList<Image>();
        icons.add(Resources.loadIcon("app_icon_16.png").getImage());
        icons.add(Resources.loadIcon("app_icon_32.png").getImage());
        icons.add(Resources.loadIcon("app_icon_64.png").getImage());
        icons.add(Resources.loadIcon("app_icon_128.png").getImage());
        setIconImages(icons);
    }

    private void setupMenu()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem openItem = new JMenuItem("Open...");
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
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

        JMenu helpMenu = new JMenu("Help");

        JMenuItem aboutItem = new JMenuItem("About...");
        aboutItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                AboutDialog aboutDialog = new AboutDialog(MainFrame.this);
                aboutDialog.setVisible(true);
            }
        });
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);
    }

    private void setupTabs()
    {
        tabs = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT)
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                if(getTabCount() == 0)
                {
                    Graphics2D g2 = (Graphics2D)g;
                    g2.setColor(Color.GRAY);
                    g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20));
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    String defaultText = "Press CTRL + O to open a WAV file";

                    FontMetrics fm = g2.getFontMetrics();
                    Rectangle2D r = fm.getStringBounds(defaultText, g2);
                    int x = (this.getWidth() - (int) r.getWidth()) / 2;
                    int y = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                    g.drawString(defaultText, x, y);
                }
            }
        };
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
