package com.dyds.orders.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author khran
 * @version 1.0
 * @description
 */

public class OrderSnGenerator {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static String currentDate = dateFormat.format(new Date());
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static synchronized String generateOrderSn() {
        String nowDate = dateFormat.format(new Date());
        if (!nowDate.equals(currentDate)) {
            currentDate = nowDate;
            counter.set(0);
        }
        int count = counter.incrementAndGet();
        return nowDate + String.format("%04d", count);
    }
}
