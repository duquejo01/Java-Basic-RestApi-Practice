package co.duquejo.api.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImagesManager {
    public static ImageIcon parseUrl( String url ) {

        ImageIcon imageIcon = null;
        try {

            URL imageUrl = new URL( url );
            HttpURLConnection httpConnection = (HttpURLConnection) imageUrl.openConnection(); // Fixing https issues
            httpConnection.addRequestProperty( "User-Agent", "" ); // Fixing https issues
            BufferedImage bufferedImage = ImageIO.read( httpConnection.getInputStream() );
            imageIcon = new ImageIcon( bufferedImage );

            // Check cat width
            if( imageIcon.getIconWidth() > 800 ) {
                java.awt.Image bg = imageIcon.getImage();
                java.awt.Image rescaledImage = bg.getScaledInstance(800, 600, java.awt.Image.SCALE_SMOOTH );
                imageIcon = new ImageIcon( rescaledImage );
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return imageIcon;
    }

    public static ImageIcon mergeImages(ArrayList<ImageIcon> images ) {

        int width = 0;
        int height = 0;
        int posX = 0;
        ArrayList<ImageIcon> resizedImages = new ArrayList<>();

        for ( ImageIcon image: images ) {

            if( image.getIconWidth() > 300 ) {
                Image rescaledImage = image.getImage().getScaledInstance(300, 250, Image.SCALE_SMOOTH );
                image = new ImageIcon( rescaledImage );
            }
            resizedImages.add( image );

            width += image.getIconWidth();
            height = Math.max( height, image.getIconHeight() );
        }

        final BufferedImage combinedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g = combinedImage.createGraphics();

        for ( ImageIcon ri: resizedImages ) {
            g.drawImage( ri.getImage(), posX, 0, null );
            posX += ri.getIconWidth(); // Update posX
        }
        g.dispose();

        return new ImageIcon( combinedImage );
    }
}
