import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

public class Request {
    private static final List<String> MISSING = Collections.singletonList("MISSING");
    private static HttpResponse<String> response;
    private static double elapsedTimeSeconds;
    RedirectionStack<Optional<HttpResponse<String>>> redirectionStack = new RedirectionStack<>();
    private HashMap<String, List<String>> map;

    public Request(String protocol, String url, HashMap<String, List<String>> map) {
        this.map = map;

        HttpClient client = createClient();
        HttpRequest request = createRequest(protocol, url);
        response = getResponse(request, client);
        processHeaders(response.headers(), map);
    }

    /**
     * Creates the client making the request
     *
     * @return an HttpClient instance
     * @implSpec Sets headers to shyly mimic a user
     */
    private HttpClient createClient() {
        //Store session cookies when we make request
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        return HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).cookieHandler(cookieManager).connectTimeout(Duration.ofSeconds(10)).version(HttpClient.Version.HTTP_2).build();
    }

    /**
     * Creates and sends an HttpResponse to a target site
     *
     * @param protocol the desired protocol to test (http, https)
     * @param url      the target site
     * @return response returned from target site
     */
    private HttpRequest createRequest(String protocol, String url) {
        HttpRequest request;

        StringBuilder sb = new StringBuilder(protocol);
        sb.append("://").append(url);

        try {
            //Create the request
            request = HttpRequest.newBuilder().uri(URI.create(sb.toString())).header("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120" +
                            ".0.0.0 Safari/537.36").header("Content-Type", "text/html").header("Accept-Language", "en" +
                    "-US").timeout(Duration.ofSeconds(20)).build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    /**
     * Sends a client request
     *
     * @param request Http request
     * @param client  Http client
     * @return a response
     */
    private HttpResponse<String> getResponse(HttpRequest request, HttpClient client) {
        HttpResponse<String> response;
        try {
            // Client sends req and receives response. Track response time
            long start = System.nanoTime();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            long end = System.nanoTime();

            long elapsedTimeNs = end - start;
            elapsedTimeSeconds = elapsedTimeNs / 1000000000.00;

            if (response.previousResponse().isPresent()) {
                redirectionStack.push(response.previousResponse());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
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
     * Process headers and check the presence for core security headers like CSP, STS, X-Frame-Options,
     * X-Content-Type-Options, Referrer-Policy
     *
     * @param responseHeaders from webpage
     */
    private void processHeaders(HttpHeaders responseHeaders, HashMap<String, List<String>> map) {
        boolean challenge = hasChallenge(responseHeaders.map());
        if (challenge) System.out.println("Challenge Detected. Implement Selenium to bypass.");

        // Scan incoming response and only take the header values we need for our 6 core security headers
        responseHeaders.map().forEach((key, valueArray) -> {
            if (map.containsKey(key)) {
                map.put(key, new ArrayList<>(valueArray));
            }
        });

        map.replaceAll((key, value) -> value == null ? MISSING : value);
    }

    protected void print() {

        try {

            System.out.printf("""
                            Status Code: %s
                            Resolved URL: %s
                            Elapsed Time: %s
                            # of Redirects: %d
                            """,
                    response.statusCode(),
                    response.uri().toURL(),
                    elapsedTimeSeconds,
                    redirectionStack.getRedirectCount());
        } catch (MalformedURLException e) {
            throw new RuntimeException();
        }

        map.forEach((k, v) -> {
            if (v.equals(MISSING)) {
                System.out.println(k + ": " + v);
            } else {
                System.out.println(k + ": ");
                System.out.println(v);
            }
        });
    }
}
