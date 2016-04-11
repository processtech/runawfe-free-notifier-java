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

package ru.runa.notifier.auth;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.Configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import ru.runa.notifier.GUI;
import ru.runa.notifier.util.ClassLoaderUtil;
import ru.runa.notifier.util.ResourcesManager;
import ru.runa.wfe.webservice.User;

/**
 * Created on 2006
 * 
 * @author Gritsenko_S
 */
public class LoginHelper {
    private static final Log log = LogFactory.getLog(LoginHelper.class);

    private static User user = null;
    private static String webParam = null;
    private static boolean tryLogin = true;

    private static Map<String, String> authenticators = new HashMap<String, String>();

    static {
        Configuration.setConfiguration(new LoginConfiguration());

        authenticators.put("kerberos", "ru.runa.notifier.auth.KerberosAuthenticator");
        authenticators.put("sspiKerberos", "ru.runa.notifier.auth.SSPIKerberosAuthenticator");
        authenticators.put("userinput", "ru.runa.notifier.auth.UserInputAuthenticator");
    }

    public static boolean isLogged() {
        return (user != null);
    }

    public static User getUser() {
        return user;
    }

    public static String getWebParameters() {
        return webParam;
    }

    public static void login() {
        if (!tryLogin) {
            return;
        }
        String authType = ResourcesManager.getAuthenticationType();
        Authenticator authenticator = ClassLoaderUtil.instantiate(authenticators.get(authType));
        while (user == null) {
            if (!tryLogin) {
                return;
            }
            try {
                user = authenticator.authenticate();
                if (user != null) {
                    webParam = authenticator.getParamForWeb();
                    if (log.isDebugEnabled()) {
                        log.debug("Authenticated.");
                    }
                    return;
                }
            } catch (Exception e) {
                log.error("Authentication failed", e);
            }
            if (!tryLogin) {
                return;
            }
            if (authenticator.isRetryDialogEnabled()) {
                showRetryDialog();
            } else {
                try {
                    Thread.sleep(60000);
                } catch (Exception e) {
                }
            }
        }
    }

    public static void reset() {
        user = null;
        webParam = null;
    }

    public static void setLoginEnable(boolean isLoginEnable) {
        tryLogin = isLoginEnable;
    }

    static boolean result = false;

    private static boolean showRetryDialog() {
        result = false;
        if (GUI.display != null) {
            GUI.display.syncExec(new Runnable() {

                @Override
                public void run() {
                    MessageBox messageBox = new MessageBox(GUI.shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
                    messageBox.setMessage(ResourcesManager.getRetryMessage());
                    messageBox.setText(ResourcesManager.getErrorLoginMessage());
                    if ((messageBox.open() & SWT.YES) != 0) {
                        setLoginEnable(true);
                    } else {
                        setLoginEnable(false);
                    }
                }
            });
        }
        return result;
    }

}
