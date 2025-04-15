package logging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.logging")
public class LoggingProperties {
    private boolean enabled = true;
    private Level level = Level.INFO;
    private boolean logExecutionEnabled = true;
    private boolean logExceptionEnabled = true;
    private boolean logExecutionTimeEnabled = true;
    private boolean logTrackingEnabled = true;
    private boolean handlingResultEnabled = true;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }

    public boolean isLogExecutionEnabled() { return logExecutionEnabled; }
    public void setLogExecutionEnabled(boolean logExecutionEnabled) { this.logExecutionEnabled = logExecutionEnabled; }

    public boolean isLogExceptionEnabled() { return logExceptionEnabled; }
    public void setLogExceptionEnabled(boolean logExceptionEnabled) { this.logExceptionEnabled = logExceptionEnabled; }

    public boolean isLogExecutionTimeEnabled() { return logExecutionTimeEnabled; }
    public void setLogExecutionTimeEnabled(boolean logExecutionTimeEnabled) { this.logExecutionTimeEnabled = logExecutionTimeEnabled; }

    public boolean isLogTrackingEnabled() { return logTrackingEnabled; }
    public void setLogTrackingEnabled(boolean logTrackingEnabled) { this.logTrackingEnabled = logTrackingEnabled; }

    public boolean isHandlingResultEnabled() { return handlingResultEnabled; }
    public void setHandlingResultEnabled(boolean handlingResultEnabled) { this.handlingResultEnabled = handlingResultEnabled; }

    public enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
