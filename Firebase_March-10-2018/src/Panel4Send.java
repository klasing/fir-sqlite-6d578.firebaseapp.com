package src;

import java.awt.Color;
import java.awt.Image;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;

import java.io.File;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Panel4Send extends JPanel implements DocumentListener, ActionListener {
    protected Panel4Send(final Control control) {
        Log.d(LOG_TAG, "<<constructor>> Panel4Send()");

        this.control = control;

        //setBackground(Color.GREEN);

        JLabel jLabel = new JLabel("Message");
        add(jLabel);

        jTextField = new JTextField(40);
        jTextField.getDocument().addDocumentListener(this);
        add(jTextField);

        jbSend = new JButton("Send");
        // send button is disabled, as long as no text is entered in the
        // message-textfield
        jbSend.setEnabled(false);
        jbSend.addActionListener(this);
        add(jbSend);

        JButton jbImage = new JButton();
        // icon downloaded from: https://material.io/icons
        ImageIcon imageIcon =
            new ImageIcon("src/resource/ic_photo_black_24dp_1x.png");
        jbImage.setIcon(imageIcon);
        jbImage.setActionCommand("image");
        jbImage.addActionListener(this);
        add(jbImage);

    }
    //************************************************************************
    //*                 documentListener methods
    //************************************************************************
    public void insertUpdate(DocumentEvent e) {
        //Log.d(LOG_TAG, ".insertUpdate()");

        jbSend.setEnabled(true);
    }
    public void removeUpdate(DocumentEvent e) {
        //Log.d(LOG_TAG, ".removeUpdate()");

        if (jTextField.getText().length() == 0)
            jbSend.setEnabled(false);
    }
    public void changedUpdate(DocumentEvent e) {
        //Log.d(LOG_TAG, ".changedUpdate()");

        // attribute(s) of jTextField has (have) been changed
        // do nothing
    }
    //************************************************************************
    //*                 actionPerformed
    //************************************************************************
    public void actionPerformed(ActionEvent e) {
        //Log.d(LOG_TAG, ".actionPerformed()");

        if (e.getActionCommand().equals("Send")) {
            // Log.i(e.getActionCommand());

            control.sendMessage(jTextField.getText());
            return;
        }
        if (e.getActionCommand().equals("image")) {
            // Log.i(e.getActionCommand());

            // open filechooser
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setDialogTitle("Open image file");
            jFileChooser.setCurrentDirectory(new File("src/resource/image"));
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jFileChooser.addChoosableFileFilter(new ImageFilter());
            jFileChooser.setFileView(new ImageFileView());

            int returnVal = jFileChooser.showOpenDialog(this);
            // process selected file choice
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = jFileChooser.getSelectedFile();

                try {
                    // read image from file
                    Image image = ImageIO.read(file);
                    control.sendImage(image, file.getName(),
                        FileChooserUtil.getExtension(file));
                } catch(Exception ex) {
                    Log.d(LOG_TAG, ex.getClass().getName() + " " + ex.getMessage());
                }

                // reset filechooser
                jFileChooser.setSelectedFile(null);
            }
        }
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + Panel4Send.class.getSimpleName();

    private static Control control = null;

    protected static JTextField jTextField = null;
    protected static JButton jbSend = null;
}