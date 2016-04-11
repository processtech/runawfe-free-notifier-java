package ru.runa.notifier.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;

public enum AppServerType {
    jboss4("http://${server.name}:${server.port}/runawfe-wfe-service-${server.version}/SERVICE_NAMEServiceBean?wsdl"),
    //
    jboss7("http://${server.name}:${server.port}/wfe-service-${server.version}/SERVICE_NAMEWebService/SERVICE_NAMEAPI?wsdl"),
    //
    auto("");

    private final String urlPattern;

    private AppServerType(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getUrlPattern() {
        if (auto == this) {
            try {
                URL url = new URL(ResourcesManager.getAppServerVersionUrl());
                InputStreamReader reader = new InputStreamReader(url.openStream());
                String type = CharStreams.toString(reader);
                reader.close();
                int colonIndex = type.indexOf(":");
                if (colonIndex != -1) {
                    type = type.substring(colonIndex + 1);
                }
                return AppServerType.valueOf(type).getUrlPattern();
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }
        return urlPattern;
    }
}
