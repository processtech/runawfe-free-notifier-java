package ru.runa.notifier.checker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.runa.notifier.GUI;
import ru.runa.notifier.auth.LoginHelper;
import ru.runa.notifier.tray.SystemTray;
import ru.runa.notifier.util.AePlayWave;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created on 2006
 *
 * @author Gritsenko_S
 */
public abstract class Checker {
    private static final Log log = LogFactory.getLog(Checker.class);

    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private final String onNewEventTriggerCommand;

    public static final String ERROR_OCCURRED_PROPERTY = "errorOccurred";
    private boolean errorOccurred;

    private Timer checkTimer;
    private final CountChecker countChecker;

    private Timer notificationTimer;
    private final Notifier notifier;

    public Checker(SystemTray systemTray, String onNewEventTriggerCommand) {
        countChecker = new CountChecker(systemTray);
        notifier = new Notifier();
        errorOccurred = true;
        this.onNewEventTriggerCommand = onNewEventTriggerCommand;
    }

    public void start(int checkTimeout, int notificationTimeout) {
        checkTimer = new Timer();
        checkTimer.schedule(countChecker, 0, checkTimeout);
        if (notificationTimeout > 0) {
            notificationTimer = new Timer();
            notificationTimer.schedule(notifier, notificationTimeout, notificationTimeout);
        }
    }

    public void stop() {
        if (checkTimer != null) {
            checkTimer.cancel();
            checkTimer = null;
            countChecker.cancel();
        }
        if (notificationTimer != null) {
            notificationTimer.cancel();
            notificationTimer = null;
            notifier.cancel();
        }
    }

    public void setErrorOccurred(boolean errorOccurred) {
        boolean old = this.errorOccurred;
        this.errorOccurred = errorOccurred;
        if (old != this.errorOccurred) {
            firePropertyChange(ERROR_OCCURRED_PROPERTY, old, this.errorOccurred);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.addPropertyChangeListener(pcl);
    }

    protected void firePropertyChange(String propName, Object old, Object newValue) {
        listeners.firePropertyChange(propName, old, newValue);
    }

    abstract void setCounts();
    abstract long getUnreadCount();
    abstract long getNewCount();

    private class CountChecker extends TimerTask {
        protected SystemTray systemTray;
        private int errorCount = 0;

        public CountChecker(SystemTray systemTray) {
            this.systemTray = systemTray;
        }

        @Override
        public void run() {
            try {
                if (!LoginHelper.isLogged()) {
                    try {
                        LoginHelper.login();
                    } catch (Throwable e) {
                        log.error("unable to login", e);
                        return;
                    }
                }
                if (LoginHelper.isLogged()) {
                    errorCount = 0;
                    setCounts();
                    final long unreadCount = getUnreadCount();
                    final long newCount = getNewCount();
                    GUI.display.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            setErrorOccurred(false);
                            systemTray.setTasks(unreadCount, newCount);
                        }
                    });
                    if (newCount > 0) {
                        AePlayWave.playNotification("/onNewTask.wav");
                    }
                    notifier.setCount(unreadCount);
                    if (onNewEventTriggerCommand != null) {
                        try {
                            Runtime.getRuntime().exec(onNewEventTriggerCommand);
                        } catch (Throwable th) {
                            log.warn("Unable to start onNewEventTrigger command", th);
                        }
                    }
                }
            } catch (Exception e) {
                // TODO AuthenticationException
                LoginHelper.reset();
            } catch (Throwable e) {
                log.warn("run (Unforeseen Exception)", e);
                if (++errorCount > 10) {
                    GUI.display.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            setErrorOccurred(true);
                        }
                    });
                }
            }
        }
    }

    private static class Notifier extends TimerTask {
        private long count;

        public void setCount(long count) {
            this.count = count;
        }

        @Override
        public void run() {
            if (count > 0) {
                AePlayWave.playNotification("/unreadTasksNotification.wav");
            }
        }
    }
}
