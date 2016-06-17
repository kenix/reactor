/*
* vwd KL
* Created by zzhao on 6/16/16 1:46 PM
*/
package com.vwd.sbs.reactor.repo;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author zzhao
 */
public interface ReactiveRepo<T> {

    Mono<Void> save(Publisher<T> supplier);

    Mono<T> findFirst();

    Flux<T> findAll();

    Mono<T> findById(String id);
}
