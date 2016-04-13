package com.reception.candy.candyreception.util;

public class URLS {
    private static final String BASE_URL = "http://10.38.2.75:3000"; //WEB SERVICE
    public static final String SAVE_CUSTOMER = BASE_URL.concat("/customers");
    public static final String SAVE_EVENT = BASE_URL.concat("/events");
    public static final String VERIFY_AVAILABILITY = BASE_URL.concat("/events/availability/");
}