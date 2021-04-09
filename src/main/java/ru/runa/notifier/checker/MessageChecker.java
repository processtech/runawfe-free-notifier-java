package ru.runa.notifier.checker;

import ru.runa.notifier.GUI;
import ru.runa.notifier.WFEConnection;
import ru.runa.notifier.auth.LoginHelper;
import ru.runa.notifier.tray.SystemTray;
import ru.runa.notifier.util.ResourcesManager;

public class MessageChecker extends Checker {
    private long unreadMessagesCount = 0;
    private long newMessagesCount = 0;

    public MessageChecker(SystemTray systemTray) {
        super(systemTray, ResourcesManager.getOnNewTaskTriggerCommand());
    }

    @Override
    void setCounts() {
        long count = WFEConnection.getChatAPI().getNewMessagesCount(LoginHelper.getUser());
        newMessagesCount = (count - unreadMessagesCount > 0) ? count - unreadMessagesCount : 0;
        unreadMessagesCount = count;
    }

    @Override
    long getUnreadCount() {
        return unreadMessagesCount;
    }

    @Override
    long getNewCount() {
        return newMessagesCount;
    }

    public void start() {
        super.start(GUI.setting.getCheckTasksTimeout(), GUI.setting.getUnreadTasksNotificationTimeout());
    }
}
