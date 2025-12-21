import java.util.*;

/**
 * A Fetcher program that will create an http request gathering information on a website security headers.
 *
 * @author Cornelius Davis
 * @version 0.0.1
 * @implSpec Checks core security headers: CSP, STS, X-Frame-Options, X-Content-Type-Options, Referrer-Policy,
 * Permissions Policy, and X-Xss-Protection.
 * @since 12/13/2025
 */
public class Fetcher {
    private final HashMap<String, List<String>> headerMap;
    private static final Set<String> tldSet = Set.of("com","org","net","edu","gov","io","app","dev","ai","xyz",
            "us","uk","co","me","tv","info","biz");
//    private static final String TLD_RESOURCE_PATH = "/tlds.txt";

    public Fetcher() {
        headerMap = new HashMap<>(7);
        headerMap.put("content-security-policy", null);
        headerMap.put("strict-transport-security", null);
        headerMap.put("x-frame-options", null);
        headerMap.put("x-content-type-options", null);
        headerMap.put("referrer-policy", null);
        headerMap.put("permissions-policy", null);
        headerMap.put("x-xss-protection", null);
    }


//    private void parseHTML(HttpResponse<String> response) throws IOException {
//        String html = response.body();
//        Document doc = Jsoup.parse(html);
//
//        //TODO: What elements do we need to check?
//
//        try (StreamParser stream = Jsoup.connect(response.uri().toURL().toString()).execute().streamParser()) {
//        Element element;
//
//        while ((element = stream.selectNext("")) != null) {
//            // Will include children of query
//            System.out.println();
//            element.remove(); // Clean up and keep memory usage low
//        }
//        } catch (IOException e) {
//            throw new IOException();
//        }
//    }

//    /**
//     * Detects if a cloudflare bot mitigation has been enabled
//     * TODO: Detect challenges of all kinds
//     *
//     * @param headers response headers
//     * @return true if 'cf-mitigated' was detected
//     */
//    private boolean hasChallenge(Map<String, List<String>> headers) {
//        return headers.get("cf-mitigated") != null;
//    }

    /**
     * Runs the program
     */
    protected void run() {
        int input = 0;
        String protocol;

        do {
            displayStartMenu();
            String rawInput = getUserInput();
            try {
                input = Integer.parseInt(rawInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid selection");
                continue;
            }
            if (input < 1 || input > 3) {
                System.out.println("Invalid selection");
                continue;
            }
            switch (input) {
                case 1 -> {
                    protocol = "http";
                }
                case 2 -> {
                    protocol = "https";
                }
                case 3 -> {
//                    TODO: end infinity loop.
                }
            }
        } while (input != 3);

        String web = "";

        do {
        web = getWebsite();

        } while (!tldSet.contains(web));

        Request requestResponse = new Request(protocol, web, headerMap);
        requestResponse.print();
    }

    private String getWebsite(){

        System.out.print("Enter a website: ");
        System.out.printf("""
                Example: google.com or tryhackme.com
                Available domains: %s
                """, tldSet.toArray());

        return getUserInput().trim();
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
     * Display the start menu
     *
     */
    private void displayStartMenu() {
        System.out.println("""
                ===Threat Mapper v0.0.1===
                Choose a protocol:
                1. http
                2. https
                3. EXIT PROGRAM
                """);
    }
}
