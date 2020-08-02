package ru.runa.notifier.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author e.sladkov
 */
public class Setting {

    private static final Log log = LogFactory.getLog(Setting.class);

    public static final Setting SETTING = new Setting();

    private String protocol = ResourcesManager.getProtocol();

    private String host = ResourcesManager.getHost();

    private String port = ResourcesManager.getPort();

    private String login = ResourcesManager.getDefaultLogin();

    private String password = ResourcesManager.getDefaultPassword();

    private String authenticationType = ResourcesManager.getAuthenticationType();

    private Boolean isLoginSilently = ResourcesManager.isLoginSilently();

    private Integer checkTasksTimeout = ResourcesManager.getCheckTasksTimeout();

    private Integer autoClosePopupTimeout = ResourcesManager.getAutoClosePopupTimeout();

    private Boolean isSoundsEnabled = ResourcesManager.isSoundsEnabled();

    private Integer unreadTasksNotificationTimeout = ResourcesManager.getUnreadTasksNotificationTimeout();
    
    public void read() {

        final String srcProperties = System.getProperty("user.home") + "/config.properties";

        Properties properties = new Properties();
        try (InputStream in = new FileInputStream(srcProperties)) {
            properties.load(in);

            setProtocol(properties.getProperty("server.protocol", protocol));

            setHost(properties.getProperty("server.host", host));

            setPort(properties.getProperty("server.port", port));

            setAuthenticationType(properties.getProperty("authentication.type", authenticationType));

            setLogin(properties.getProperty("userinput.default.login", login));

            setPassword(properties.getProperty("userinput.default.password", password));

            Boolean tmpIsLoginSilently = Boolean.parseBoolean(properties.getProperty("userinput.login.silently"));

            setLoginSilently(tmpIsLoginSilently != null ? tmpIsLoginSilently : isLoginSilently);

            Integer tmpTimeout = Integer.parseInt(properties.getProperty("check.tasks.timeout", "0")) * 1000;
            setCheckTasksTimeout((tmpTimeout != 0) ? tmpTimeout : checkTasksTimeout);

            tmpTimeout = Integer.parseInt(properties.getProperty("popup.autoclose.timeout", "0")) * 1000;
            setAutoClosePopupTimeout((tmpTimeout != 0) ? tmpTimeout : autoClosePopupTimeout);

            Boolean tmpIsSoundEnabled = Boolean.parseBoolean(properties.getProperty("sounds.enabled"));
            setSoundsEnabled(tmpIsSoundEnabled != null ? tmpIsSoundEnabled : isSoundsEnabled);

            tmpTimeout = Integer.parseInt(properties.getProperty("unread.tasks.notification.timeout", "0")) * 1000;
            setUnreadTasksNotificationTimeout((tmpTimeout != 0) ? tmpTimeout : unreadTasksNotificationTimeout);
 
        } catch (IOException e) {
            log.debug("config.properties not found by path " + srcProperties + ", using defaults");
        }

    }

    public void save() {

        final Properties properties = new Properties();

        properties.setProperty("server.protocol", protocol);

        properties.setProperty("server.host", host);

        properties.setProperty("server.port", port);

        properties.setProperty("userinput.default.login", login);

        properties.setProperty("userinput.default.password", password);

        properties.setProperty("userinput.login.silently", isLoginSilently.toString());

        properties.setProperty("sounds.enabled", isSoundsEnabled.toString());

        properties.setProperty("check.tasks.timeout", checkTasksTimeout.toString());

        properties.setProperty("popup.autoclose.timeout", autoClosePopupTimeout.toString());

        properties.setProperty("unread.tasks.notification.timeout", unreadTasksNotificationTimeout.toString());

        final String srcProperties = System.getProperty("user.home") + "/config.properties";

        try (OutputStream out = new FileOutputStream(srcProperties)) {
            properties.store(out, "Runa Task notifier config");
        } catch (IOException e) {
            log.warn("Unable to save " + srcProperties + ": " + e);
        }
    }

    public String getUrl() {
        return protocol + "://" + host + ":" + port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isSoundsEnabled() {
        return isSoundsEnabled;
    }

    public void setSoundsEnabled(boolean isSoundEnabled) {
        this.isSoundsEnabled = isSoundEnabled;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getCheckTasksTimeout() {
        return checkTasksTimeout;
    }

    public void setCheckTasksTimeout(Integer checkTasksTimeout) {
        this.checkTasksTimeout = checkTasksTimeout;
    }

    public Integer getAutoClosePopupTimeout() {
        return autoClosePopupTimeout;
    }

    public void setAutoClosePopupTimeout(Integer autoClosePopupTimeout) {
        this.autoClosePopupTimeout = autoClosePopupTimeout;
    }

    public Integer getUnreadTasksNotificationTimeout() {
        return unreadTasksNotificationTimeout;
    }

    public void setUnreadTasksNotificationTimeout(Integer unreadTasksNotificationTimeout) {
        this.unreadTasksNotificationTimeout = unreadTasksNotificationTimeout;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public Boolean isLoginSilently() {
        return isLoginSilently;
    }

    public void setLoginSilently(Boolean isLoginSilently) {
        this.isLoginSilently = isLoginSilently;
    }

    public HttpURLConnection getConnection() {
        return new ConnectionHelper().getConnection();
    }

    private class ConnectionHelper {

        private HttpURLConnection connection = null;

        private int setConnection(String versionUrl) {
            try {
                URL url = new URL(versionUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Runa Task Notifier");
                setProtocol(url.getProtocol());
                setPort(url.getPort() == -1 ? "80" : Integer.toString(url.getPort()));
                setHost(url.getHost());
                return connection.getResponseCode();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return HttpURLConnection.HTTP_BAD_REQUEST;
        }

        public ConnectionHelper() {

            protocol = protocol.equals("auto") ? "https" : protocol;
            String versionUrl = getUrl() + "/wfe/version";
            
            if (setConnection(versionUrl) == HttpURLConnection.HTTP_MOVED_PERM) {
                setConnection(connection.getHeaderField("Location"));
            }
        }

        public HttpURLConnection getConnection() {
            return connection;
        }

    }

}
