
package ru.runa.notifier.auth;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

class LoginConfiguration extends Configuration {
    
    static {
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
    }

    public LoginConfiguration() {
    }

    @Override
	public AppConfigurationEntry[] getAppConfigurationEntry(String appName){
        if (appName.equals(LoginModuleResources.getAppName())) {
            AppConfigurationEntry appConfigurationEntry = new AppConfigurationEntry(
            		LoginModuleResources.getModuleClassName(), 
            		AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, 
            		LoginModuleResources.getInitParameters());
            
            return new AppConfigurationEntry[]{ appConfigurationEntry };
        }
        return null;
    }

    @Override
	public void refresh() {
    }
}
