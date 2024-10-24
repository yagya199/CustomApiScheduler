package io.scheduler.CustomApiScheduler.controller;

import io.scheduler.CustomApiScheduler.constant.ApiResponseConstants;
import io.scheduler.CustomApiScheduler.dto.ResponseDto;
import io.scheduler.CustomApiScheduler.entity.ApiData;
import io.scheduler.CustomApiScheduler.service.ApiScheduler;
import io.scheduler.CustomApiScheduler.service.impl.ApiSchedulerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scheduler/api")
public class ApiSchedulerController {

    @Autowired
    private ApiScheduler apiScheduler;


    @PostMapping
    public ResponseEntity<ResponseDto> addApi(@RequestBody ApiData apiData) {
        apiScheduler.createApiData(apiData);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(ApiResponseConstants.STATUS_201,ApiResponseConstants.MESSAGE_201));
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<ApiData>> getAllApis() {
        List<ApiData> data=apiScheduler.getAllApiData();
        return ResponseEntity.ok(data);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDto> updateApi(@PathVariable Long id, @RequestBody ApiData apiData) {
        boolean isUpdated = apiScheduler.updateApiData(id,apiData);
        if(isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(ApiResponseConstants.STATUS_200, ApiResponseConstants.MESSAGE_200));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(ApiResponseConstants.STATUS_417, ApiResponseConstants.MESSAGE_417_UPDATE));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteApi(@PathVariable Long id) {
        apiScheduler.deleteApiData(id);
        return ResponseEntity.noContent().build();
    }


}



