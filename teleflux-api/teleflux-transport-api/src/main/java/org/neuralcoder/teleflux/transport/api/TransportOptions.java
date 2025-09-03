package org.neuralcoder.teleflux.transport.api;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Immutable, declarative configuration for a Transport implementation.
 * No logic here; builders are validated by implementations at runtime.
 */
@Value
@Builder
public class TransportOptions {

    /** Protocol family (TCP, HTTP2, WS, etc.). */
    ProtocolFamily protocol;

    /** Connect timeout; if empty, implementation default applies. */
    Optional<Duration> connectTimeout;

    /** Idle keep-alive interval; if empty, keep-alives are disabled. */
    Optional<Duration> keepAliveInterval;

    /** Max frame payload (in bytes) accepted by the codec; 0 = impl default. */
    @Builder.Default long maxFrameBytes = 0;

    /** Optional TLS configuration. */
    Optional<Tls> tls;

    /** Optional proxy configuration. */
    Optional<Proxy> proxy;

    /** Reconnect policy (exponential, fixed, off). */
    Optional<ReconnectPolicy> reconnectPolicy;

    /** Additional, implementation-specific flags (key=value). */
    @Singular("flag")
    List<String> flags;

    @Value
    @Builder
    public static class Tls {
        /** Enable TLS. */
        @Builder.Default boolean enabled = true;
        /** SNI host override (optional). */
        String sniHost;
        /** ALPN protocol list (e.g., ["h2", "http/1.1"]). */
        @Singular List<String> alpnProtocols;
        /** Trust store material (implementation-defined reference). */
        String trustRef;
        /** Client key/cert material (implementation-defined reference). */
        String keyRef;
        /** Whether to skip certificate validation (NOT recommended). */
        @Builder.Default boolean insecureSkipVerify = false;
    }

    @Value
    @Builder
    public static class Proxy {
        /** Proxy type (e.g., "SOCKS5", "HTTP"). */
        String type;
        /** Hostname or IP of the proxy. */
        String host;
        /** Port of the proxy. */
        int port;
        /** Optional username/password or token reference. */
        String auth;
        /** Optional no-proxy list ("example.com,10.0.0.0/8"). */
        String noProxy;
    }
}
