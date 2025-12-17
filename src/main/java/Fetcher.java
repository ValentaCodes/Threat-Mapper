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
 * A Fetcher program that will create an http request gathering information on a websites security headers.
 *
 * @author Cornelius Davis
 * @version 0.0.1
 * @implSpec Checks core security headers: CSP, STS, X-Frame-Options, X-Content-Type-Options, Referrer-Policy, and
 * Permissions Policy.
 * @since 12/13/2025
 */
public class Fetcher {
    private final HashMap<String, List<String>> headerMap;
    private int redirects = 0;

    public Fetcher() {
        headerMap = new HashMap<>(6);
        headerMap.put("Content-Security-Policy", null);
        headerMap.put("Strict-Transport-Security", null);
        headerMap.put("X-Frame-Options", null);
        headerMap.put("X-Content-Type-Options", null);
        headerMap.put("Referrer-Policy", null);
        headerMap.put("Permissions-Policy", null);
    }

    /**
     * Process headers and check the presence for core security headers like CSP, STS, X-Frame-Options,
     * X-Content-Type-Options, Referrer-Policy
     *
     * @param headers from webpage
     */
    private void processHeaders(HttpHeaders headers) {
        boolean challenge = hasChallenge(headers.map());
        if (challenge) System.out.println("Challenge Detected. Implement Selenium to bypass.");

        headers.map().forEach((keys, values) -> {
            if (headerMap.containsKey(keys)) {
                headerMap.replace(keys, null, values);
            }
        });
    }

    /**
     * Detects if a cloudflare bot mitigation has been enabled
     * TODO: Detect challenges of all kinds
     *
     * @param headers response headers
     * @return true if 'cf-mitigated' was detected
     */
    private boolean hasChallenge(Map<String, List<String>> headers) {
        return headers.get("cf-mitigated") != null;
    }

    /**
     * Runs the program
     */
    protected void run() {
        String proto = displayProtocolOptions();

        System.out.print("Enter a website: ");

        String website = getUserInput();

        HttpClient client = createClient();
        HttpResponse<String> response = createRequest(client, proto, website);
        processHeaders(response.headers());
    }

    /**
     * Get the users' input
     *
     * @return user input
     */
    private String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    /**
     * Display protocol options for the user to select from
     *
     * @return the preferred protocol
     */
    private String displayProtocolOptions() {
        System.out.println("""
                Choose a protocol:
                1. http
                2. https
                """);

        String input = getUserInput();
        int selection;
        try {
            selection = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }

        String protocol;
        switch (selection) {
            case 1 -> protocol = "http";
            case 2 -> protocol = "https";
            default -> protocol = "https";
        }
        return protocol;
    }

    /**
     * Creates and sends an HttpResponse to a target site
     * @param client the client that is making the request
     * @param protocol the desired protocol to test (http, https)
     * @param url the target site
     * @return response returned from target site
     */
    private HttpResponse<String> createRequest(HttpClient client, String protocol, String url) {
        HttpResponse<String> response;
        StringBuilder sb = new StringBuilder(protocol);
        sb.append("://").append(url);

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(sb.toString())).header("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120" +
                            ".0.0.0 Safari/537.36").header("Content-Type", "text/html").header("Accept-Language", "en" +
                    "-US").timeout(Duration.ofSeconds(20)).build();
            long start = System.nanoTime();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            long end = System.nanoTime();

            long elapsedTimeNs = end - start;
            double elapsedTimeSec = elapsedTimeNs / 1000000000.00;

            if (response.previousResponse().get().statusCode() != 200) {
                redirects++;

            }
            System.out.println("Resolved URL: " + response.uri().toURL());
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Elapsed Time (seconds): " + elapsedTimeSec);
            System.out.println("Redirects: " + redirects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    /**
     * Creates the client making the request
     * @return an HttpClient instance
     * @implSpec Sets headers to shyly mimic a user
     */
    private HttpClient createClient() {
        //Store session cookies when we make request
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        return HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).cookieHandler(cookieManager).connectTimeout(Duration.ofSeconds(10)).version(HttpClient.Version.HTTP_2).build();
    }
}
