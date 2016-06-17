/*
* vwd KL
* Created by zzhao on 6/16/16 1:43 PM
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

import java.util.stream.Collectors

/**
 * @author zzhao
 */
class TransformSpec extends Specification {

    @Shared
    ReactiveRepo<User> userRepo = new ReactiveUserRepo()

    def 'transform mono'() {
        when:
        Mono<User> mono = this.userRepo.findFirst()
        then:
        TestSubscriber
                .subscribe(mono.map(TransformSpec.&up))
                .await()
                .assertValues(new User('SWHITE', 'SKYLER', 'WHITE'))
                .assertComplete()
    }

    private static User up(User u) {
        u.upperCase()
    }

    def 'transform flux'() {
        when:
        Flux<User> flux = this.userRepo.findAll()
        then:
        TestSubscriber
                .subscribe(flux.map(TransformSpec.&up))
                .await()
                .assertValues(users())
                .assertComplete()
    }

    private static User[] users() {
        User.USERS
                .stream()
                .map(TransformSpec.&up)
                .collect(Collectors.toList()).toArray(new User[0])
    }

    def 'transform flux async'() {
        when:
        Flux<User> flux = this.userRepo.findAll()
        then:
        TestSubscriber
                .subscribe(flux.flatMap(TransformSpec.&capUserAsync))
                .await()
                .assertValues(users())
                .assertComplete()
    }

    private static Mono<User> capUserAsync(User u) {
        Mono.just(u.upperCase())
    }
}
