package com.blg.demo.datasource.service;

import com.blg.demo.datasource.model.$Girl;
import com.jpaquery.core.Querys;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: panhongtong
 * @Date: 2020/4/10 16:04
 * @Description:
 */
@Service
@Transactional
public class DatasourceService {

    @PersistenceContext
    private EntityManager entityManager;

    public String testSave() {
        $Girl girl = new $Girl();
        girl.setName("断腿少女");
        girl.setAge(3);
        entityManager.persist(girl);
        return "OK";
    }

    public String testJPAQ() {
        return Querys.query(query -> {
            query.from($Girl.class);
            return query.list(entityManager);
        }).toString();
    }
}
