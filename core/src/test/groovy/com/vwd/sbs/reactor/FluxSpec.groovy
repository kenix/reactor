/*
* vwd KL
* Created by zzhao on 6/16/16 10:51 AM
*/
package com.vwd.sbs.reactor

import reactor.core.publisher.Flux
import reactor.core.test.TestSubscriber
import spock.lang.Specification
import spock.lang.Unroll;

/**
 * @author zzhao
 */
class FluxSpec extends Specification {

    def 'flux empty'() {
        when:
        Flux<String> fluxEmpty = Flux.empty()
        then:
        TestSubscriber
                .subscribe(fluxEmpty)
                .assertValueCount(0)
                .assertComplete()
    }

    @Unroll
    def 'flux #method'() {
        when:
        Flux<String> flux = Flux."$method"(arg)
        then:
        TestSubscriber
                .subscribe(flux)
                .assertValues('foo', 'bar')
                .assertComplete()

        where:
        method         | arg
        'from'         | Flux.fromIterable(['foo', 'bar'])
        'fromIterable' | ['foo', 'bar']
        'fromArray'    | ['foo', 'bar'] as String[]
        'fromStream'   | ['foo', 'bar'].stream()
    }

    def 'flux with error'() {
        when:
        Flux<String> flux = Flux.error(new IllegalStateException())
        then:
        TestSubscriber
                .subscribe(flux)
                .assertError(IllegalStateException)
                .assertNotComplete()
    }

    def 'flux never terminate'() {
        when:
        Flux<String> flux = Flux.never()
        then:
        TestSubscriber
                .subscribe(flux)
                .assertNotTerminated()
    }

    def 'flux count each second'() {
        when:
        Flux<Long> flux = Flux.interval(100)
        then:
        TestSubscriber
                .subscribe(flux)
                .assertNotTerminated()
                .awaitAndAssertNextValues(0L, 1L, 2L)
    }
}
