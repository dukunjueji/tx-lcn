package com.example.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.codingapi.tx.annotation.TxTransaction;
import com.example.demo.client.Demo2Client;
import com.example.demo.client.Demo3Client;
import com.example.demo.dao.TestMapper;
import com.example.demo.entity.Test;
import com.example.demo.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lorne on 2017/6/26.
 */
@Service
public class DemoServiceImpl implements DemoService {


    @Autowired
    private Demo2Client demo2Client;

    @Autowired
    private Demo3Client demo3Client;

    @Autowired
    private TestMapper testMapper;

    private Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    @Override
    public List<Test> list() {
        System.out.println(JSON.toJSONString(demo2Client.list()));
        System.out.println("获取测试2数据");
        return testMapper.findAll();
    }

    @Override
    @TxTransaction(isStart = true)
    @Transactional
    public int save(Integer id, String name) {
        System.out.println("插入测试1数据库");
        testMapper.save("mybatis1");
        System.out.println("插入测试2数据库");
        demo2Client.save(id, name);
        //int i = 1/0;

        //demo3Client.save();

        return 2;
    }
}
