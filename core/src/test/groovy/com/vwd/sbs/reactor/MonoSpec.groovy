/*
* vwd KL
* Created by zzhao on 6/16/16 11:39 AM
*/
package com.vwd.sbs.reactor

import reactor.core.publisher.Mono
import reactor.core.test.TestSubscriber
import spock.lang.Specification;

/**
 * @author zzhao
 */
class MonoSpec extends Specification {

    def 'mono empty'() {
        when:
        Mono<String> mono = Mono.empty()
        then:
        TestSubscriber
                .subscribe(mono)
                .assertValueCount(0)
                .assertComplete()
    }

    def 'mono value'() {
        when:
        Mono<String> mono = Mono.just('foo')
        then:
        TestSubscriber
                .subscribe(mono)
                .assertValues('foo')
                .assertComplete()
    }

    def 'mono error'() {
        when:
        Mono<String> mono = Mono.error(new IllegalStateException())
        then:
        TestSubscriber
                .subscribe(mono)
                .assertError(IllegalStateException)
                .assertNotComplete()
    }
}
