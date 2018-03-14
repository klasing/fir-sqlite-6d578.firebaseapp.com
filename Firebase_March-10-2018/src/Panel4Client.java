package src;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class Panel4Client extends JPanel {
    protected Panel4Client(final Control control) {
        super(new BorderLayout());
        Log.d(LOG_TAG, "<<constructor>> Panel4Client()");

        setBackground(Color.BLACK);

        // create DataTable4Recieve instance
        DataTable4Receive dataTable4Receive = new DataTable4Receive(
            dtm4Receive);

        // place dataTable4Receive into jScrollPane
        JScrollPane jScrollPane = new JScrollPane(dataTable4Receive,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(jScrollPane, BorderLayout.CENTER);

        Panel4Send panel4Send = new Panel4Send(control);
        add(panel4Send, BorderLayout.SOUTH);
    }
    //************************************************************************
    //*                 receiveMessage
    //************************************************************************
    protected void receiveMessage(final ImageIcon iconPhoto,
        final String text, final ImageIcon iconImage, final String name) {

        //Log.d(LOG_TAG, ".receiveMessage()");

        // place received data into DataObject4Receive instance
        DataObject4Receive dataObject4Receive = new DataObject4Receive();
        dataObject4Receive.setObject(iconPhoto, 0);
        if (text != null) {
            dataObject4Receive.setObject(text, 1);
        }
        if (iconImage != null) {
            dataObject4Receive.setObject(iconImage, 1);
        }
        dataObject4Receive.setObject(name, 2);

        // place DataObject4Receive instance into Dtm4Receive instance
        dtm4Receive.addData(dataObject4Receive);
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + Panel4Client.class.getSimpleName();

    private static Control control = null;
    private static Dtm4Receive dtm4Receive = new Dtm4Receive();
}
