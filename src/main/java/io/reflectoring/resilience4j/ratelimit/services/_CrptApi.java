package io.reflectoring.resilience4j.ratelimit.services;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.function.Supplier;

public class _CrptApi {

    @Getter @Setter
    private int requestLimit;
    @Getter @Setter
    private Duration timeRefreshPeriod;
    @Getter
    private Duration timeDuration;

    @Getter @Setter
    private int countMaxAttempts = 2;
    @Getter @Setter
    private Duration timeWaitDuration;

    private Supplier<String> rateLimitedSupplier;
    private Supplier<String> retryingSupplier;

    public _CrptApi(Duration timeRefreshPeriod, int requestLimit) {
        this.timeRefreshPeriod = timeRefreshPeriod;
        this.requestLimit = requestLimit;
        this.timeWaitDuration = timeRefreshPeriod;
    }

    public _CrptApi setTimeDuration(Duration timeDuration) {
        this.timeDuration = timeDuration;
        return this;
    }

    public _CrptApi addLogic(Supplier<String> mainLogic) {
        configuration(mainLogic);
        return this;
    }

    public void configuration(Supplier<String> logic) {
        RateLimiterConfig config = RateLimiterConfig.custom().
                limitForPeriod(requestLimit).
                limitRefreshPeriod(timeRefreshPeriod).
                timeoutDuration(timeDuration).build();
        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        RateLimiter rateLimiter = registry.rateLimiter("Send document");

        RetryConfig retryConfig = RetryConfig.custom().maxAttempts(countMaxAttempts).
                waitDuration(timeWaitDuration).build();
        RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);
        Retry retry = retryRegistry.retry("Send document", retryConfig);
        retry.getEventPublisher().onRetry(System.out::println);
        retry.getEventPublisher().onSuccess(System.out::println);

        rateLimitedSupplier = RateLimiter.decorateSupplier(rateLimiter, logic);
        retryingSupplier = Retry.decorateSupplier(retry, rateLimitedSupplier);
    }

    public void retryAndRateLimit() {
        System.out.println(retryingSupplier.get());
        System.out.println();
    }
}
