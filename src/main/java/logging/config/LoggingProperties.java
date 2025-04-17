package logging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api.logging")
public class LoggingProperties {
    private boolean enabled = true;
    private Level level = Level.INFO;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }

    public enum Level {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
