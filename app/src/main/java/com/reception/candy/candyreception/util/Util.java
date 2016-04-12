package com.reception.candy.candyreception.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains methods needed to create a customer and an encrypted password.
 */
public class Util {

    /**
     * Provides a request factory with a connection timeout.
     * It is useful for the resttemplate object in the rest clients.
     *
     * @return The request factory with the timeout configured.
     */
    public static ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Constant.WEB_CONNECTION_TIMEOUT);
        factory.setReadTimeout(Constant.READ_WEB_CONNECTION_TIMEOUT);
        return factory;
    }

    public static HttpHeaders getRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "Close");
        final Map<String, String> charsetMap = new HashMap<>(4);
        charsetMap.put("charset", "utf-8");

        MediaType mediaType = new MediaType("application", "json", charsetMap);

        headers.setContentType(mediaType);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        return headers;
    }

}