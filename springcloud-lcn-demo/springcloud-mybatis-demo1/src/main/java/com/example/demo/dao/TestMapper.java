package com.example.demo.dao;

import com.example.demo.entity.Test;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by lorne on 2017/6/28.
 */
@Mapper
public interface TestMapper {


    @Select("SELECT * FROM t_test")
    List<Test> findAll();

    @Insert("INSERT INTO t_test(NAME) VALUES(#{name})")
    int save(@Param("name") String name);

}
