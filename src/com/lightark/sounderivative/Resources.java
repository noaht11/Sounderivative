package com.lightark.sounderivative;

import javax.swing.*;

public class Resources
{
    public static final ImageIcon loadIcon(String fileName)
    {
        return new ImageIcon(Resources.class.getResource("resources/icons/" + fileName));
    }
}
