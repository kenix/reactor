/*
* vwd KL
* Created by zzhao on 6/16/16 5:11 PM
*/
package com.vwd.sbs.reactor

import com.vwd.sbs.reactor.domain.User
import com.vwd.sbs.reactor.repo.ReactiveRepo
import com.vwd.sbs.reactor.repo.impl.ReactiveUserRepo
import reactor.core.converter.RxJava1ObservableConverter
import reactor.core.converter.RxJava1SingleConverter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.test.TestSubscriber
import rx.Observable
import rx.Single
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.CompletableFuture;

/**
 * @author zzhao
 */
class ConversionSpec extends Specification {

    @Shared
    ReactiveRepo<User> repo = new ReactiveUserRepo()

    def 'conversion between flux and observable'() {
        when:
        Flux<User> flux = this.repo.findAll()
        Observable<User> observable = RxJava1ObservableConverter.from(flux)
        then:
        TestSubscriber
                .subscribe(RxJava1ObservableConverter.from(observable))
                .await()
                .assertValues(User.USERS.toArray(new User[0]))
                .assertComplete()
    }

    def 'conversion between mono and single'() {
        when:
        Mono<User> mono = this.repo.findFirst()
        Single<User> single = RxJava1SingleConverter.from(mono)
        then:
        TestSubscriber
                .subscribe(RxJava1SingleConverter.from(single))
                .await()
                .assertValues(User.SKYLER)
                .assertComplete()
    }

    def 'conversion between mono and completable future'() {
        when:
        Mono<User> mono = this.repo.findFirst()
        CompletableFuture<User> future = mono.toFuture()
        then:
        TestSubscriber
                .subscribe(Mono.fromFuture(future))
                .await()
                .assertValues(User.SKYLER)
                .assertComplete()
    }
}
