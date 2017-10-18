import javax.swing.*;
import java.awt.*;

public class CellRenderer extends DefaultListCellRenderer
{
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        final java.awt.Color DARK_GREEN = new Color(0x3A7B0F);
        if (value.equals("active")) {
            c.setForeground(DARK_GREEN);
        }
        else
        {
            c.setForeground(Color.RED);
        }
        return c;
    }
}
