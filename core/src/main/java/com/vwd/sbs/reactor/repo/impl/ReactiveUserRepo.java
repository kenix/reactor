/*
* vwd KL
* Created by zzhao on 6/16/16 1:49 PM
*/
package com.vwd.sbs.reactor.repo.impl;

import java.util.List;

import com.google.common.collect.Lists;
import com.vwd.sbs.reactor.domain.User;
import com.vwd.sbs.reactor.repo.ReactiveRepo;
import com.vwd.sbs.reactor.util.ReactorUtil;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author zzhao
 */
public class ReactiveUserRepo implements ReactiveRepo<User> {

    private static final long DELAY_MS_DEFAULT = 50;

    private final long delayMs;

    private final List<User> users;

    public ReactiveUserRepo(long delayMs, User... users) {
        this.delayMs = delayMs;
        this.users = Lists.newArrayList(users);
    }

    public ReactiveUserRepo(User... users) {
        this(DELAY_MS_DEFAULT, users);
    }

    public ReactiveUserRepo(long delayMs) {
        this(delayMs, User.USERS.toArray(new User[0]));
    }

    public ReactiveUserRepo() {
        this(DELAY_MS_DEFAULT, User.USERS.toArray(new User[0]));
    }

    @Override
    public Mono<Void> save(Publisher<User> supplier) {
        return ReactorUtil.withDelay(Mono.from(supplier).doOnNext(this.users::add).then(),
                this.delayMs);
    }

    @Override
    public Mono<User> findFirst() {
        return ReactorUtil.withDelay(Mono.just(this.users.get(0)), this.delayMs);
    }

    @Override
    public Flux<User> findAll() {
        return ReactorUtil.withDelay(Flux.fromIterable(this.users), this.delayMs);
    }

    @Override
    public Mono<User> findById(String id) {
        return ReactorUtil.withDelay(
                Mono.just(
                        this.users
                                .stream()
                                .filter(u -> u.getUsername().equals(id))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("no user with username '" + id + "'"))
                ), this.delayMs);
    }
}
