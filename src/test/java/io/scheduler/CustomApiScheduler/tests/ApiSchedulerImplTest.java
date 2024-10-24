package io.scheduler.CustomApiScheduler.tests;

import io.scheduler.CustomApiScheduler.entity.ApiData;
import io.scheduler.CustomApiScheduler.repositories.ApiDataRepository;
import io.scheduler.CustomApiScheduler.repositories.ApiExecutionDataRepository;
import io.scheduler.CustomApiScheduler.service.impl.ApiSchedulerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApiSchedulerImplTest {

    @InjectMocks
    private ApiSchedulerImpl apiScheduler;

    @Mock
    private ApiDataRepository apiDataRepository;

    @Mock
    private ApiExecutionDataRepository executionDataRepository;

    private ApiData apiData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        apiData = new ApiData();
        apiData.setId(1L);
        apiData.setApiUrl("https://jsonplaceholder.typicode.com/posts/1");
        apiData.setApi_interval(2);

    }

    @Test
    void addApiData_shouldSaveAndReturnApiConfiguration() {
        when(apiDataRepository.save(any(ApiData.class))).thenReturn(apiData);

        ApiData data= apiScheduler.createApiData(apiData);

        assertNotNull(data);
        assertEquals(apiData.getId(),data.getId());
        assertEquals(apiData.getApiUrl(),data.getApiUrl());
        assertEquals(apiData.getApi_interval(),data.getApi_interval());
        verify(apiDataRepository, times(1)).save(apiData);
    }

    @Test
    void deleteApiConfiguration_shouldInvokeDeleteById() {
        Long apiId = 1L;

        apiScheduler.deleteApiData(apiId);

        verify(apiDataRepository, times(1)).deleteById(apiId);
    }

    @Test
    void updateApiConfiguration_shouldUpdateAndReturnApiConfiguration() {
        Long apiId=1L;
        when(apiDataRepository.save(any(ApiData.class))).thenReturn(apiData);

        boolean value = apiScheduler.updateApiData(apiId,apiData);

        assertTrue(value);


    }


}
