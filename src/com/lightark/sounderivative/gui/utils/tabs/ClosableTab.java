package com.lightark.sounderivative.gui.utils.tabs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class ClosableTab extends JPanel implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTabbedPane tabs;
	private TabCloseListener closeListener;
	
	private JLabel title;
	private JButton close;
	
	public ClosableTab(JTabbedPane tabs, TabCloseListener closeListener)
	{
		this.closeListener = closeListener;
		this.tabs = tabs;
		
        title = new JLabel("",JLabel.CENTER)
        {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public String getText()
            {
                int i = ClosableTab.this.tabs.indexOfTabComponent(ClosableTab.this);
                if (i != -1)
                {
                    return ClosableTab.this.tabs.getTitleAt(i);
                }
                return "";
            }
        };
		title.setOpaque(false);
		title.setPreferredSize(new Dimension(100,(getPreferredSize().height + 5)));
		add(title);
		
		close = new JButton("\u00d7");
		close.setOpaque(false);
		close.setFocusable(false);
		close.setBorderPainted(false);
		close.setMargin(new Insets(0,0,0,0));
		close.setForeground(Color.gray);
		close.setFont(new Font("Calibri",Font.BOLD,20));
		close.setToolTipText("Close Tab");
		close.setPreferredSize(new Dimension(20,20));
		close.setContentAreaFilled(false);
		
		close.addMouseListener(new MouseAdapter()
		{
	        public void mouseEntered(MouseEvent e)
	        {
	            Component component = e.getComponent();
	            if (component instanceof AbstractButton)
	            {
	                AbstractButton button = (AbstractButton) component;
	                button.setForeground(Color.red);
	            }
	        }

	        public void mouseExited(MouseEvent e)
	        {
	            Component component = e.getComponent();
	            if (component instanceof AbstractButton)
	            {
	                AbstractButton button = (AbstractButton) component;
	                button.setForeground(Color.gray);
	            }
	        }
		});
		close.addActionListener(this);
		
		add(close);
		
		setOpaque(false);
	}

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		int index = tabs.indexOfTabComponent(this);
		closeListener.tabClosed(index);
		/*if(index != -1)
		{
			tabs.removeTabAt(index);
		}
		if(tabs.getTabCount() <= 0)
		{
			//System.exit(0);
		}*/
	}

}
