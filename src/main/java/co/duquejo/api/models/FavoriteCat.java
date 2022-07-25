package co.duquejo.api.models;

public class FavoriteCat {
    private int id;
    private String image_id;
    private FavoriteCatImage image;

    public int getId() {
        return id;
    }

    public String getImage_id() {
        return image_id;
    }

    public FavoriteCatImage getImage() {
        return image;
    }
}
