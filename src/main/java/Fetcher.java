import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


/**
 * Representation of an HTTP object that will fetch and collect headers/cookies/forms
 *
 * @implSpec Pull website headers, HTML, and metadata to analyze.
 */
public class Fetcher {
    private HashMap<String, Boolean> headers;

    public Fetcher(String string) throws IOException, InterruptedException {

        //Store session cookies when we make request
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .cookieHandler(cookieManager)
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_2)
                .build();

        //Set basic user-like headers
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(string))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Content-Type", "text/html")
                .header("Accept-Language", "en-US")
                .timeout(Duration.ofSeconds(20))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        processHeaders(response.headers());
    }


    /**
     * Process headers and check the presence for core security headers like CSP, STS, X-Frame-Options,
     * X-Content-Type-Options, Referrer-Policy
     *
     * @param headers from webpage
     */
    private void processHeaders(HttpHeaders headers) {
        hasChallange(headers.map());
        headers.map().forEach((keys, values) -> {
            System.out.printf("""
                    %s : %s
                    """, keys, values);
        });
    }

    private boolean hasChallange(Map<String, List<String>> headers) {
        if (headers.get("cf-mitigated") != null) {
            System.out.println("Cloudflare Challenge Detected");
            return true;
        }
        return false;
    }

    private String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void welcomeStart() {
    }

}
