package com.lightark.sounderivative.gui;

import com.lightark.sounderivative.Resources;
import com.lightark.sounderivative.audio.Transformer;
import com.lightark.sounderivative.audio.WavData;
import com.lightark.sounderivative.audio.WavFileException;
import com.lightark.sounderivative.audio.WavProcessingListener;
import com.lightark.sounderivative.gui.utils.ProgressDialog;
import com.lightark.sounderivative.gui.utils.filechoosers.SaveFileChooser;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

public class WavDataPanel extends JPanel
{
    private WavData wavData;

    private GraphPanel graphPanel;

    public WavDataPanel(final WavData wavData, final String title, Color graphColor)
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

        infoPanel.add(Box.createVerticalStrut(5));

        final JCheckBox autoAmplifyCheckBox = new JCheckBox("Auto-Amplify");
        autoAmplifyCheckBox.setSelected(true);
        autoAmplifyCheckBox.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                graphPanel.setAutoAmplify(autoAmplifyCheckBox.isSelected());
                revalidate();
                repaint();
            }
        });
        infoPanel.add(autoAmplifyCheckBox);

        infoPanel.add(Box.createVerticalStrut(5));

        JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
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
                }
            }
        });
        infoPanel.add(playButton);

        infoPanel.add(Box.createVerticalStrut(5));

        JButton exportButton = new JButton("Export...");
        exportButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                final JFrame frame = ((JFrame)SwingUtilities.getWindowAncestor(WavDataPanel.this));
                SaveFileChooser sfc = new SaveFileChooser(frame, ".wav", "WAV files (*.wav)", title + ".wav")
                {
                    @Override
                    public void chosen(final Object obj)
                    {
                        final ProgressDialog progressDialog = new ProgressDialog(frame);
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try
                                {
                                    progressDialog.setVisible(true);

                                    WavProcessingListener listener = new WavProcessingListener()
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
                                    };

                                    progressDialog.setDescription("Auto-Amplifying Audio...");
                                    WavData amplifiedData = true ? WavData.fromExisting(new Transformer.Amplifier(wavData), listener) : wavData;

                                    progressDialog.setDescription("Exporting as WAV...");
                                    File destination = (File)obj;
                                    amplifiedData.exportToFile(destination, listener);
                                    progressDialog.dispose();

                                    Icon saveIcon = Resources.loadIcon("save.png");
                                    int selection = JOptionPane.showOptionDialog(frame, "File saved to: " + destination.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, saveIcon, new String[]{"Show in Explorer", "Close"}, 0);
                                    if(selection == 0)
                                    {
                                        Runtime.getRuntime().exec("explorer.exe /select, \"" + destination.getAbsolutePath() + "\"");
                                    }
                                }
                                catch(IOException e1)
                                {
                                    e1.printStackTrace();
                                }
                                catch(WavFileException e1)
                                {
                                    e1.printStackTrace();
                                }
                                catch(Exception e1)
                                {
                                    e1.printStackTrace();
                                }
                            }
                        }).start();
                    }
                };
                sfc.showChooser();
            }
        });
        infoPanel.add(exportButton);

        infoPanel.setPreferredSize(new Dimension(150, infoPanel.getPreferredSize().height));

        add(infoPanel, BorderLayout.LINE_START);

        this.graphPanel = new GraphPanel(wavData, graphColor);
        graphPanel.setAutoAmplify(true);
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
