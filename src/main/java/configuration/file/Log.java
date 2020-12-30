package configuration.file;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import configuration.constant.Folder;

public class Log {

    private Logger logger;
    private FileHandler fileHandler;

    public Log(String className, String fileName) {
        try {
            logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            fileHandler = new FileHandler(Folder.RESOURCES.getName() + Folder.LOG.getName() + "/" + fileName, true);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);
            fileHandler.setFormatter(new CustomFormatter(className));
            logger.addHandler(fileHandler);
        } catch (SecurityException e) {
        } catch (IOException e) {
        }
    }

    public void log(Level level, String msg) {
        logger.log(level, msg);
        fileHandler.close();
    }

    private class CustomFormatter extends Formatter {

        private String className;

        private CustomFormatter(String className) {
            this.className = className;
        }

        @Override
        public String format(LogRecord record) {
            record.setSourceClassName(className);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY hh:mm:ss a");
            StringBuilder message = new StringBuilder();
            message.append(record.getInstant().atZone(ZoneId.of("UTC+2")).toLocalDateTime().format(formatter)).append(" ## ");
            message.append(record.getSourceClassName()).append(" ## ");
            message.append(record.getLevel()).append(": ");
            message.append(record.getMessage()).append("\n");
            return message.toString();
        }
    }

}