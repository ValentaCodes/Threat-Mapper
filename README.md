# Threat Mapper CLI

A Java CLI that scans websites and generates a human-readable Markdown report on security issues: HTTP headers, CSP directives, and common web risks. Built for learning and practical use—clean, transparent, and focused on empowering creators and builders.

## Features
- **Header Analysis:** Detects presence, configuration, and gaps for core security headers (CSP, HSTS, X-Content-Type-Options, X-Frame-Options, Referrer-Policy, Permissions-Policy).
- **CSP Insights:** Parses directives and flags risky patterns (unsafe-inline, unsafe-eval, wildcard sources).
- **Clear Report Output:** Produces Markdown reports designed for non-experts and teams.
- **CLI Workflow:** Simple commands, structured flags, and readable console output.
- **Extensible Architecture:** Modular analyzers to add checks (cookies, redirects, TLS info) over time.

## Quick Start
1. Install Java 11+ and Gradle.
2. Clone the repo.
3. Build: `./gradlew clean build`
4. Run: `./gradlew run --args="scan https://example.com -o report.md"`

## Usage
- Scan a single site:
  - `threat-mapper scan https://udemy.com -o udemy-report.md`
- Customize timeouts and retries:
  - `threat-mapper scan https://site.com --timeout 10s --retries 2`
- Output to console only:
  - `threat-mapper scan https://site.com`

## Output Example (Markdown)
- Site: `https://example.com`
- Date: `2025-12-16`
- Redirects: `2`
- Status: `200 OK`

### Headers
- Strict-Transport-Security: present, `max-age=31536000; includeSubDomains`
- Content-Security-Policy: present, flags: `unsafe-inline`, wildcard in `img-src`
- X-Content-Type-Options: present, `nosniff`
- X-Frame-Options: missing → risk of clickjacking
- Referrer-Policy: present, `no-referrer-when-downgrade` → recommend `strict-origin-when-cross-origin`

### Recommendations
- Remove `unsafe-inline` from CSP; adopt nonces or hashes.
- Prefer `Referrer-Policy: strict-origin-when-cross-origin`.
- Add `X-Frame-Options: DENY` or CSP `frame-ancestors 'none'`.

## Design
- Core modules:
  - Requester: HTTP client for GET/HEAD with redirects handling.
  - HeaderCollector: gathers and normalizes header values.
  - CSPParser: tokenizes directives and evaluates risks.
  - Analyzer: runs checks and produces findings.
  - Reporter: renders Markdown with a friendly tone and clear actions.
- Tech stack: Java 11+, Gradle (Kotlin DSL), JUnit, jsoup (for HTML parsing where needed).

## Development
- Build: `./gradlew build`
- Test: `./gradlew test`
- Lint/format: `./gradlew spotlessApply` (if configured)
- Run with args: `./gradlew run --args="<command>"`

## Measuring Request Time
Use a monotonic clock for elapsed durations:
- Java: `System.nanoTime()` before and after the request; convert to milliseconds by dividing by 1_000_000. For whole seconds, divide milliseconds by 1,000.

## Roadmap
- Cookie flags (Secure, HttpOnly, SameSite).
- TLS details and certificate checks.
- Per-phase timing (DNS, connect, TLS handshake) via lower-level clients.
- SIEM-friendly JSON output.
- Rulesets tailored for common platforms.

## Ethics & Purpose
This tool aims to educate and elevate—not exploit. Reports emphasize practical fixes and sharing knowledge, especially for independent creators and small teams.

## Contribution
- Fork and open a PR with clear description.
- Add tests for new analyzers.
- Keep reports concise and actionable.

## License
MIT
