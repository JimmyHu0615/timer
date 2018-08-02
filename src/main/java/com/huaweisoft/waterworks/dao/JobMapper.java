package com.huaweisoft.waterworks.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.huaweisoft.waterworks.model.GetJobResp;

@Mapper
public interface JobMapper {

    List<GetJobResp> getJobAndTriggerDetails();
}
