package com.lightark.sounderivative;

import com.lightark.sounderivative.gui.MainFrame;

import javax.swing.*;

public class Sounderivative
{
    public static void main(String[] args)
    {
        try
        {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(UnsupportedLookAndFeelException e)
        {
            // handle exception
        }
        catch(ClassNotFoundException e)
        {
            // handle exception
        }
        catch(InstantiationException e)
        {
            // handle exception
        }
        catch(IllegalAccessException e)
        {
            // handle exception
        }

        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }
}
