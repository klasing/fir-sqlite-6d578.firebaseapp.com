package src;

import java.awt.Dimension;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class View extends ComponentAdapter {
    protected View(final Control control) {
        Log.d(LOG_TAG, "<<construtor>> View()");

        this.control = control;

        // schedule a job for the event dispatching thread:
        // creating and showing this application's GUI
        // using lambda expression
        SwingUtilities.invokeLater(() -> createAndShowGui());
    }
    //************************************************************************
    //*                 createAndShowGui
    //************************************************************************
    private void createAndShowGui() {
        Log.d(LOG_TAG, ".createAndShowGui()");

        // create and set up the window
        JFrame frame = new JFrame("Firebase REST");
        // set initial size
        frame.setPreferredSize(new Dimension(MINIMUM_FRAME_WIDTH,
            MINIMUM_FRAME_HEIGHT));
        // set the default close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // try to get an icon on the frame
        ImageIcon img = new ImageIcon("src/resource/firebase-logo.png");
        frame.setIconImage(img.getImage());
        // add menubar
        frame.setJMenuBar(new MenuBar());
        // add listener
        frame.addComponentListener(this);

        // add tabbedpane
        panel4Tab = new Panel4Tab(control);
        frame.add(panel4Tab);

        frame.validate();
        // display the window
        frame.pack();
        frame.setVisible(true);

        // set focus to textfield message
        Panel4Send.jTextField.requestFocusInWindow();
        // button send acts 'clicked', whenever the user hits the Enter/Return key
        frame.getRootPane().setDefaultButton(Panel4Send.jbSend);
    }
    //************************************************************************
    //*                 componentShown
    //************************************************************************
    public void componentShown(ComponentEvent e) {
        Log.d(LOG_TAG, ".componentShown()");

        control.frameIsVisible();
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + View.class.getSimpleName();

    protected static final int MINIMUM_FRAME_WIDTH = 800;
    protected static final int MINIMUM_FRAME_HEIGHT = 600;

    private static Control control = null;
    protected static Panel4Tab panel4Tab = null;
}