import java.io.IOException;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {


        Fetcher fetch = new Fetcher("https://github.com");

    }
//
//    private static void getHeaderInformation(URL url) throws IOException {
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
//        con.connect();
//        Object conType = con.getContentType();
//        for (int i = 0; i < con.getHeaderFields().size(); i++) {
//            System.out.println(con.getHeaderField(i));
//        }
//    }

    private static void print(URI uri) {
        System.out.printf("""
                               ------------------------------
                               [scheme:] scheme-specific-part[#fragment]
                               ------------------------------
                               Scheme: %s
                               Scheme-specific part:  %s
                               Authority: %s
                               User info: %s
                               Host: %s
                               Port: %d
                               Path: %s
                               Query: %s
                             Fragment: %s
                        """,
                uri.getScheme(),
                uri.getSchemeSpecificPart(),
                uri.getAuthority(),
                uri.getUserInfo(),
                uri.getHost(),
                uri.getPort(),
                uri.getPath(),
                uri.getQuery(),
                uri.getFragment()
        );
    }
}
