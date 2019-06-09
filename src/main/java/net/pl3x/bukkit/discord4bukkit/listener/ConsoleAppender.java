package net.pl3x.bukkit.discord4bukkit.listener;

import net.pl3x.bukkit.discord4bukkit.D4BPlugin;
import net.pl3x.bukkit.discord4bukkit.task.ConsoleMessageQueueWorker;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class ConsoleAppender extends AbstractAppender {
    private static final PatternLayout PATTERN_LAYOUT = PatternLayout.createDefaultLayout();
    private static final Pattern STRIP_PATTERN = Pattern.compile("\\[m|\\[([0-9]{1,2}[;m]?){3}|\u001B+");
    private static final List<Level> LEVELS_TO_LOG = Arrays.asList(Level.INFO, Level.WARN, Level.ERROR);

    private static Date date = new Date();
    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm");

    private final ConsoleMessageQueueWorker worker = new ConsoleMessageQueueWorker();

    public static String timeStamp() {
        date.setTime(System.currentTimeMillis());
        return format.format(date);
    }

    private final D4BPlugin plugin;

    public ConsoleAppender(D4BPlugin plugin) {
        super("D4B", null, PATTERN_LAYOUT, false);
        this.plugin = plugin;

        ((Logger) LogManager.getRootLogger()).addAppender(this);

        worker.start();
    }

    @Override
    public boolean isStarted() {
        return true;
    }

    @Override
    public void append(LogEvent event) {
        if (plugin.getBot().getConsoleChannel() == null) {
            return;
        }

        if (!LEVELS_TO_LOG.contains(event.getLevel())) {
            return;
        }

        String message = STRIP_PATTERN.matcher(event.getMessage().getFormattedMessage()).replaceAll("");

        if (StringUtils.isBlank(message)) {
            return;
        }

        worker.QUEUE.add("[" + timeStamp() + " " + event.getLevel().name().toUpperCase() + "] " + message);
    }
}
