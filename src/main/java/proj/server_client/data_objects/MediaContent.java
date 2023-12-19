package proj.server_client.data_objects;
import com.google.gson.*;
import java.io.Serializable;
import java.util.Base64;

public class MediaContent implements Serializable {

    private String titleContent;
    private String lyrics;
    private String filePath;
    private String audiobase64;

    // Constructors

    public MediaContent() {
    }

    public MediaContent(String titleContent, String lyrics, String filePath) {
        this.titleContent = titleContent;
        this.lyrics = lyrics;
        this.filePath = filePath;
    }

    // Getters

    public String getTitleContent() {
        return titleContent;
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

    public void setTitleContent(String titleContent) {
        this.titleContent = titleContent;
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
