package com.lightark.sounderivative.gui.utils.filechoosers;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public abstract class OpenFileChooser extends FileFilter implements Choosable
{
    private JFrame frame;
    private String[] extensions;
    private String description;

    private boolean multiSelection;

    private JFileChooser jfc;

    public OpenFileChooser(JFrame frame, String extension, String desc)
    {
        this(frame, new String[]{extension}, desc);
    }

    public OpenFileChooser(JFrame frame, String[] extensions, String desc)
    {
        this.extensions = extensions;
        this.description = desc;
        this.frame = frame;

        jfc = new JFileChooser()
        {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void approveSelection()
            {
                File f = getSelectedFile();
                if(!f.exists() && getDialogType() == OPEN_DIALOG)
                {
                    String message = "The file \"" + f.getName() + "\" does not exist. Please select another file";
                    JOptionPane.showMessageDialog(OpenFileChooser.this.frame, message, "File does not exist", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                super.approveSelection();
            }
        };
        if(extensions.length > 0)
        {
            jfc.setFileFilter(this);
            jfc.setAcceptAllFileFilterUsed(false);
        }
        jfc.setMultiSelectionEnabled(false);
        multiSelection = false;
    }

    public void enableMultiSelection()
    {
        jfc.setMultiSelectionEnabled(true);
        multiSelection = true;
    }
    public void disableMultiSelection()
    {
        jfc.setMultiSelectionEnabled(false);
        multiSelection = false;
    }

    public void showChooser()
    {
        int returnVal = jfc.showOpenDialog(frame);

        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            if(multiSelection)
            {
                chosen(jfc.getSelectedFiles());
            }
            else
            {
                chosen(jfc.getSelectedFile());
            }
        }
    }

    @Override
    public boolean accept(File f)
    {
        if (f.isDirectory())
        {
            return true;
        }

        for(String ext : extensions)
        {
            if(f.getName().toLowerCase().endsWith(ext))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    public static File[] filterFiles(String[] extensions, File[] files)
    {
        ArrayList<File> filtered = new ArrayList<File>();
        for(File f : files)
        {
            for(String s : extensions)
            {
                if(!f.isDirectory())
                {
                    if(f.getAbsolutePath().toLowerCase().endsWith(s.toLowerCase()))
                    {
                        filtered.add(f);
                    }
                }
            }
        }
        File[] filteredArray = new File[filtered.size()];
        for(int i = 0;i < filtered.size();i++)
        {
            filteredArray[i] = filtered.get(i);
        }
        return filteredArray;
    }
}