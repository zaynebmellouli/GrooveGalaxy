package proj.server_client.data_objects;

import java.io.Serializable;

public class Media implements Serializable{

    private int mediaId;
    private int ownerId;
    private String format;
    private String artist;
    private String title;
    private String genre;

    // Constructors

    public Media() {
    }

    public Media(int mediaId, int ownerId, String format, String artist, String title, String genre) {
        this.mediaId = mediaId;
        this.ownerId = ownerId;
        this.format = format;
        this.artist = artist;
        this.title = title;
        this.genre = genre;
    }

    // Getters

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
        this.mediaId=mediaId;
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
}
