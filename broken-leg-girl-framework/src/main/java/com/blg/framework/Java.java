package com.blg.framework;

import java.util.Arrays;

/**
 * 语法糖工具类
 */
public class Java {
    /**
     * 用户包裹异常对象
     * @param e
     * @return
     */
    public static RuntimeException unchecked(Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

    /**
     * 合并数组
     * @param first
     * @param rest
     * @param <T>
     * @return
     */
    public static <T> T[] concatArray(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
