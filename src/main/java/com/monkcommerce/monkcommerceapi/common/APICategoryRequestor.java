package com.monkcommerce.monkcommerceapi.common;

import com.monkcommerce.monkcommerceapi.constants.ExternalAPI;
import com.monkcommerce.monkcommerceapi.custom_exceptions.RateLimitException;
import com.monkcommerce.monkcommerceapi.data_objects.categories.response.CategoriesDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class APICategoryRequestor {
    private final int maxRequestsPerInterval;
    private final int intervalInSeconds;
    private final int retryIntervalInSeconds;
    private Queue<Long> requestTimes;

    public APICategoryRequestor()
    {
        this.maxRequestsPerInterval = 100;
        this.intervalInSeconds = 3;
        this.retryIntervalInSeconds = 1;
        this.requestTimes = new LinkedList<>();
    }

    public APICategoryRequestor(int maxRequestsPerInterval, int intervalInSeconds, int retryIntervalInSeconds) {
        this.maxRequestsPerInterval = maxRequestsPerInterval;
        this.intervalInSeconds = intervalInSeconds;
        this.retryIntervalInSeconds = retryIntervalInSeconds;
        this.requestTimes = new LinkedList<>();
    }

    public CategoriesDTO makeCategoriesRequest(Integer page) throws RateLimitException, InterruptedException {
        long currentTime = System.currentTimeMillis();

        // Remove requests older than the interval
        while (!requestTimes.isEmpty() &&
                (currentTime - requestTimes.peek()) > TimeUnit.SECONDS.toMillis(intervalInSeconds)) {
            requestTimes.poll();
        }

        if (requestTimes.size() >= maxRequestsPerInterval) {
            TimeUnit.SECONDS.sleep(retryIntervalInSeconds);
            return makeCategoriesRequest(page);
        }

        try {
            var categories = callTheCategoryApi(page);
            requestTimes.offer(currentTime);
            return categories.getBody();
        } catch (RateLimitException e) {
            TimeUnit.SECONDS.sleep(retryIntervalInSeconds);
            return makeCategoriesRequest(page);
        }
    }

    @Bean("asyncExecution")
    private ResponseEntity<CategoriesDTO> callTheCategoryApi(Integer page) throws RateLimitException {
        RestTemplate restTemplate = new RestTemplate();
        var categories = restTemplate.exchange(ExternalAPI.getCategoriesWithParams(ExternalAPI.DEFAULT_LIMIT,page), HttpMethod.GET, new HttpEntity<Object>(ExternalAPI.getHeadersWithApiKey(new HashMap<>())) , new ParameterizedTypeReference<CategoriesDTO>() {});
        if(categories.getStatusCode() == HttpStatusCode.valueOf(429))
            throw new RateLimitException("Api Rate Limit Exceed");
        return categories;
    }
}

