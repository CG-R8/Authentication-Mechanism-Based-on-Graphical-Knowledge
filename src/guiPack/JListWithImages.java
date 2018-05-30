package guiPack;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import javax.swing.*;
import java.awt.*;

public class JListWithImages extends JList {

    public JListWithImages() {
        setCellRenderer(new CustomCellRenderer());
    }

    class CustomCellRenderer implements ListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            Component component = (Component) value;
            component.setBackground(isSelected ? new Color(100,100,255)  : Color.white);
            component.setForeground(isSelected ? Color.gray : Color.gray);
            return component;
        }
    }
}