package com.bookssm.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单号生成工具类
 * 格式: ORD + yyyyMMddHHmmss + 6位随机数
 */
public class OrderNoUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generate() {
        String datetime = LocalDateTime.now().format(FORMATTER);
        String random = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        return "ORD" + datetime + random;
    }
}
