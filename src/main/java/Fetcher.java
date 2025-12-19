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
    private static final Set<String> tldSet = Set.of("com", "org", "net", "edu", "gov", "io", "app", "dev", "ai", "xyz",
            "us", "uk", "co", "me", "tv", "info", "biz");
    private final HashMap<String, List<String>> headerMap;
    private int redirects = 0;

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

    /**
     * Runs the program
     */
    protected void run() {

        String protocol = getProtocol();
        String targetWebsite = getWebsite(protocol);
        Request request = new Request(protocol, targetWebsite, headerMap);
        request.print();
        System.out.println(Arrays.toString(request.redirectionStack.getRedirects()));
    }


    private String getWebsite(String proto) {
        System.out.println("Current url: www." + proto + "://");
        System.out.print("Enter a website: ");
        System.out.println("Available domains: " + Arrays.toString(tldSet.toArray()));

        String input = getUserInput().trim();
        String domain = input.substring(input.length() - 3);


        if (!tldSet.contains(domain)) {
            System.out.println("Invalid domain");
            getWebsite(proto);
        }

        return input;
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
     * Get the desired protocol option or end program
     *
     * @return string
     */
    private String getProtocol() {

        int input = 0;

        do {
            displayProtocolMenu();
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
                    return "http";
                }
                case 2 -> {
                    return "https";
                }
                case 3 -> {
                    System.out.println("Terminating program");
                    System.exit(0);
                }
            }
        } while (input != 3);

        return "";
    }


    /**
     * Display the start menu
     */
    private void displayProtocolMenu() {
        System.out.println("""
                ===Threat Mapper v0.0.1===
                Choose a protocol:
                1. http
                2. https
                3. EXIT PROGRAM
                """);
    }
}
