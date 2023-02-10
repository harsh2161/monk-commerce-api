package com.monkcommerce.monkcommerceapi.constants;

import org.springframework.http.HttpHeaders;
import java.util.Map;

public class ExternalAPI
{
    public static final String GET_CATEGORIES = "https://stageapi.monkcommerce.app/task/categories";
    private static final String X_API_KEY = "x-api-key";
    private static final String X_API_KEY_VALUE = "s72rash876asg1";
    public static final Integer DEFAULT_LIMIT = 100;
    public static final Integer DEFAULT_PAGE = 1;
    public static final String DATA_SAVED = "Data Saved Successfully";
    public static HttpHeaders getHeaders(Map<String,String> headersValue)
    {
        HttpHeaders headers = new HttpHeaders();
        for(var header : headersValue.entrySet()){
            headers.add(header.getKey(), header.getValue());
        }
        return headers;
    }
    public static HttpHeaders getHeadersWithApiKey(Map<String,String> headersValue)
    {
        HttpHeaders headers = new HttpHeaders();
        for(var header : headersValue.entrySet()){
            headers.add(header.getKey(), header.getValue());
        }
        headers.add(X_API_KEY , X_API_KEY_VALUE);
        return headers;
    }
    public static String getCategoriesWithParams(Integer limit, Integer page)
    {
        return GET_CATEGORIES + "?limit="+limit+"&page="+page;
    }
}
