package com.blg.demo.datasource.controller;

import com.blg.demo.datasource.service.DatasourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: panhongtong
 * @Date: 2020/4/10 16:06
 * @Description:
 */
@RestController
public class DatasourceController {

    @Autowired
    private DatasourceService datasourceService;

    @GetMapping("testData")
    public String testDatasource() {
        return datasourceService.testSave();
    }

    @GetMapping("testJPAQ")
    public String testJPAQ() {
        return datasourceService.testJPAQ();
    }
}
