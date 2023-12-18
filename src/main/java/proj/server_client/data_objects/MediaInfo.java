package proj.server_client.data_objects;
import org.json.JSONObject;
import java.io.Serializable;
public class MediaInfo implements Serializable {
        public static JSONObject toJson(Media media, MediaContent mediaContent) {
            JSONObject json = new JSONObject();
            json.put("media", media.toJson());
            json.put("media_content", mediaContent.toJson());
            return json;
        }
}
