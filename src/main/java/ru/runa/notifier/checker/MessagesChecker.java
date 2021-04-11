package ru.runa.notifier.checker;

import ru.runa.notifier.GUI;
import ru.runa.notifier.WFEConnection;
import ru.runa.notifier.auth.LoginHelper;
import ru.runa.notifier.tray.SystemTray;
import ru.runa.notifier.util.ResourcesManager;

public class MessagesChecker extends Checker {
    public MessagesChecker(SystemTray systemTray) {
        super(systemTray, ResourcesManager.getOnNewTaskTriggerCommand());
    }

    @Override
    public void start() {
        super.start(GUI.setting.getCheckTasksTimeout(), GUI.setting.getUnreadTasksNotificationTimeout());
    }

    @Override
    void setCounts() {
        long count = WFEConnection.getChatAPI().getNewMessagesCount(LoginHelper.getUser());
        newCount = (count - unreadCount > 0) ? count - unreadCount : 0;
        unreadCount = count;
    }
}
