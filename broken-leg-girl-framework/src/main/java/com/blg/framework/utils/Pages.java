package com.blg.framework.utils;

import com.blg.framework.Java;
import com.github.pagehelper.PageHelper;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.Callable;

/**
 * 分页工具类
 * panhongtong
 */
public class Pages {
    public static <T> T page(Pageable pageable, Callable<T> callable){
        PageHelper.offsetPage(pageable.getPageNumber(),pageable.getPageSize());
        if(pageable.getSort()!=null){
            pageable.getSort().stream().forEach(order -> {
                String orderBy = order.getProperty();
                if(order.getDirection()!=null){
                    orderBy+=" "+order.getDirection().name();
                }
                PageHelper.orderBy(orderBy);
            });
        }
        try {
            return callable.call();
        } catch (Exception e) {
            throw Java.unchecked(e);
        }
    }
}
