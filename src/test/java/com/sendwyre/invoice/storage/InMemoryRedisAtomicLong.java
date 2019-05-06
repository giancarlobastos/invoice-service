package com.sendwyre.invoice.storage;

import org.mockito.Mockito;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.concurrent.atomic.AtomicLong;

public class InMemoryRedisAtomicLong extends RedisAtomicLong {

    private AtomicLong atomicLong;

    public InMemoryRedisAtomicLong() {
        super("InMemoryRedisAtomicLong", Mockito.mock(RedisConnectionFactory.class, Mockito.RETURNS_DEEP_STUBS));
        this.atomicLong = new AtomicLong();
    }

    @Override
    public long incrementAndGet() {
        return atomicLong.incrementAndGet();
    }
}
