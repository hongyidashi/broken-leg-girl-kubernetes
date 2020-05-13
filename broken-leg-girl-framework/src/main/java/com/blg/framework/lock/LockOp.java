package com.blg.framework.lock;

import java.util.concurrent.Callable;

/**
 * 锁操作
 */
public interface LockOp {
    <V> V tryLock(Object obj, int timeout, Callable<V> callable);
}
