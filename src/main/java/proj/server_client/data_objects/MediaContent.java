package proj.server_client.data_objects;
import com.google.gson.*;
import java.io.Serializable;
import java.util.Base64;

public class MediaContent implements Serializable {

    private int mediaId;
    private String lyrics;
    private String filePath;
    private String audiobase64;

    // Constructors

    public MediaContent() {
    }

    public MediaContent(int mediaId, String lyrics, String filePath) {
        this.mediaId = mediaId;
        this.lyrics = lyrics;
        this.filePath = filePath;
    }

    // Getters

    public int getMediaId() {
        return mediaId;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getFilePath() {
        return filePath;
    }

    //public String getAudiobase64(){ return audiobase64;}
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
    //public void setAudiobase64(String audiobase64) {
      //  this.audiobase64 = audiobase64;
    //}


    public String toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("title_content", this.getTitleContent());
        json.addProperty("lyrics", this.getLyrics());
        json.addProperty("file_path", this.getFilePath());
        return json.toString();
    }
}
