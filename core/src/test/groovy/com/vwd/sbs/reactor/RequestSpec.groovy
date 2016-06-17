/*
* vwd KL
* Created by zzhao on 6/16/16 3:19 PM
*/
package com.vwd.sbs.reactor

import com.vwd.sbs.reactor.domain.User
import com.vwd.sbs.reactor.repo.ReactiveRepo
import com.vwd.sbs.reactor.repo.impl.ReactiveUserRepo
import reactor.core.publisher.Flux
import reactor.core.test.TestSubscriber
import spock.lang.Shared
import spock.lang.Specification;

/**
 * @author zzhao
 */
class RequestSpec extends Specification {

    @Shared
    ReactiveRepo<User> repo = new ReactiveUserRepo()

    def 'request no value'() {
        when:
        Flux<User> flux = this.repo.findAll()
        TestSubscriber<User> testSubscriber = TestSubscriber.subscribe(flux, 0)
        then:
        testSubscriber
                .await()
                .assertNoValues()
    }

    def 'request value one by one'() {
        when:
        Flux<User> flux = this.repo.findAll()
        TestSubscriber<User> testSubscriber = TestSubscriber.subscribe(flux, 0)
        then:
        testSubscriber.assertValueCount(0)

        when:
        testSubscriber.request(1)
        then:
        testSubscriber
                .awaitAndAssertNextValues(User.SKYLER)
                .assertNotTerminated()

        when:
        testSubscriber.request(1)
        then:
        testSubscriber
                .awaitAndAssertNextValues(User.JESSE)
                .assertNotTerminated()

        when:
        testSubscriber.request(1)
        then:
        testSubscriber
                .awaitAndAssertNextValues(User.WALTER)
                .assertNotTerminated()

        when:
        testSubscriber.request(1)
        then:
        testSubscriber
                .awaitAndAssertNextValues(User.SAUL)
                .assertComplete()
    }

    def 'experiment with log'() {
        when:
        Flux<User> flux = this.repo.findAll().log()
        TestSubscriber<User> testSubscriber = TestSubscriber.subscribe(flux, 0)
        then:
        testSubscriber.assertValueCount(0)

        when:
        testSubscriber.request(1)
        then:
        testSubscriber
                .awaitAndAssertNextValues(User.SKYLER)
                .assertNotTerminated()

        when:
        testSubscriber.request(1)
        then:
        testSubscriber
                .awaitAndAssertNextValues(User.JESSE)
                .assertNotTerminated()

        when:
        testSubscriber.request(1)
        then:
        testSubscriber
                .awaitAndAssertNextValues(User.WALTER)
                .assertNotTerminated()

        when:
        testSubscriber.request(1)
        then:
        testSubscriber
                .awaitAndAssertNextValues(User.SAUL)
                .assertComplete()
    }

    def 'experiment with doOnXXX'() {
        when:
        Flux<User> flux = this.repo.findAll()
                .doOnSubscribe({ s -> System.out.println('starring:') })
                .doOnNext({ p -> System.out.println(p.firstname + " " + p.lastname) })
                .doOnComplete({ System.out.println('The end!') })
        TestSubscriber<User> testSubscriber = TestSubscriber.subscribe(flux, 0)
        then:
        testSubscriber.assertValueCount(0)

        when:
        testSubscriber.request(1)
        then:
        testSubscriber
                .awaitAndAssertNextValues(User.SKYLER)
                .assertNotTerminated()

        when:
        testSubscriber.request(1)
        then:
        testSubscriber
                .awaitAndAssertNextValues(User.JESSE)
                .assertNotTerminated()

        when:
        testSubscriber.request(1)
        then:
        testSubscriber
                .awaitAndAssertNextValues(User.WALTER)
                .assertNotTerminated()

        when:
        testSubscriber.request(1)
        then:
        testSubscriber
                .awaitAndAssertNextValues(User.SAUL)
                .assertComplete()
    }
}
