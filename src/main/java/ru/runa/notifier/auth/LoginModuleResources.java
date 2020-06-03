
package ru.runa.notifier.auth;

import java.util.Map;

import ru.runa.notifier.util.PropertyResources;

class LoginModuleResources {
    private final static PropertyResources PROPERTIES = new PropertyResources("kerberos.properties");

    private static final String APP_NAME_PROPERTY = "appName";
    private static final String MODULE_CLASS_NAME_PROPERTY = "moduleClassName";
    private static final String SERVER_PRINCIPAL_PROPERTY = "serverPrincipal";

    public static Map<String, String> getInitParameters() {
        return PROPERTIES.getAllProperties();
    }

    public static String getAppName() {
        return PROPERTIES.getStringProperty(APP_NAME_PROPERTY);
    }

    public static String getServerPrincipal() {
        return PROPERTIES.getStringProperty(SERVER_PRINCIPAL_PROPERTY);
    }

    public static String getModuleClassName() {
        return PROPERTIES.getStringProperty(MODULE_CLASS_NAME_PROPERTY);
    }

}
