package com.sm.project.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceLoggingUtil {

    private static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static void logPerformanceInfo(Class<?> clazz, String message) {
        Logger logger = getLogger(clazz);

        String threadName = Thread.currentThread().getName();
        long threadId = Thread.currentThread().getId();
        long timestamp = System.currentTimeMillis();

        String formattedMessage = String.format("[Timestamp: %d, Thread Name: %s, Thread ID: %d] %s", timestamp, threadName, threadId, message);

        if (logger.isInfoEnabled()) {
            logger.info(formattedMessage);
        }
    }

    public static void logPerformanceError(Class<?> clazz, String message, Throwable t) {
        Logger logger = getLogger(clazz);

        String threadName = Thread.currentThread().getName();
        long threadId = Thread.currentThread().getId();
        long timestamp = System.currentTimeMillis();

        String formattedMessage = String.format("[Timestamp: %d, Thread Name: %s, Thread ID: %d] %s", timestamp, threadName, threadId, message);

        if (logger.isErrorEnabled()) {
            logger.error(formattedMessage, t);
        }
    }
}
