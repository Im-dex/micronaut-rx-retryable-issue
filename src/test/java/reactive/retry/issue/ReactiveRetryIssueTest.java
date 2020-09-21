package reactive.retry.issue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import io.micronaut.retry.annotation.Retryable;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class RetryableBean {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Retryable(attempts = "5", delay = "1ms")
    public Mono<String> rx() {
        if (counter.incrementAndGet() > 2) {
            return Mono.just("str");
        } else {
            return Mono.error(new RuntimeException());
        }
    }

    @Retryable(attempts = "5", delay = "1ms")
    public CompletableFuture<String> future() {
        if (counter.incrementAndGet() > 2) {
            return CompletableFuture.completedFuture("str");
        } else {
            return CompletableFuture.supplyAsync(() -> {throw new RuntimeException();});
        }
    }

    @Retryable(attempts = "5", delay = "1ms")
    public String blocking() {
        if (counter.incrementAndGet() > 2) {
            return "str";
        } else {
            throw new RuntimeException();
        }
    }

    public void reset() {
        counter.set(0);
    }
}

@MicronautTest
public class ReactiveRetryIssueTest {
    @Inject
    RetryableBean bean;

    @Test
    void testRx() {
        bean.reset();
        Assertions.assertEquals(bean.rx().block(), "str");
    }

    @Test
    void testFuture() throws ExecutionException, InterruptedException {
        bean.reset();
        Assertions.assertEquals(bean.future().get(), "str");
    }

    @Test
    void testBlocking() {
        bean.reset();
        Assertions.assertEquals(bean.blocking(), "str");
    }
}
