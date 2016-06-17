package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.audio.Transformer;
import com.lightark.sounderivative.audio.WavData;
import com.lightark.sounderivative.audio.WavProcessingListener;
import com.lightark.sounderivative.gui.utils.ProgressDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class WavAnalysisPanel extends JPanel implements Runnable
{
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

    private ProgressDialog progressDialog;

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
        setFocusable(true);
        addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                revalidate();
                repaint();
            }
        });

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
        progressDialog = new ProgressDialog(frame);

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
                        zoom(ZOOM_OUT);
                    }
                    else if(wheelRotation < 0)
                    {
                        zoom(ZOOM_IN);
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
                        scroll(SCROLL_LEFT);
                    }
                }
            }
        });
        // Scroll
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        this.getActionMap().put("left", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                scroll(SCROLL_LEFT);
            }
        });
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
        this.getActionMap().put("right", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                scroll(SCROLL_RIGHT);
            }
        });
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), "home");
        this.getActionMap().put("home", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                scroll = 0;
                updateScroll();
            }
        });
        // Zoom keys
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK), "ctrlequals");
        this.getActionMap().put("ctrlequals", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                zoom(ZOOM_IN);
            }
        });
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK), "ctrlminus");
        this.getActionMap().put("ctrlminus", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                zoom(ZOOM_OUT);
            }
        });
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK), "ctrlzero");
        this.getActionMap().put("ctrlzero", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                zoom = 1.0f;
                updateZoom();
            }
        });


        // LOAD WAV DATA
        new Thread(this).start();
    }

    private void zoom(int direction)
    {
        if(direction == ZOOM_IN && zoom >= MAX_ZOOM)
        {
            return;
        }
        if(direction == ZOOM_OUT && zoom <= MIN_ZOOM)
        {
            return;
        }

        zoom += direction * 0.1f;

        updateZoom();
    }

    private void updateZoom()
    {
        srcWav.setZoom(zoom);
        derivativeWav.setZoom(zoom);
        integralWav.setZoom(zoom);

        revalidate();
        repaint();
    }

    private void scroll(int direction)
    {
        if(direction == SCROLL_LEFT && scroll >= 0)
        {
            return;
        }

        scroll += SCROLL_RESOLUTION * direction;

        updateScroll();
    }

    private void updateScroll()
    {
        srcWav.setScroll(scroll);
        derivativeWav.setScroll(scroll);
        integralWav.setScroll(scroll);

        revalidate();
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
        progressDialog.setDescription("Reading WAV File...");
        try
        {
            srcData = WavData.fromFile(file, new WavProcessingListener()
            {
                @Override
                public void progressUpdate(long framesProcessed, long numFrames)
                {
                    double progress = (double)framesProcessed;
                    double total = (double)numFrames;

                    progressDialog.setPercentage(progress / total);
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
        progressDialog.setDescription("Calculating Derivative...");
        try
        {
            derivativeData = WavData.fromExisting(new Transformer.Differentiator(srcData), new WavProcessingListener()
            {
                @Override
                public void progressUpdate(long framesProcessed, long numFrames)
                {
                    double progress = (double)framesProcessed;
                    double total = (double)numFrames;

                    progressDialog.setPercentage(progress / total);
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
        progressDialog.setDescription("Calculating Integral...");
        try
        {
            integralData = WavData.fromExisting(new Transformer.Integrator(srcData), new WavProcessingListener()
            {
                @Override
                public void progressUpdate(long framesProcessed, long numFrames)
                {
                    double progress = (double)framesProcessed;
                    double total = (double)numFrames;

                    progressDialog.setPercentage(progress / total);
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

        requestFocusInWindow();
    }
}
