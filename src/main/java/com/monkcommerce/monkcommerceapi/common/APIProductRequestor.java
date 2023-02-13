package com.monkcommerce.monkcommerceapi.common;

import com.google.gson.Gson;
import com.monkcommerce.monkcommerceapi.constants.ExternalAPI;
import com.monkcommerce.monkcommerceapi.custom_exceptions.RateLimitException;
import com.monkcommerce.monkcommerceapi.data_objects.products.response.ProductDTO;
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

public class APIProductRequestor
{
    private final int maxRequestsPerInterval;
    private final int intervalInSeconds;
    private final int retryIntervalInSeconds;
    private Queue<Long> requestTimes;

    public APIProductRequestor()
    {
        this.maxRequestsPerInterval = 100;
        this.intervalInSeconds = 3;
        this.retryIntervalInSeconds = 2;
        this.requestTimes = new LinkedList<>();
    }

    public APIProductRequestor(int maxRequestsPerInterval, int intervalInSeconds, int retryIntervalInSeconds) {
        this.maxRequestsPerInterval = maxRequestsPerInterval;
        this.intervalInSeconds = intervalInSeconds;
        this.retryIntervalInSeconds = retryIntervalInSeconds;
        this.requestTimes = new LinkedList<>();
    }

    public ProductDTO makeProductRequest(String categoryId,Integer page) throws RateLimitException, InterruptedException {
        long currentTime = System.currentTimeMillis();

        // Remove requests older than the interval
        while (!requestTimes.isEmpty() &&
                (currentTime - requestTimes.peek()) > TimeUnit.SECONDS.toMillis(intervalInSeconds)) {
            requestTimes.poll();
        }

        if (requestTimes.size() >= maxRequestsPerInterval) {
            TimeUnit.SECONDS.sleep(retryIntervalInSeconds);
            return makeProductRequest(categoryId,page);
        }

        try {
            var product = callTheProductApi(categoryId,page).getBody();
            ProductDTO products = extractProductValue(product);
            requestTimes.offer(currentTime);
            return products;
        } catch (RateLimitException e) {
            TimeUnit.SECONDS.sleep(retryIntervalInSeconds);
            return makeProductRequest(categoryId,page);
        }
    }

    @Bean("asyncExecution")
    private ResponseEntity<String> callTheProductApi(String categoryId,Integer page) throws RateLimitException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(ExternalAPI.getProductWithParams(ExternalAPI.DEFAULT_PRODUCT_LIMIT, page, categoryId), HttpMethod.GET, new HttpEntity<Object>(ExternalAPI.getHeadersWithApiKey(new HashMap<>())), new ParameterizedTypeReference<String>() {});
        if(response.getStatusCode() == HttpStatusCode.valueOf(429))
            throw new RateLimitException("Api Rate Limit Exceed");
        return response;
    }
    private ProductDTO extractProductValue(String product)
    {
        ProductDTO productDTO = new ProductDTO();
        try {
            Gson gson = new Gson();
            return productDTO = gson.fromJson(product, ProductDTO.class);
        }catch (Exception ex) { return productDTO;}
    }
}
