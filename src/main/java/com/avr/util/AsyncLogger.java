package com.avr.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncLogger {
    private final Logger logger;

    public AsyncLogger(String sourceName) {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s");
        logger = Logger.getLogger(sourceName);
    }

    public synchronized void log(Level logLevel, String message, String color) {
        synchronized (this){
            logger.log(logLevel, ConsoleColor.paint(message, color));
        }
    }
    public synchronized void log(Level logLevel, String message) {
        synchronized (this){
            logger.log(logLevel, message);
        }
    }
}
