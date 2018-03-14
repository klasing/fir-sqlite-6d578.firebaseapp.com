package src;

import javax.swing.JTabbedPane;

public class Panel4Tab extends JTabbedPane {
    protected Panel4Tab(final Control control) {
        Log.d(LOG_TAG, "<<constructor>> Panel4Tab()");

        this.control = control;

        panel4Client = new Panel4Client(control);
        addTab("Client", null, panel4Client, "Show panel for Firebase Client");

    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + Panel4Tab.class.getSimpleName();

    private static Control control = null;
    protected static Panel4Client panel4Client = null;
}