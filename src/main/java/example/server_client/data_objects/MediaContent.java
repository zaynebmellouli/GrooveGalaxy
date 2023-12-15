package example.server_client.data_objects;

import java.io.Serializable;

public class MediaContent implements Serializable {

    private int mediaId;
    private String lyrics;
    private String audioBase64;

    // Constructors

    public MediaContent() {
    }

    public MediaContent(int mediaId, String lyrics, String audioBase64) {
        this.mediaId = mediaId;
        this.lyrics = lyrics;
        this.audioBase64 = audioBase64;
    }

    // Getters

    public int getMediaId() {
        return mediaId;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getAudioBase64() {
        return audioBase64;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public void setAudioBase64(String audioBase64) {
        this.audioBase64 = audioBase64;
    }
}
