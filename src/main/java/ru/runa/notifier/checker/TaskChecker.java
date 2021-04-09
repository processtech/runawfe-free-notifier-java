package ru.runa.notifier.checker;

import com.google.common.collect.Maps;
import ru.runa.notifier.GUI;
import ru.runa.notifier.WFEConnection;
import ru.runa.notifier.auth.LoginHelper;
import ru.runa.notifier.tray.SystemTray;
import ru.runa.notifier.util.ResourcesManager;
import ru.runa.wfe.webservice.WfTask;
import java.util.List;
import java.util.Map;

public class TaskChecker extends Checker {
    private final Map<Long, WfTask> existingTasks = Maps.newHashMap();
    private long unreadTasksCount = 0;
    private long newTasksCount = 0;

    public TaskChecker(SystemTray systemTray) {
        super(systemTray, ResourcesManager.getOnNewTaskTriggerCommand());
    }

    @Override
    void setCounts() {
        List<WfTask> tasks = WFEConnection.getTaskAPI().getMyTasks(LoginHelper.getUser(), null);
        unreadTasksCount = getUnreadTasksCount(tasks);
        newTasksCount = getNewTasksCount(tasks);
        existingTasks.clear();
        for (WfTask task : tasks) {
            existingTasks.put(task.getId(), task);
        }
    }

    private int getUnreadTasksCount(List<WfTask> newTaskIds) {
        int result = 0;
        for (WfTask task : newTaskIds) {
            if (task.isFirstOpen()) {
                result++;
            }
        }
        return result;
    }

    private int getNewTasksCount(List<WfTask> newTaskIds) {
        int result = 0;
        for (WfTask task : newTaskIds) {
            if (!existingTasks.containsKey(task.getId()) && task.isFirstOpen()) {
                result++;
            }
        }
        return result;
    }

    @Override
    long getUnreadCount() {
        return unreadTasksCount;
    }

    @Override
    long getNewCount() {
        return newTasksCount;
    }

    public void start() {
        super.start(GUI.setting.getCheckTasksTimeout(), GUI.setting.getUnreadTasksNotificationTimeout());
    }
}
