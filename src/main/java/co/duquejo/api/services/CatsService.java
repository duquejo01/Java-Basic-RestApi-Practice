package co.duquejo.api.services;

import co.duquejo.api.models.Cat;
import co.duquejo.api.models.FavoriteCat;
import co.duquejo.api.utils.ImagesManager;
import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CatsService {

    public static void getCats() {

        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/images/search")
                .get()
                .build();

        try ( Response response = client.newCall(request).execute() ) {

            ResponseBody responseBody = response.body(); // Parsing body
            Cat[] apiResponse = gson.fromJson( responseBody.string(), Cat[].class );

            for ( Cat cat: apiResponse) {
                try {

                    ImageIcon bgCat = ImagesManager.parseUrl( cat.getUrl() );

                    // Inner menu logic
                    int selectedOption = -1;

                    ArrayList<String> options = new ArrayList<>();
                    options.add("Show another image");
                    options.add("Add it as a favorite");
                    options.add("Return to menu");

                    String catID = cat.getId();

                    Object catPanel = JOptionPane.showInputDialog(
                            null,
                            "Cat options",
                            catID,
                            JOptionPane.INFORMATION_MESSAGE,
                            bgCat,
                            options.toArray(),
                            options.get(0)
                    );

                    selectedOption = options.indexOf( catPanel );
                    switch ( selectedOption ) {
                        case 0:
                            getCats();
                            break;
                        case 1:
                            setFavorite( cat );
                            break;
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } catch ( IOException e ) {
            System.out.println(e);
        }
    }

    public static void setFavorite( Cat cat ) {

        OkHttpClient client = new OkHttpClient();
        Dotenv dotenv = Dotenv.configure().directory("./config" ).load();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create( "{\"image_id\":\"" + cat.getId() + "\"}", mediaType );

        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/favourites")
                .addHeader("Content-Type", "application/json")
                .addHeader("x-api-key", dotenv.get("API_KEY") )
                .post( body )
                .build();

        try ( Response response = client.newCall(request).execute() ) {
            if( response.code() == 200 ) {
                JOptionPane.showMessageDialog( null, "The cat (" + cat.getId() + ") was added!" );
            } else {
                System.out.println( response.code() );
                JOptionPane.showMessageDialog( null, "Something happened, try again." );
            }
        } catch ( IOException e ) {
            System.out.println(e);
        }
    }

    public static void getFavorites() {

        Dotenv dotenv = Dotenv.configure().directory("./config" ).load();
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        Request request = new Request.Builder()
                .url("https://api.thecatapi.com/v1/favourites")
                .addHeader("x-api-key", dotenv.get("API_KEY") )
                .addHeader("Content-Type", "application/json" )
                .get()
                .build();

        try ( Response response = client.newCall(request).execute() ){

            ResponseBody responseBody = response.body(); // Parsing body
            FavoriteCat[] apiResponse = gson.fromJson( responseBody.string(), FavoriteCat[].class );

            ArrayList<ImageIcon> bgCats = new ArrayList<>();
            HashMap<String, Integer> favorite = new HashMap<String, Integer>();

            for ( FavoriteCat fc : apiResponse ) {
                ImageIcon bgCat = ImagesManager.parseUrl( fc.getImage().getUrl() );
                bgCats.add( bgCat );
                favorite.put( "Remove " + fc.getId(), fc.getId() );
            }

            // Combine images
            ImageIcon combinedImages = ImagesManager.mergeImages( bgCats );

            // Default option
            int selectedOption = -1;
            favorite.put( "Return", -1 );

            Object favoritePanel = JOptionPane.showInputDialog(
                    null,
                    "My favorite cats (by order)",
                    "My favorite cats",
                    JOptionPane.INFORMATION_MESSAGE,
                    combinedImages,
                    favorite.keySet().toArray(),
                    1
            );

            if( favorite.get( favoritePanel ) != -1 ) {
                removeFavorite( String.valueOf( favorite.get( favoritePanel ) ) );
            }

        } catch ( IOException e ) {
            System.out.println(e);
        }
    }

    public static void removeFavorite( String favoriteCatID ) {

        Dotenv dotenv = Dotenv.configure().directory("./config" ).load();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url( "https://api.thecatapi.com/v1/favourites/" + favoriteCatID )
                .delete()
                .addHeader("Content-Type", "application/json")
                .addHeader("x-api-key", dotenv.get("API_KEY") )
                .build();

        try (Response response = client.newCall(request).execute() ) {
            if( response.code() == 200 ) {
                JOptionPane.showMessageDialog( null, "The cat (" + favoriteCatID + ") was removed!" );
            } else {
                System.out.println( response.code() );
                JOptionPane.showMessageDialog( null, "Something happened, try again." );
            }
        } catch( IOException e ) {
            System.out.println(e);
        }

    }
}
