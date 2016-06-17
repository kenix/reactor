/*
* vwd KL
* Created by zzhao on 6/16/16 5:39 PM
*/
package com.vwd.sbs.reactor

import com.google.common.collect.Lists
import com.vwd.sbs.reactor.domain.User
import com.vwd.sbs.reactor.repo.impl.BlockingUserRepo
import com.vwd.sbs.reactor.repo.impl.ReactiveUserRepo
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.core.test.TestSubscriber
import spock.lang.Specification;

/**
 * @author zzhao
 */
class BlockingToReactiveSpec extends Specification {

    def 'handling null'() {
        when:
        Mono<User> mono = Mono.justOrEmpty(User.SKYLER)
        then:
        TestSubscriber
                .subscribe(mono)
                .assertValues(User.SKYLER)
                .assertComplete()

        when:
        mono = Mono.justOrEmpty(null)
        then:
        TestSubscriber
                .subscribe(mono)
                .assertNoValues()
                .assertComplete()
    }

    def 'slow publisher and fast subscriber'() {
        given:
        def repo = new BlockingUserRepo()

        when:
        def flux = Flux.defer({
            Flux.fromIterable(repo.findAll()).subscribeOn(Schedulers.elastic())
        })
        then:
        TestSubscriber
                .subscribe(flux)
                .assertNotTerminated()
                .await()
                .assertValues(User.USERS.toArray(new User[0]))
                .assertComplete()
    }

    def 'fast publisher and slow subscriber'() {
        given:
        def rxRepo = new ReactiveUserRepo()
        def bkRepo = new BlockingUserRepo(new User[0])

        when:
        def mono = rxRepo.findAll().publishOn(Schedulers.parallel())
                .doOnNext({ u -> bkRepo.save(u) })
                .then()
        then:
        TestSubscriber
                .subscribe(mono)
                .assertNotTerminated()
                .await()
                .assertComplete()
        User.USERS == Lists.newArrayList(bkRepo.findAll())
    }
}
