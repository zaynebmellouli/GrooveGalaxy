import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.time.Duration;

public class Client {

    public static void startClient(String url) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET() // or .POST(HttpRequest.BodyPublishers.ofString("Your request body")) for POST requests
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response status code: " + response.statusCode());
        System.out.println("Response body: " + response.body());
    }

    public static void main(String[] args) throws Exception {
        // The URL should be the endpoint of your HTTPS server
        String url = "https://localhost:5000/test";
        startClient(url);
    }
}
