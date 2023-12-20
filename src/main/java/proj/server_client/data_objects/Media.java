package proj.server_client.data_objects;
import com.google.gson.*;
import java.io.Serializable;
import java.util.Base64;

public class Media implements Serializable{

    private int mediaId;
    private String title;
    private int ownerId;
    private String format;
    private String artist;
    private String genre;

    // Constructors

    public Media() {
    }

    public Media (int media_id, int ownerId, String format, String artist, String title, String genre) {
        this.mediaId=media_id;
        this.ownerId = ownerId;
        this.format = format;
        this.artist = artist;
        this.title = title;
        this.genre = genre;
    }

    public int getMediaId() {
        return mediaId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getFormat() {
        return format;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId=ownerId;
    }

    public void setFormat(String format) {
        this.format=format;
    }

    public void setArtist(String artist) {
        this.artist=artist;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public void setGenre(String genre) {
        this.genre=genre;
    }

    public String toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("media_id", this.getMediaId());
        json.addProperty("title", this.getTitle());
        json.addProperty("owner_id", this.getOwnerId());
        json.addProperty("format", this.getFormat());
        json.addProperty("artist", this.getArtist());
        json.addProperty("genre", this.getGenre());
        return json.toString();
    }
}
