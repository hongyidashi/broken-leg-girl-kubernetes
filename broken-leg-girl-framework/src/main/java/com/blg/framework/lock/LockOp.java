package com.blg.framework.lock;

import java.util.concurrent.Callable;

/**
 * 锁操作
 * @author lujijiang
 */
public interface LockOp {
    <V> V tryLock(Object obj, int timeout, Callable<V> callable);
}
