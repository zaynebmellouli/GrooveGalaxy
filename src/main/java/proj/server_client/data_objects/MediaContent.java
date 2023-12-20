package proj.server_client.data_objects;
import com.google.gson.*;
import java.io.Serializable;
import java.util.Base64;

public class MediaContent implements Serializable {
    private int mediaContentId;
    private String titleContent;
    private int ownerId;
    private String lyrics;
    private String filePath;


    public MediaContent() {
    }

    public MediaContent(int mediaContentId, int ownerId,String titleContent, String lyrics, String filePath) {
        this.mediaContentId= mediaContentId;
        this.ownerId = ownerId;
        this.titleContent = titleContent;
        this.lyrics = lyrics;
        this.filePath = filePath;
    }

    // Getters


    public int getMediaContentId() {
        return mediaContentId;
    }

    public String getTitleContent() {
        return titleContent;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getOwnerId(){ return ownerId;}
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setTitleContent(String titleContent) {
        this.titleContent = titleContent;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public void setMediaContentId(int mediaContentId) {
        this.mediaContentId = mediaContentId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("media_content_id", this.getMediaContentId());
        json.addProperty("title_content", this.getTitleContent());
        json.addProperty("owner_id", this.getOwnerId());
        json.addProperty("lyrics", this.getLyrics());
        json.addProperty("file_path", this.getFilePath());
        return json.toString();
    }
}
