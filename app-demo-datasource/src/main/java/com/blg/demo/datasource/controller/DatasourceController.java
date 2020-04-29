package com.blg.demo.datasource.controller;

import com.blg.demo.datasource.service.DatasourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: panhongtong
 * @Date: 2020/4/10 16:06
 * @Description:
 */
@Api(tags = "数据源测试接口")
@RestController
public class DatasourceController {

    @Autowired
    private DatasourceService datasourceService;

    @ApiOperation(value = "测试持久化", notes = "顺便测试下Swagger2")
    @GetMapping("testData")
    public String testDatasource() {
        return datasourceService.testSave();
    }

    @ApiOperation(value = "测试JPAQuery", notes = "测试JPAQuery")
    @GetMapping("testJPAQ")
    public String testJPAQ() {
        return datasourceService.testJPAQ();
    }

    private AtomicInteger ac = new AtomicInteger();

    @ApiOperation(value = "主动出现异常接口")
    @GetMapping("testE")
    public String testException(@RequestParam("name") String name) {
        if (name.equals("123")) {
            throw new RuntimeException("error");
        }
        System.err.println(ac.addAndGet(1));
        return "执行成功--->" + ac.get();
    }
}
