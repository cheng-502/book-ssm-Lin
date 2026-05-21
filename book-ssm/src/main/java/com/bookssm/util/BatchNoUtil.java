package com.bookssm.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 库存批次号生成工具类
 * 格式: RK + yyyyMMdd + - + 3位随机数
 */
public class BatchNoUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public static String generate() {
        String datetime = LocalDateTime.now().format(FORMATTER);
        String random = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        return "RK" + datetime + random;
    }
}
