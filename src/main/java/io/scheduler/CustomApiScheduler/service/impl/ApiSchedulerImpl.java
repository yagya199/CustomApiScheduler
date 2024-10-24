package io.scheduler.CustomApiScheduler.service.impl;

import io.scheduler.CustomApiScheduler.entity.ApiData;
import io.scheduler.CustomApiScheduler.entity.ApiExecutionData;
import io.scheduler.CustomApiScheduler.exception.ResourceNotFoundException;
import io.scheduler.CustomApiScheduler.repositories.ApiDataRepository;
import io.scheduler.CustomApiScheduler.repositories.ApiExecutionDataRepository;
import io.scheduler.CustomApiScheduler.service.ApiScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApiSchedulerImpl implements ApiScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ApiSchedulerImpl.class);


    @Autowired
    private ApiDataRepository apiDataRepository;

    @Autowired
    private ApiExecutionDataRepository apiExecutionDataRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JavaMailSender javaMailSender;



    //create ApiData
    @CachePut(value="apiData",key="#apiData.id")
    public ApiData createApiData(ApiData apiData) {
        apiData.setLastExecutionTime(LocalDateTime.now());
        apiData.setNextExecutionTime(LocalDateTime.now().plusSeconds(apiData.getApi_interval()));
        apiDataRepository.save(apiData);

        return apiData;
    }

    //get apiData
    @Cacheable(value="apiData",key="#apiData.id")
    public List<ApiData> getAllApiData() {
        return apiDataRepository.findAll();
    }


    //delete apidata
    @CacheEvict(value="appData",key="#apiData.id")
    public void deleteApiData(Long id) {
        apiDataRepository.deleteById(id);
    }

    //update apiData
    @CachePut(value="apiData",key="#apiData.id")
    public boolean updateApiData(Long id,ApiData apiData) {
        ApiData existingData = apiDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("API Data not found"));

        existingData.setApiUrl(apiData.getApiUrl());
        existingData.setHttpMethod(apiData.getHttpMethod());
        existingData.setApi_interval(apiData.getApi_interval());
        apiDataRepository.save(existingData);

        return true;
    }


    //Scheduling code......................................................................................
    //.......................................................................................

    @Scheduled(fixedDelay = 50000)
    public void scheduleApiCall() {
        logger.info("Checking for scheduled APIs to execute.");
        List<ApiData> apiData = apiDataRepository.findAll();
        if(apiData.isEmpty()){
            logger.error("Create API DATA first");
        }
        for (ApiData data : apiData) {
            if (LocalDateTime.now().isAfter(data.getNextExecutionTime())) {
                logger.info("Executing API: {}", data.getApiUrl());
                executeApi(data);
            }
        }
    }



   @Retryable(value={Exception.class},maxAttempts = 5, backoff = @Backoff(delay = 3000))
    public void executeApi(ApiData data) {
        ApiExecutionData executionData = new ApiExecutionData();
        executionData.setApiId(data.getId());
        executionData.setExecutionTime(LocalDateTime.now());

        try {
            Instant startTime = Instant.now();
            ResponseEntity<String> response = callApi(data);
            long responseTime = Instant.now().toEpochMilli() - startTime.toEpochMilli();

            executionData.setStatus("SUCCESS");
            executionData.setResponseTime(responseTime);
            executionData.setErrorMessage("No error");

            logger.info("API executed successfully: {} - Response time: {} ms", data.getApiUrl(), responseTime);

            // Update next execution time based on interval
            data.setLastExecutionTime(LocalDateTime.now());
            data.setNextExecutionTime(LocalDateTime.now().plusSeconds(data.getApi_interval()));
            apiDataRepository.save(data);
        } catch (Exception e) {
            executionData.setStatus("FAILURE");
            executionData.setResponseTime(Long.valueOf(null));
            executionData.setErrorMessage(e.getMessage());
            // Optionally retry logic can be added here
            logger.error("API execution failed: {} - Error: {}", data.getApiUrl(), e.getMessage());

        } finally {
            apiExecutionDataRepository.save(executionData);
        }
    }


    private ResponseEntity<String> callApi(ApiData data) {
        HttpMethod method = HttpMethod.valueOf(data.getHttpMethod().toUpperCase());
        HttpEntity<String> entity = new HttpEntity<>(null);

        return restTemplate.exchange(data.getApiUrl(), method, entity, String.class);
    }


    @Recover
    public void recover(Exception e, ApiData apiData) {
        // Logging the failure
        logger.error("Max retries reached for API: {}. Error: {}", apiData.getApiUrl(), e.getMessage());

        ApiExecutionData executionData= new ApiExecutionData();
        executionData.setApiId(apiData.getId());
        executionData.setExecutionTime(LocalDateTime.now());
        executionData.setStatus("RECOVERED FAILURE");
        executionData.setResponseTime(Long.valueOf(null));
        executionData.setErrorMessage("Max retries reached for API: " + apiData.getApiUrl() + ". Error: " + e.getMessage());
        apiExecutionDataRepository.save(executionData);

        //Send alert mail
        sendAlertEmail(apiData,e);

    }

    private void sendAlertEmail(ApiData apiData, Exception e) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("shubham19k@gmail.com.com");
        message.setSubject("API Execution Failure Alert");
        message.setText("The API of url: " + apiData.getApiUrl() + " failed after maximum retries.\n" +
                "Error: " + e.getMessage());
        javaMailSender.send(message);
    }






}
