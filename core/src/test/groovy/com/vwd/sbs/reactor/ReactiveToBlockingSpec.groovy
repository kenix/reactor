/*
* vwd KL
* Created by zzhao on 6/16/16 4:58 PM
*/
package com.vwd.sbs.reactor

import com.vwd.sbs.reactor.domain.User
import com.vwd.sbs.reactor.repo.ReactiveRepo
import com.vwd.sbs.reactor.repo.impl.ReactiveUserRepo
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Collectors

/**
 * @author zzhao
 */
class ReactiveToBlockingSpec extends Specification {

    @Shared
    ReactiveRepo<User> repo = new ReactiveUserRepo()

    def mono() {
        when:
        def mono = this.repo.findFirst()
        then:
        User.SKYLER == mono.block()
    }

    def flux() {
        when:
        def flux = this.repo.findAll()
        then:
        User.USERS == flux.toStream().collect(Collectors.toList())
    }
}
