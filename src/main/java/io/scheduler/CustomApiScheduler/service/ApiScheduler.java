package io.scheduler.CustomApiScheduler.service;

import io.scheduler.CustomApiScheduler.entity.ApiData;

import java.util.List;

public interface ApiScheduler {

    ApiData createApiData(ApiData apiData);

    List<ApiData> getAllApiData();

    void deleteApiData(Long id);

    boolean updateApiData(Long id,ApiData apiData);


}
