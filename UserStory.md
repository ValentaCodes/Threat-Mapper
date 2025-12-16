# Threat Mapper

*v.0.0.1*

## User Story

As a beginner-friendly security tester running a website scan, I want a simple CLI tool that fetches a given URL and
reports the presence and values of core security headers.

- Content-Security-Policy: nonces with hashes and unsafe-inline
- Strict-Transport-Security: with includeSubDomains and preload
- X-Frame-Options: DENY or CSP frame-ancestors
- X-Content-Type-Options:  noSniff
- Referrer-Policy: no-referrer or strict-origin-when-cross-origin
- Permissions Policy: disabling unneeded features

So that I can quickly identify basic configuration gaps without parsing the DOM or dealing with complex setup.

## Criteria

- Given a valid URL,
    - WHEN I run the tool, THEN it should:
        - Perform an HTTP GET with redirects enabled and a clear timeout.
        - Print the final resolved URL, HTTP status code, and elapsed time.
        - List each core security header as “present” with its value or “missing” if not set.
        - Exit with code 0 on success, 2 on network/timeout errors, and 1 on invalid input.
- Given an invalid URL
    - WHEN I run the tool, THEN it should print a helpful error message and exit with code 1.
- Given a reachable URL that returns non-HTML
    - WHEN I run the tool, THEN it should still check headers and report them.
    - WHEN a network error occurs (e.g., timeout, TLS error), THEN the tool should print the error type and exit with
      code 2.

## Tools Used

- Java

## Planned upgrades for v0.0.2

- Use Selenium to bypass bot-mitigation