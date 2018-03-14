package src;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.net.URL;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;

public class Control {
    protected Control() {
        Log.d(LOG_TAG, "<<constructor>> Control()");

        // create Model instance
        model = new Model(this);
        // create View instance
        view = new View(this);
    }
    //************************************************************************
    //*                 frameIsVisible
    //************************************************************************
    protected void frameIsVisible() {
        Log.d(LOG_TAG, ".frameIsVisible()");

        // test for JTable
//        ImageIcon iconPhoto, iconImage;
//        Image imagePhoto, imageImage, scaled_image;
//        String text, name;
//        for (int i = 0; i < 5; i++) {
//            // get icon photo placeholder from disk
//            iconPhoto = new ImageIcon("src/resource/profile_placeholder.png");
//            // scale icon photo
//            imagePhoto = iconPhoto.getImage();
//            scaled_image = imagePhoto.getScaledInstance(40, 40,
//                java.awt.Image.SCALE_SMOOTH);
//            iconPhoto = new ImageIcon(scaled_image);
//            // set text
//            text = "java client message " + i;
//            // no icon image
//            iconImage = null;
//            // set name
//            name = "klasing1959@gmail.com";
//
//            view.panel4Tab.panel4Client.receiveMessage(iconPhoto,
//                text, iconImage, name);
//        }
//        for (int i = 0; i < 5; i++) {
//            // get icon photo placeholder from disk
//            iconPhoto = new ImageIcon("src/resource/profile_placeholder.png");
//            // scale icon photo
//            imagePhoto = iconPhoto.getImage();
//            scaled_image = imagePhoto.getScaledInstance(40, 40,
//                java.awt.Image.SCALE_SMOOTH);
//            iconPhoto = new ImageIcon(scaled_image);
//            // set text
//            text = null;
//            // get icon image from disk
//            iconImage = new ImageIcon("src/resource/image/Catalina_Island.png");
//            // scale icon image
//            imageImage = iconImage.getImage();
//            scaled_image = imageImage.getScaledInstance(60, 60,
//                java.awt.Image.SCALE_SMOOTH);
//            iconImage = new ImageIcon(scaled_image);
//            // set name
//            name = "klasing1959@gmail.com";
//
//            // send image message
//            view.panel4Tab.panel4Client.receiveMessage(iconPhoto,
//                text, iconImage, name);
//        }

        if (!model.authorizeFirebase()) {
            Main.exit(1);
        }
        if (!model.authorizeGoogle()) {
            Main.exit(2);
        }
        // start listen for Firebase message
        model.listenFirebase();
    }
    //************************************************************************
    //*                 sendMessage
    //************************************************************************
    protected void sendMessage(final String message) {
        Log.d(LOG_TAG, ".sendMessage()");

        JsonObject jsonObject = new JsonObject();
        jsonObject.setPhotoUrl(null);
        jsonObject.setText(message);
        jsonObject.setImageUrl(null);
        jsonObject.setName(model.serviceAccountId);

        model.sendMessage(jsonObject);
    }
    //************************************************************************
    //*                 receiveMessage
    //************************************************************************
    protected void receiveMessage(final JsonObject jsonObject) {
        Log.d(LOG_TAG, ".receiveMessage()");
        //Log.i(jsonObject.toString());

        ImageIcon iconPhoto = getIconPhoto(jsonObject);
        String text = jsonObject.getText();
        ImageIcon iconImage = null;
        String name = jsonObject.getName();

        view.panel4Tab.panel4Client.receiveMessage(iconPhoto,
            text, iconImage, name);

    }
    //************************************************************************
    //*                 sendImage
    //************************************************************************
    protected void sendImage(final Image image, final String file_name,
        final String extension) {

        Log.d(LOG_TAG, ".sendImage()");

        final String LOADING_IMAGE_URL =
            "https://www.google.com/images/spin-32.gif";

        JsonObject jsonObject = new JsonObject();
        jsonObject.setPhotoUrl(null);
        jsonObject.setText(null);
        jsonObject.setImageUrl(LOADING_IMAGE_URL);
        jsonObject.setName(model.serviceAccountId);

        // send image with a link to a spinner image,
        // then upload image into google-cloud-storage
        model.sendImage(jsonObject, file_name, image, extension);
    }
    //************************************************************************
    //*                 receiveImage
    //************************************************************************
    protected void receiveImage(final JsonObject jsonObject,
        final BufferedImage bufferedImage) {
        Log.d(LOG_TAG, ".receiveImage()");

        ImageIcon iconPhoto = getIconPhoto(jsonObject);
        String text = null;
        Image scaled_image = bufferedImage.getScaledInstance(60, 60,
            Image.SCALE_SMOOTH);
        ImageIcon iconImage = new ImageIcon(scaled_image);
        String name = jsonObject.getName();

        view.panel4Tab.panel4Client.receiveMessage(iconPhoto,
            text, iconImage, name);

    }
    //************************************************************************
    //*                 getIconPhoto
    //************************************************************************
    protected ImageIcon getIconPhoto(final JsonObject jsonObject) {
        Log.d(LOG_TAG, ".getIconPhoto()");

        ImageIcon iconPhoto = null;
        Image imagePhoto = null;
        Image scaled_image = null;
        if (jsonObject.getPhotoUrl() != null) {
            try {
                URL url = new URL(jsonObject.getPhotoUrl());
                imagePhoto = ImageIO.read(url);
                scaled_image = imagePhoto.getScaledInstance(40, 40,
                    Image.SCALE_SMOOTH);
                iconPhoto = new ImageIcon(scaled_image);
            } catch(Exception e) {
                Log.d(LOG_TAG, e.getClass().getName() + ": " + e.getMessage());
                // fall back to placeholder
                iconPhoto = new ImageIcon("src/resource/profile_placeholder.png");
                imagePhoto = iconPhoto.getImage();
                scaled_image = imagePhoto.getScaledInstance(40, 40,
                    Image.SCALE_SMOOTH);
                iconPhoto = new ImageIcon(scaled_image);
            }
        } else {
            // fall back to placeholder
            iconPhoto = new ImageIcon("src/resource/profile_placeholder.png");
            imagePhoto = iconPhoto.getImage();
            scaled_image = imagePhoto.getScaledInstance(40, 40,
                Image.SCALE_SMOOTH);
            iconPhoto = new ImageIcon(scaled_image);
        }

        return iconPhoto;
    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + Control.class.getSimpleName();

    private static Model model = null;
    private static View view = null;
}