/*
* vwd KL
* Created by zzhao on 6/16/16 5:42 PM
*/
package com.vwd.sbs.reactor.repo.impl;

import com.vwd.sbs.reactor.domain.User;
import com.vwd.sbs.reactor.repo.BlockingRepo;
import com.vwd.sbs.reactor.repo.ReactiveRepo;
import reactor.core.publisher.Mono;

/**
 * @author zzhao
 */
public class BlockingUserRepo implements BlockingRepo<User> {

    private final ReactiveRepo<User> delegate;

    public BlockingUserRepo(long delayMs, User... users) {
        this.delegate = new ReactiveUserRepo(delayMs, users);
    }

    public BlockingUserRepo(long delayMs) {
        this.delegate = new ReactiveUserRepo(delayMs);
    }

    public BlockingUserRepo(User... users) {
        this.delegate = new ReactiveUserRepo(users);
    }

    public BlockingUserRepo() {
        this.delegate = new ReactiveUserRepo();
    }

    @Override
    public void save(User value) {
        this.delegate.save(Mono.just(value)).block();
    }

    @Override
    public User findFirst() {
        return this.delegate.findFirst().block();
    }

    @Override
    public Iterable<User> findAll() {
        return this.delegate.findAll().toIterable();
    }

    @Override
    public User findById(String id) {
        return this.delegate.findById(id).block();
    }
}
