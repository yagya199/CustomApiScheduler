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
public class ApiData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String apiUrl;

    private String httpMethod;

    private long api_interval;

    private LocalDateTime nextExecutionTime;

    private LocalDateTime lastExecutionTime;
}
