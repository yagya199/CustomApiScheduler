package io.scheduler.CustomApiScheduler.repositories;

import io.scheduler.CustomApiScheduler.entity.ApiData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiDataRepository extends JpaRepository<ApiData,Long> {
}
