package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.audio.Transformer;
import com.lightark.sounderivative.audio.WavData;
import com.lightark.sounderivative.audio.WavProcessingListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;

public class WavAnalysisPanel extends JPanel implements Runnable
{
    private static final int PROGRESS_SCALE = 100000;

    private static final float MAX_ZOOM = 20.0f;
    private static final float MIN_ZOOM = 0.2f;
    private static final int ZOOM_IN = 1;
    private static final int ZOOM_OUT = -1;

    private static final int SCROLL_RESOLUTION = 50;
    private static final int SCROLL_LEFT = 1;
    private static final int SCROLL_RIGHT = -1;

    private File file;
    private WavData srcData;
    private WavData derivativeData;
    private WavData integralData;

    private JDialog progressDialog;
    private JProgressBar progressBar;
    private JLabel progressDescription;

    private JPanel graphs;

    private float zoom = 1.0f;
    private int scroll = 0;

    private WavDataPanel srcWav;
    private WavDataPanel derivativeWav;
    private WavDataPanel integralWav;

    public WavAnalysisPanel(File file, JFrame frame)
    {
        this.file = file;

        setLayout(new BorderLayout());

        // HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        JLabel pathLabel = new JLabel(file.getAbsolutePath());
        pathLabel.setFont(pathLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(pathLabel);

        add(headerPanel, BorderLayout.PAGE_START);

        // GRAPHS
        graphs = new JPanel();
        graphs.setLayout(new GridBagLayout());

        add(graphs, BorderLayout.CENTER);

        // PROGRESS DIALOG
        progressDialog = new JDialog(frame, "Processing WAV File", false);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setSize(300, 65);
        progressDialog.setLocationRelativeTo(frame);
        progressDialog.setUndecorated(true);
        progressDialog.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        contentPanel.setBackground(Color.WHITE);

        progressDescription = new JLabel();
        progressDescription.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.add(BorderLayout.PAGE_START, progressDescription);

        progressBar = new JProgressBar(0, PROGRESS_SCALE);
        progressBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.add(BorderLayout.CENTER, progressBar);

        progressDialog.add(contentPanel, BorderLayout.CENTER);

        // SCROLL LISTENER
        addMouseWheelListener(new MouseWheelListener()
        {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                boolean ctrl = e.isControlDown();
                int wheelRotation = e.getWheelRotation();
                if(ctrl)
                {
                    if(wheelRotation > 0)
                    {
                        if(zoom >= MIN_ZOOM)
                        {
                            zoom(ZOOM_OUT);
                        }
                    }
                    else if(wheelRotation < 0)
                    {
                        if(zoom < MAX_ZOOM)
                        {
                            zoom(ZOOM_IN);
                        }
                    }
                }
                else
                {
                    if(wheelRotation > 0)
                    {
                        scroll(SCROLL_RIGHT);
                    }
                    else if(wheelRotation < 0)
                    {
                        if(scroll < 0)
                        {
                            scroll(SCROLL_LEFT);
                        }
                    }
                }
            }
        });

        // LOAD WAV DATA
        new Thread(this).start();
    }

    private void zoom(int direction)
    {
        zoom += direction * 0.1f;

        srcWav.setZoom(zoom);
        derivativeWav.setZoom(zoom);
        integralWav.setZoom(zoom);

        invalidate();
        repaint();
    }

    private void scroll(int direction)
    {
        scroll += SCROLL_RESOLUTION * direction;

        srcWav.setScroll(scroll);
        derivativeWav.setScroll(scroll);
        integralWav.setScroll(scroll);

        invalidate();
        repaint();
    }

    @Override
    public void run()
    {
        progressDialog.setVisible(true);

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;

        // ORIGINAL
        progressDescription.setText("Reading WAV File...");
        try
        {
            srcData = WavData.fromFile(file, new WavProcessingListener()
            {
                @Override
                public void progressUpdate(long framesProcessed, long numFrames)
                {
                    final double progress = (double)framesProcessed;
                    double total = (double)numFrames;
                    final int percentage = (int)((progress / total) * PROGRESS_SCALE);

                    progressBar.setValue(percentage);
                    progressDialog.validate();
                    progressDialog.repaint();
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // DERIVATIVE
        progressDescription.setText("Calculating Derivative...");
        try
        {
            derivativeData = WavData.fromExisting(new Transformer.Differentiator(srcData), new WavProcessingListener()
            {
                @Override
                public void progressUpdate(long framesProcessed, long numFrames)
                {
                    final double progress = (double)framesProcessed;
                    double total = (double)numFrames;
                    final int percentage = (int)((progress / total) * PROGRESS_SCALE);

                    progressBar.setValue(percentage);
                    progressDialog.validate();
                    progressDialog.repaint();
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // INTEGRAL
        progressDescription.setText("Calculating Integral...");
        try
        {
            integralData = WavData.fromExisting(new Transformer.Integrator(srcData), new WavProcessingListener()
            {
                @Override
                public void progressUpdate(long framesProcessed, long numFrames)
                {
                    final double progress = (double)framesProcessed;
                    double total = (double)numFrames;
                    final int percentage = (int)((progress / total) * PROGRESS_SCALE);

                    progressBar.setValue(percentage);
                    progressDialog.validate();
                    progressDialog.repaint();
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        // DISPLAY ALL THE GRAPHS

        srcWav = new WavDataPanel(srcData, "Original", Color.BLACK);
        constraints.gridy = 0;
        graphs.add(srcWav, constraints);

        derivativeWav = new WavDataPanel(derivativeData, "Derivative", Color.BLUE);
        constraints.gridy = 1;
        graphs.add(derivativeWav, constraints);

        integralWav = new WavDataPanel(integralData, "Integral", Color.RED);
        constraints.gridy = 2;
        graphs.add(integralWav, constraints);

        progressDialog.dispose();
    }
}
