package src;

import java.io.File;

import javax.swing.filechooser.FileView;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ImageFileView extends FileView {
    //************************************************************************
    //*                 getIcon
    //************************************************************************
    public Icon getIcon(File file) {
        //Log.d(LOG_TAG, ".getIcon()");

        String extension = FileChooserUtil.getExtension(file);
        Icon icon = null;

        if (extension != null) {
            if (extension.equals(FileChooserUtil.gif)) {
                icon = gifIcon;
            } else if (extension.equals(FileChooserUtil.jpg)) {
                icon = jpgIcon;
            } else if (extension.equals(FileChooserUtil.png)) {
                icon = pngIcon;
            }
        }
        return icon;
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + ImageFileView.class.getSimpleName();

    private ImageIcon gifIcon = FileChooserUtil.createImageIcon(
        "resource/icons8-gif-26.png");
    private ImageIcon jpgIcon = FileChooserUtil.createImageIcon(
        "resource/icons8-jpg-26.png");
    private ImageIcon pngIcon = FileChooserUtil.createImageIcon(
        "resource/icons8-png-26.png");
}