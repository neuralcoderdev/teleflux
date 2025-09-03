package org.neuralcoder.teleflux.core.api.options;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import org.neuralcoder.teleflux.core.api.policy.ratelimit.RateLimitPolicy;
import org.neuralcoder.teleflux.core.api.policy.retry.RetryPolicy;
import org.neuralcoder.teleflux.core.api.session.SessionStore;
import org.neuralcoder.teleflux.core.api.util.Clock;
import org.neuralcoder.teleflux.core.api.util.Scheduler;

import java.time.Duration;
import java.util.Map;

@Value
@Builder
public class TelegramClientOptions {

    @NonNull
    SessionStore sessionStore;
    @NonNull
    RetryPolicy retryPolicy;
    @NonNull
    RateLimitPolicy rateLimitPolicy;
    @NonNull
    Clock clock;
    @NonNull
    Scheduler scheduler;
    @NonNull
    Duration requestTimeout;
    @NonNull
    AppInfo appInfo;

    @Value
    @Builder
    public static class AppInfo {
        @NonNull
        Integer apiId;
        @NonNull
        String apiHash;
        @Builder.Default
        String device = "Teleflux";
        @Builder.Default
        String systemLangCode = "en";
        @Builder.Default
        String appVersion = "0.1.0";
        @Singular
        Map<String, String> extras;
    }
}
