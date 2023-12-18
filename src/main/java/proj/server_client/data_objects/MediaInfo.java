package proj.server_client.data_objects;
import com.google.gson.*;
import java.io.Serializable;
public class MediaInfo implements Serializable {
        public static JsonObject toJson(Media media, MediaContent mediaContent) {
            JsonObject json = new JsonObject();
            json.addProperty("media", media.toJson());
            json.addProperty("media_content", mediaContent.toJson());
            return json;
        }
}
