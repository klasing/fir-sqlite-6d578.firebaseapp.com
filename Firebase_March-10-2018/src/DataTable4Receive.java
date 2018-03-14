package src;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class DataTable4Receive extends JTable {
    protected DataTable4Receive(final Dtm4Receive dtm) {
        super(dtm);
        Log.d(LOG_TAG, "<<constructor>> DataTable4Receive()");

        // don't show table header
        setTableHeader(null);
        // don't show lines, around cells
        setShowHorizontalLines(false);
        setShowVerticalLines(false);
        // don't allow selection
        setCellSelectionEnabled(false);
        // set row height
        setRowHeight(80);
        // set column width
        getColumnModel().getColumn(0).setPreferredWidth(60);
        getColumnModel().getColumn(1).setPreferredWidth(400);
        getColumnModel().getColumn(2).setPreferredWidth(340);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

    }
    //************************************************************************
    //*                 getCellRenderer
    //************************************************************************
    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        //Log.d(LOG_TAG, ".getCellRenderer()");

        switch (column) {
        case 0:
            return super.getDefaultRenderer(ImageIcon.class);
        case 1:
            if(getValueAt(row, 1) instanceof String) {
                TableCellRenderer tcr = getDefaultRenderer(String.class);
                Component component = tcr.getTableCellRendererComponent(
                    this, getValueAt(row, 1), false, false, row, column);
                ((JLabel)component).setForeground(new Color(0x0, 0x0, 0x0));
                ((JLabel)component).setToolTipText(((JLabel)component).getText());
                return tcr;//return super.getDefaultRenderer(String.class);
            } else {
                // get left alignment for ImageIcon
                TableCellRenderer tcr = getDefaultRenderer(ImageIcon.class);
                Component component = tcr.getTableCellRendererComponent(
                    this, getValueAt(row, 1), false, false, row, column);
                ((JLabel)component).setHorizontalAlignment(JLabel.LEFT);
                return tcr;
            }
        default:
            TableCellRenderer tcr = getDefaultRenderer(String.class);
            Component component = tcr.getTableCellRendererComponent(
                this, getValueAt(row, 2), false, false, row, column);
            ((JLabel)component).setForeground(new Color(0xA0, 0xA0, 0xA0));
            ((JLabel)component).setToolTipText(null);
            return tcr;//return super.getDefaultRenderer(String.class);
        } // eof switch
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + DataTable4Receive.class.getSimpleName();

}