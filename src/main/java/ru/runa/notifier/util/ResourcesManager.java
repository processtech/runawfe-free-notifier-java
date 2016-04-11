/*
 * This file is part of the RUNA WFE project.
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation; version 2.1 
 * of the License. 
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Lesser General Public License for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package ru.runa.notifier.util;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.CharStreams;

/**
 * Created on 2006
 * 
 * @author Gritsenko_S
 */

public class ResourcesManager {
    private final static PropertyResources PROPERTIES = new PropertyResources("application.properties");

    public static boolean isRestartRtnOnClose() {
        return PROPERTIES.getBooleanProperty("restart.rtn.onclose", false);
    }

    public static String getStartRtnCommand() {
        return PROPERTIES.getStringProperty("start.rtn.command");
    }

    public static String getApplicationName() {
        return PROPERTIES.getStringProperty("application.name");
    }

    public static String getTeasePopupText() {
        return PROPERTIES.getStringProperty("popup.tease");
    }

    public static String getTooltipPopupTasksText() {
        return PROPERTIES.getStringProperty("popup.tasks");
    }

    public static String getTooltipPopupNotLoggedText() {
        return PROPERTIES.getStringProperty("popup.not.logged");
    }

    public static String getTooltipPopupErrorText() {
        return PROPERTIES.getStringProperty("popup.error");
    }

    public static String getTooltipPopupNoTasksText() {
        return PROPERTIES.getStringProperty("popup.no.tasks");
    }

    public static String getNewTasksPopupTitle() {
        return PROPERTIES.getStringProperty("popup.newtasks");
    }

    public static String getUserName() {
        return PROPERTIES.getStringProperty("user.name");
    }

    public static String getPasswordName() {
        return PROPERTIES.getStringProperty("user.password");
    }

    public static String getErrorLoginMessage() {
        return PROPERTIES.getStringProperty("error.login");
    }

    public static String getErrorInternalMessage() {
        return PROPERTIES.getStringProperty("error.internal");
    }

    public static String getLoginMessage() {
        return PROPERTIES.getStringProperty("login.message");
    }

    public static String getRetryMessage() {
        return PROPERTIES.getStringProperty("retry.message");
    }

    public static String getAuthenticationType() {
        return PROPERTIES.getStringProperty("authentication.type");
    }

    public static String getMenuOpenName() {
        return PROPERTIES.getStringProperty("menu.open");
    }

    public static String getMenuExitName() {
        return PROPERTIES.getStringProperty("menu.exit");
    }

    public static String getLoginRelativeUrl() {
        return PROPERTIES.getStringProperty("login.relative.url");
    }

    public static boolean getShowTray() {
        return PROPERTIES.getBooleanProperty("show.tray", true);
    }

    public static boolean isLoginSilently() {
        return PROPERTIES.getBooleanProperty("userinput.login.silently", false);
    }

    public static String getDefaultLogin() {
        return PROPERTIES.getStringProperty("userinput.default.login", "");
    }

    public static String getDefaultPassword() {
        return PROPERTIES.getStringProperty("userinput.default.password", "");
    }

    public static int getCheckTasksTimeout() {
        return 1000 * PROPERTIES.getIntegerProperty("check.tasks.timeout", 300);
    }

    public static int getAutoClosePopupTimeout() {
        return 1000 * PROPERTIES.getIntegerProperty("popup.autoclose.timeout", 6);
    }

    public static boolean isSoundsEnabled() {
        return PROPERTIES.getBooleanProperty("sounds.enabled", false);
    }

    public static String getOnNewTaskTriggerCommand() {
        return PROPERTIES.getStringProperty("onNewTask.trigger.command");
    }

    public static int getUnreadTasksNotificationTimeout() {
        return 1000 * PROPERTIES.getIntegerProperty("unread.tasks.notification.timeout", 0);
    }

    private static AppServerType getServerType() {
        String enumValue = PROPERTIES.getStringPropertyNotNull("application.server.type");
        return AppServerType.valueOf(enumValue);
    }

    public static String getHttpServerUrl() {
        return applyPattern("http://${server.name}:${server.port}/wfe");
    }

    public static String getAppServerVersionUrl() {
        return applyPattern("http://${server.name}:${server.port}/version");
    }

    public static String getWebServiceUrl() {
        return applyPattern(getServerType().getUrlPattern());
    }

    private static final Pattern VARIABLE_REGEXP = Pattern.compile("\\$\\{(.*?[^\\\\])\\}");

    private static String applyPattern(String pattern) {
        Matcher matcher = VARIABLE_REGEXP.matcher(pattern);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String name = matcher.group(1);
            String value = PROPERTIES.getStringPropertyNotNull(name);
            if ("server.version".equals(name) && "auto".equals(value)) {
                String versionUrl = getHttpServerUrl() + "/version";
                try {
                    InputStreamReader reader = new InputStreamReader(new URL(versionUrl).openStream());
                    value = CharStreams.toString(reader);
                    int colonIndex = value.indexOf(":");
                    if (colonIndex != -1) {
                        value = value.substring(colonIndex + 1);
                    }
                    reader.close();
                } catch (Exception e) {
                    throw new RuntimeException("Unable to acquire version using " + versionUrl);
                }
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

}
