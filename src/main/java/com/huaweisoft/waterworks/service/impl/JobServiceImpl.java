package com.huaweisoft.waterworks.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huaweisoft.waterworks.dao.JobMapper;
import com.huaweisoft.waterworks.model.GetJobResp;
import com.huaweisoft.waterworks.service.JobService;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobMapper  jobMapper;
    
    @Override
    public List<GetJobResp> getJobAndTriggerDetails(Integer pageNum, Integer pageSize) {
        return jobMapper.getJobAndTriggerDetails();
    }

}
