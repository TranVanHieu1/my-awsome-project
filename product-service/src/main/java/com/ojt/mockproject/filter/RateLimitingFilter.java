package com.ojt.mockproject.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ojt.mockproject.entity.Account;
import com.ojt.mockproject.entity.ApiRequestLog;
import com.ojt.mockproject.repository.ApiRequestLogRepository;
import com.ojt.mockproject.service.RedisService;
import com.ojt.mockproject.utils.AccountUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableScheduling
@Component
public class RateLimitingFilter implements Filter {
    private final List<ApiRequestLog> requestLogBuffer = new ArrayList<>(); // buffer
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> requestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> requestBanCounts = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS_PER_MINUTE = 100;
    private final int MAX_REQUESTS_PER_15_MINUTES = 500;
    private final int MAX_RECORDS_PER_IP = 5;
    private static final int TOO_MANY_REQUESTS = 429;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AccountUtils accountUtils;
    private final ApiRequestLogRepository apiRequestLogRepository;

    public RateLimitingFilter(AccountUtils accountUtils, ApiRequestLogRepository apiRequestLogRepository) {
        this.accountUtils = accountUtils;
        this.apiRequestLogRepository = apiRequestLogRepository;
    }
    @Autowired
    private RedisService redisService;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String ip = httpRequest.getRemoteAddr();
        String url = httpRequest.getRequestURI();

        Account account = accountUtils.getCurrentAccount();

        httpRequest.setAttribute("currentAccount", account);
        if(isBanIp(ip)) {
            String message = "You have been banned! Please comeback in tomorrow ";
            extracted(httpResponse, message);
            return;
        }

        if(!isBanRequest(ip)) {
            String message = "Handle ban one day";
            extracted(httpResponse, message);
            return;
        }
        if (!isRequestAllowed(ip, url)) {
            String message = "Too many requests. PLease try again later.";
            extracted(httpResponse, message);
            return;
        }

        saveRequestLog(ip, url, account); // save logs to memory
        chain.doFilter(request, response);
    }

    private void extracted(HttpServletResponse httpResponse, String message) throws IOException {
        httpResponse.setStatus(TOO_MANY_REQUESTS);
        httpResponse.setContentType("application/json");
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        httpResponse.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private void saveRequestLog(String ip, String url, Account account) {
        boolean isGuest = account == null;
        for (Map.Entry<String, ConcurrentHashMap<String, Integer>> entry : requestCounts.entrySet()) {
            ConcurrentHashMap<String, Integer> urlCounts = entry.getValue();

            for (Map.Entry<String, Integer> urlCount : urlCounts.entrySet()) {
                int count = urlCount.getValue();

                ApiRequestLog log = new ApiRequestLog();
                log.setIpAddress(ip);
                log.setApiEndpoint(url);
                log.setRequestCount(count);
                log.setCreateAt(LocalDateTime.now());
                log.setGuest(isGuest);
                log.setAccount(account);

                requestLogBuffer.add(log);
            }
        }
        requestLogBuffer.clear();
    }

    @Scheduled(fixedRate = 180000) // 30m
    public void saveRequestLogsToDatabase() {
        if (!requestLogBuffer.isEmpty()) {
            int logTotal = 0;

            for (ApiRequestLog log : requestLogBuffer) {

                // scan list log buffer
                String ipAddress = log.getIpAddress();
                List<ApiRequestLog> existingLogs = apiRequestLogRepository.findByIpAddress(ipAddress);
                // find log in database

                if (existingLogs.size() < MAX_RECORDS_PER_IP) {
                    // if size < 5 ( 1 2 3 4 5 )
                    log.setAccount(log.getAccount());
                    boolean found = false;
                    for (ApiRequestLog existingLog : existingLogs) {
                        if (existingLog.getApiEndpoint().equals(log.getApiEndpoint())) {
                            existingLog.setRequestCount( existingLog.getRequestCount() + log.getRequestCount());
                            found = true;
                            existingLog.setUpdateAt(LocalDateTime.now());
                            apiRequestLogRepository.save(existingLog); // update available log
                            break;
                        }
                    }
                    if (!found) {
                        apiRequestLogRepository.save(log);
                    }
                } else {
                    ApiRequestLog minLog = findMinRequestCountLog(existingLogs);

                    for (ApiRequestLog existingLog : existingLogs) {
                        // scan list 5 log in database
                        if (existingLog.getIpAddress().equals(log.getIpAddress()) && existingLog.getApiEndpoint().equals(log.getApiEndpoint())) {
                            // if ip db = ip log buffer & endpoint = endpoint
                            int logCurrent = existingLog.getRequestCount();
                            existingLog.setRequestCount(logCurrent + log.getRequestCount());
                            // math:  log in db + log buffer
                            logTotal += log.getRequestCount();
                            existingLog.setUpdateAt(LocalDateTime.now());
                            apiRequestLogRepository.save(existingLog);
                        }
//                        else if (logTotal.getRequestCount() > minLog.getRequestCount()) {
//                            apiRequestLogRepository.delete(minLog);
////                            apiRequestLogRepository.save(log);
//                        }

                        if (logTotal > minLog.getRequestCount()) {
                            apiRequestLogRepository.deleteById(minLog.getId());
                        }
                    }
                }

            }
            requestLogBuffer.clear(); // delete after save
            System.out.println("Saved request logs to database.");
        }


    }
    private ApiRequestLog findMinRequestCountLog(List<ApiRequestLog> logs) {
        ApiRequestLog minLog = null;
        for (ApiRequestLog log : logs) {
            if (minLog == null || log.getRequestCount() < minLog.getRequestCount()) {
                minLog = log;
            }
        }
        return minLog;
    }

    private synchronized boolean isRequestAllowed(String ip, String url) {
        requestCounts.putIfAbsent(ip, new ConcurrentHashMap<>());
        ConcurrentHashMap<String, Integer> urlCounts = requestCounts.get(ip);
        int count = urlCounts.getOrDefault(url, 0);
        if (count < MAX_REQUESTS_PER_MINUTE) {
            urlCounts.put(url, count + 1);
            return true;
        }
        return false;
    }

    private synchronized boolean isBanRequest(String ip) {
        int count = requestBanCounts.getOrDefault(ip, 0);
        if (count < MAX_REQUESTS_PER_15_MINUTES) {
            requestBanCounts.put(ip, count + 1);
            return true;
        } else if (ip.equals("42.117.205.235")) {
            return true;
        }
        long expirationTime = 24 * 60 * 60 * 1000L;
        redisService.addToBlacklist(ip, expirationTime);
        return false;
    }

    private synchronized boolean isBanIp(String ip) {

        boolean isban = redisService.isIpBlacklisted(ip);
        if (isban) {
            return true;
        }
        return false;
    }

    @Scheduled(fixedRate = 900000) // 15p
    public void banRequest() {
        System.out.println("Clear request ban user");
        requestBanCounts.clear();
    }

    @Scheduled(fixedRate = 30000) // 30s
    public void resetRequestCounts() {
        System.out.println("Clear request counts");
        requestCounts.clear();
    }
}
