package src;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

public class Dtm4Receive extends AbstractTableModel implements TableModelListener {
    protected Dtm4Receive() {
        Log.d(LOG_TAG, "<<constructor>> Dtm4Receive()");

        // add listener
        addTableModelListener(this);
    }
    //************************************************************************
    //*                 getRowCount
    //************************************************************************
    @Override
    public int getRowCount() {
        //Log.d(LOG_TAG, ".getRowCount()");

        return listData.size();
    }
    //************************************************************************
    //*                 getColumnCount
    //************************************************************************
    @Override
    public int getColumnCount() {
        //Log.d(LOG_TAG, ".getColumnCount()");

        return columnName.length;
    }
    //************************************************************************
    //*                 setValueAt
    //************************************************************************
    @Override
    public void setValueAt(final Object object, final int row, final int column) {
        //Log.d(LOG_TAG, ".setValueAt()");

    }
    //************************************************************************
    //*                 getValueAt
    //************************************************************************
    @Override
    public Object getValueAt(final int row, final int column) {
        //Log.d(LOG_TAG, ".getValueAt()");

        Object object = listData.get(row);

        return ((DataObject4Receive)object).getObject(column);
    }
    //************************************************************************
    //*                 addData
    //************************************************************************
    protected void addData(final DataObject4Receive dataObject4Receive) {
        //Log.d(LOG_TAG, ".addData()");

        listData.add(dataObject4Receive);

        // trigger table model event
        // one row inserted: firstRow = 1, lastRow = 1
        fireTableRowsInserted(1, 1);
    }
    //************************************************************************
    //*                 tableChanged
    //************************************************************************
    public void tableChanged(TableModelEvent e) {
        //Log.d(LOG_TAG, ".tableChanged()");
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + Dtm4Receive.class.getSimpleName();

    private static String[] columnName = { "0", "1", "2"};
    private static List<DataObject4Receive> listData = new ArrayList<>();
}