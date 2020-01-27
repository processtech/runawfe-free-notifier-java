package ru.runa.notifier;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.runa.notifier.util.ResourcesManager;
import ru.runa.notifier.util.ServerUrl;
import ru.runa.wfe.webservice.AuthenticationAPI;
import ru.runa.wfe.webservice.AuthenticationWebService;
import ru.runa.wfe.webservice.TaskAPI;
import ru.runa.wfe.webservice.TaskWebService;

public class WFEConnection {
    private static final Log log = LogFactory.getLog(WFEConnection.class);

    private static URL getUrl(String serviceName) {
        try {
            String url = ServerUrl.SERVER_URL.getUrl()+ResourcesManager.getWebServiceUrl();
            url = url.replaceAll("SERVICE_NAME", serviceName);
            log.debug("Using " + url);
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static AuthenticationAPI getAuthenticationAPI() {
        return new AuthenticationWebService(getUrl("Authentication")).getAuthenticationAPIPort();
    }

    public static TaskAPI getTaskAPI() {
        return new TaskWebService(getUrl("Task")).getTaskAPIPort();
    }
}
