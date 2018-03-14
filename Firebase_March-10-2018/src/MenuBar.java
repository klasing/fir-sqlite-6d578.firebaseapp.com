package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar extends JMenuBar implements ActionListener {
    protected MenuBar() {
        Log.d(LOG_TAG, "<<constructor>> MenuBar()");

        // item on menubar
        JMenu file_menu = new JMenu("File");
        add(file_menu);

        // sub item
        JMenuItem jmiExit = new JMenuItem("Exit");
        file_menu.add(jmiExit);

        jmiExit.addActionListener(this);
    }
    //************************************************************************
    //*                 actionPerformed
    //************************************************************************
    public void actionPerformed(ActionEvent e) {
        Log.d(LOG_TAG, ".actionPerformed()");

        if (e.getActionCommand().equals("Exit")) {
            // exit application with exit code is zero
            Main.exit(0);
        }
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + MenuBar.class.getSimpleName();
}