package com.example.demoStudy2.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface UserInfoMapper {
    List<Map<String,Object>> queryUserInfo();

}
