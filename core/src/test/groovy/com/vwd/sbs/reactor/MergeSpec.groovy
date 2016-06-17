/*
* vwd KL
* Created by zzhao on 6/16/16 2:45 PM
*/
package com.vwd.sbs.reactor

import com.vwd.sbs.reactor.domain.User
import com.vwd.sbs.reactor.repo.ReactiveRepo
import com.vwd.sbs.reactor.repo.impl.ReactiveUserRepo
import reactor.core.publisher.Flux
import reactor.core.test.TestSubscriber
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author zzhao
 */
class MergeSpec extends Specification {

    public static final User MARIE = new User('mschrader', 'Marie', 'Schrader')

    public static final User MIKE = new User('mehrmantraut', 'Mike', 'Ehrmantraut')

    @Shared
    ReactiveRepo<User> repo1 = new ReactiveUserRepo(100)

    @Shared
    ReactiveRepo<User> repo2 = new ReactiveUserRepo(MARIE, MIKE)

    def 'merge with interleave'() {
        when:
        Flux<User> flux = this.repo1.findAll().mergeWith(this.repo2.findAll())
        then:
        TestSubscriber
                .subscribe(flux)
                .await()
                .assertValues(MARIE, MIKE, User.SKYLER, User.JESSE, User.WALTER, User.SAUL)
                .assertComplete()
    }

    def 'merge without interleave'() {
        when:
        Flux<User> flux = this.repo1.findAll().concatWith(this.repo2.findAll())
        then:
        TestSubscriber
                .subscribe(flux)
                .await()
                .assertValues(User.SKYLER, User.JESSE, User.WALTER, User.SAUL, MARIE, MIKE)
                .assertComplete()
    }

    def 'merge monos'() {
        given:
        def skyler = this.repo1.findFirst()
        def marie = this.repo2.findFirst()

        when:
        Flux<User> flux = skyler.concatWith(marie)
        then:
        flux.subscribe(System.out.&println)
        TestSubscriber
                .subscribe(flux)
                .await()
                .assertValues(User.SKYLER, MARIE)
                .assertComplete()
    }
}
