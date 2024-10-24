package io.scheduler.CustomApiScheduler.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiExecutionData {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long executionId;
    private long apiId;
    private LocalDateTime executionTime;
    private String status;
    private long responseTime;
    private String errorMessage;
}
