package src;

import java.io.File;

import javax.swing.ImageIcon;

public class FileChooserUtil {
    //************************************************************************
    //*                 getExtension
    //************************************************************************
    protected static String getExtension(File file) {
        //Log.d(LOG_TAG, ".getExtension()");

        String extension = null;
        String name = file.getName();
        int i = name.lastIndexOf('.');

        if (i > 0 && i < name.length() - 1) {
            extension = name.substring(i + 1).toLowerCase();
        }
        return extension;
    }
    //************************************************************************
    //*                 createImageIcon
    //************************************************************************
    protected static ImageIcon createImageIcon(String path) {
        Log.d(LOG_TAG, ".createImageIcon()");

        java.net.URL imgURL = FileChooserUtil.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            Log.i("Couldn't find file: " + path);
            return null;
        }
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + FileChooserUtil.class.getSimpleName();

    protected static final String gif = "gif";
    protected static final String jpg = "jpg";
    protected static final String png = "png";
}