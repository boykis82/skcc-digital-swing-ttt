package org.caltech.miniswing.util;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.function.Supplier;

public class AsyncHelper {
    private final Scheduler scheduler;

    @Autowired
    public AsyncHelper(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public <T> Flux<T> flux(Supplier<Publisher<T>> publisherSupplier) {
        return Flux.defer(publisherSupplier).subscribeOn(scheduler);
    }

    public <T> Mono<T> mono(Supplier<Mono<T>> supplier) {
        return Mono.defer(supplier).subscribeOn(scheduler);
    }
}
