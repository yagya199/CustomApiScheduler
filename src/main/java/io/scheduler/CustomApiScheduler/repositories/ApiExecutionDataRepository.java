package io.scheduler.CustomApiScheduler.repositories;

import io.scheduler.CustomApiScheduler.entity.ApiExecutionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiExecutionDataRepository extends JpaRepository<ApiExecutionData,Long> {
}
