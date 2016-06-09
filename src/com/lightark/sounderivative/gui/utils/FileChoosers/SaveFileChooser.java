package com.lightark.sounderivative.gui.utils.filechoosers;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public abstract class SaveFileChooser extends FileFilter implements Choosable
{
	private boolean allFiles = false;
	
	private JFrame frame;
	private String extension;
	private String description;
	private String defaultName;
	
	private JFileChooser jfc;
	
	public SaveFileChooser(JFrame frame, String _extension, String desc, String _defaultName)
	{
		this.extension = _extension;
		this.description = desc;
		this.defaultName = _defaultName;
		this.frame = frame;
		
		if(extension == null)
		{
			allFiles = true;
		}
		
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
				String fPath = f.getAbsolutePath();
				if(!allFiles)
				{
					if(f.getPath().endsWith(extension))
					{
						fPath = f.getPath();
					}
					else
					{
						fPath = f.getPath() + extension;
					}
				}
				File newF = new File(fPath);
		        if(newF.exists() && getDialogType() == SAVE_DIALOG)
		        {
		        	String message = "The file \"" + newF.getName() + "\" already exists. Do you wan't to overwrite it?";
		            int result = JOptionPane.showConfirmDialog(this,message,"Existing file",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
		            switch(result)
		            {
		                case JOptionPane.YES_OPTION:
		                    super.approveSelection();
		                    return;
		                case JOptionPane.NO_OPTION:
		                    return;
		                case JOptionPane.CLOSED_OPTION:
		                    return;
		                case JOptionPane.CANCEL_OPTION:
		                    cancelSelection();
		                    return;
		            }
		        }
			    super.approveSelection();
		    }
		};
		jfc.setSelectedFile(new File(jfc.getCurrentDirectory() + "\\" + defaultName));
		if(!allFiles)
		{
			jfc.setFileFilter(this);
			jfc.setAcceptAllFileFilterUsed(false);
		}
		jfc.setMultiSelectionEnabled(false);
	}

	public void showChooser()
	{
		int returnVal = jfc.showSaveDialog(frame);

		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			String fPath = jfc.getSelectedFile().getAbsolutePath();
			if(!allFiles)
			{
				if(jfc.getSelectedFile().getPath().endsWith(extension))
				{
					fPath = jfc.getSelectedFile().getPath();
				}
				else
				{
					fPath = jfc.getSelectedFile().getPath() + extension;
				}
			}
			File newFile = new File(fPath);
			chosen(newFile);
		}
	}
	
	@Override
    public boolean accept(File f)
    {
        if (f.isDirectory())
        {
            return true;
        }

        if(f.getName().toLowerCase().endsWith(extension))
        {
        	return true;
        }
        else
        {
        	return false;
        }
    }

	@Override
    public String getDescription()
    {
        return description;
    }
}