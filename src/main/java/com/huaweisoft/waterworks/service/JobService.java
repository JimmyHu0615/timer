package com.huaweisoft.waterworks.service;

import java.util.List;

import com.huaweisoft.waterworks.model.GetJobResp;

public interface JobService {

    public List<GetJobResp> getJobAndTriggerDetails(Integer pageNum, Integer pageSize);

    
}
