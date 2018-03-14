package src;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ImageFilter extends FileFilter {
    //************************************************************************
    //*                 accept
    //************************************************************************
    public boolean accept(File file) {
        Log.d(LOG_TAG, ".accept()");

        if (file.isDirectory()) {
            return false;
        }

        String extension = FileChooserUtil.getExtension(file);

        if (extension != null) {
            if (extension.equals("gif") ||
                extension.equals("jpg") ||
                extension.equals("png")) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    //************************************************************************
    //*                 getDescription
    //************************************************************************
    public String getDescription() {
        Log.d(LOG_TAG, ".getDescription()");

        return "*.gif, *.jpg, *.png";
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + FileFilter.class.getSimpleName();
}