/*
* vwd KL
* Created by zzhao on 6/16/16 1:55 PM
*/
package com.vwd.sbs.reactor.util;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author zzhao
 */
public final class ReactorUtil {

    private ReactorUtil() {
        throw new AssertionError("not for instantiation or inheritance");
    }

    public static <T> Mono<T> withDelay(Mono<T> mono, long ms) {
        return Mono.delay(ms).then(c -> mono);
    }

    public static <T> Flux<T> withDelay(Flux<T> mono, long ms) {
        return Flux.interval(ms).zipWith(mono, (i, t) -> t);
    }
}
