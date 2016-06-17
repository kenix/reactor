/*
* vwd KL
* Created by zzhao on 6/16/16 4:19 PM
*/
package com.vwd.sbs.reactor

import com.vwd.sbs.reactor.domain.User
import com.vwd.sbs.reactor.repo.ReactiveRepo
import com.vwd.sbs.reactor.repo.impl.ReactiveUserRepo
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.test.TestSubscriber
import spock.lang.Shared
import spock.lang.Specification

import static com.vwd.sbs.reactor.MergeSpec.MARIE
import static com.vwd.sbs.reactor.MergeSpec.MIKE

/**
 * @author zzhao
 */
class OperationSpec extends Specification {

    @Shared
    ReactiveRepo<User> repo = new ReactiveUserRepo()

    def 'op zip'() {
        given:
        Flux<String> fluxUsername = this.repo.findAll().map({ u -> u.username })
        Flux<String> fluxFirstname = this.repo.findAll().map({ u -> u.firstname })
        Flux<String> fluxLastname = this.repo.findAll().map({ u -> u.lastname })

        when:
        Flux<User> flux = Flux.zip(fluxUsername, fluxFirstname, fluxLastname)
                .map({ t -> new User(t.t1, t.t2, t.t3) })
        then:
        TestSubscriber
                .subscribe(flux)
                .await()
                .assertValues(User.USERS.toArray(new User[0]))
                .assertComplete()
    }

    def 'fastest mono'() {
        when:
        Mono<User> mono = Mono.first(mono1, mono2)
        then:
        TestSubscriber
                .subscribe(mono)
                .await()
                .assertValues(val)
                .assertComplete()

        where:
        mono1                                        | mono2                                       | val
        new ReactiveUserRepo(MARIE).findFirst()      | new ReactiveUserRepo(100, MIKE).findFirst() | MARIE
        new ReactiveUserRepo(100, MARIE).findFirst() | new ReactiveUserRepo(MIKE).findFirst()      | MIKE
    }

    def 'fastest flux'() {
        when:
        Flux<User> flux = Flux.firstEmitting(flux1, flux2)
        then:
        TestSubscriber
                .subscribe(flux)
                .await()
                .assertValues(val)
                .assertComplete()

        where:
        flux1                                            | flux2                               | val
        new ReactiveUserRepo(MARIE, MIKE).findAll()      | new ReactiveUserRepo(100).findAll() | [MARIE, MIKE] as User[]
        new ReactiveUserRepo(100, MARIE, MIKE).findAll() | new ReactiveUserRepo().findAll()    | User.USERS.toArray(new User[0])
    }

    def 'complete signal of flux'() {
        when:
        Mono<Void> mono = this.repo.findAll().then()
        then:
        TestSubscriber
                .subscribe(mono)
                .assertNotTerminated()
                .await()
                .assertNoValues()
                .assertComplete()
    }

    def 'mono fallback'() {
        when:
        Mono<User> mono = theMono.otherwise({ e -> Mono.just(MARIE) })
        then:
        TestSubscriber
                .subscribe(mono)
                .assertValues(val)
                .assertComplete()

        where:
        theMono                                 | val
        Mono.error(new IllegalStateException()) | MARIE
        Mono.just(MIKE)                         | MIKE
    }

    def 'flux fallback'() {
        when:
        Flux<User> flux = theFlux.onErrorResumeWith({ e -> Flux.just(MARIE, MIKE) })
        then:
        TestSubscriber
                .subscribe(flux)
                .await()
                .assertValues(vals)
                .assertComplete()

        where:
        theFlux                                 | vals
        Flux.error(new IllegalStateException()) | [MARIE, MIKE] as User[]
        this.repo.findAll()                     | User.USERS.toArray(new User[0])
    }
}
