package com.blg.framework.lock;

import org.springframework.util.Assert;

import java.util.concurrent.Callable;

/**
 * 通用锁工具
 */
public class Locks {
    private static LockOp lockOp;
    /**
     * 设置锁操作实现，只能设置一次
     */
    static void setLockOp(LockOp lockOp){
        if(Locks.lockOp!=null){
            throw new IllegalStateException("The lockOp has been initialized with "+ Locks.lockOp);
        }
        Locks.lockOp = lockOp;
    }

    /**
     * 尝试加锁
     */
    public static  <V> V tryLock(Object obj,int timeout, Callable<V> callable) {
        Assert.notNull(lockOp,"The lockOp has not been initialized yet.");
        return lockOp.tryLock(obj,timeout,callable);
    }
}
