package com.jp.Kintai.util;

import org.springframework.stereotype.Service;
import com.jp.Kintai.enumClass.LogLevel;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Service
public class LoggerUtil {

	private static final Logger logger = LogManager.getLogger(LoggerUtil.class);

	public void LogOutput(LogLevel inputLogLevel, String errorTitle, String ExceptionMessage){
		switch (inputLogLevel) {
			case INFO:
				logger.info(errorTitle);
				logger.info(ExceptionMessage);
				break;
			case WARN:
				logger.warn(errorTitle);
				logger.warn(ExceptionMessage);
				break;
			case FATAL:
				logger.fatal(errorTitle);
				logger.fatal(ExceptionMessage);
				break;
			case ERROR:
				logger.error(errorTitle);
				logger.error(ExceptionMessage);
				break;
			case DEBUG:
				logger.debug(errorTitle);
				logger.debug(ExceptionMessage);
				break;
			case TRACE:
				logger.trace(errorTitle);
				logger.trace(ExceptionMessage);
				break;
		}
	}
}
