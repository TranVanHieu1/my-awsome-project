package com.ojt.mockproject.repository;

import com.ojt.mockproject.entity.ApiRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiRequestLogRepository extends JpaRepository<ApiRequestLog, Integer> {
    List<ApiRequestLog> findByIpAddress(String ipAddress);

    List<ApiRequestLog> findByAccountId(Integer accountId);

    ApiRequestLog findByApiEndpoint(String apiEndpoint);
}
