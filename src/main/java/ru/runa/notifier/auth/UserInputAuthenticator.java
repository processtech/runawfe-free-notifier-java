package ru.runa.notifier.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ru.runa.notifier.GUI;
import ru.runa.notifier.WFEConnection;
import ru.runa.notifier.util.ResourcesManager;
import ru.runa.wfe.webservice.User;

public class UserInputAuthenticator implements Authenticator {
    private static final Log log = LogFactory.getLog(UserInputAuthenticator.class);

    private String login = null;
    private String password = null;

    @Override
    public User authenticate() throws Exception {
        try {
            if (ResourcesManager.isLoginSilently()) {
                try {
                    login = ResourcesManager.getDefaultLogin();
                    password = ResourcesManager.getDefaultPassword();
                    return WFEConnection.getAuthenticationAPI().authenticateByLoginPassword(login, password);
                } catch (Exception e) {
                    log.warn("Auth with default credentials failed, requesting", e);
                }
            }
            if (login != null) {
                return WFEConnection.getAuthenticationAPI().authenticateByLoginPassword(login, password);
            }
            if (GUI.display != null) {
                GUI.display.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        LoginDialog loginDialog = new LoginDialog(GUI.shell);
                        String[] userInput = loginDialog.open();
                        if (userInput != null) {
                            login = userInput[0];
                            password = userInput[1];
                        }
                    }
                });
            }
            if (login != null) {
                return WFEConnection.getAuthenticationAPI().authenticateByLoginPassword(login, password);
            }
        } catch (Exception e) {
            log.warn("Auth exception.", e);
            login = null;
            password = null;
            throw e;
        }
        return null;
    }

    @Override
    public String getParamForWeb() {
        String param;
        try {
            param = "login=" + URLEncoder.encode(login, "utf-8") + "&password=" + URLEncoder.encode(password, "utf-8");
            return param;
        } catch (UnsupportedEncodingException e) {
            log.error("Can't create parameters", e);
            return null;
        }
    }

    @Override
    public boolean isRetryDialogEnabled() {
        return true;
    }

    public class LoginDialog extends Dialog {
        private String[] returnValue = null;
        private Shell shell;
        private Text loginField;
        private Text passwordField;

        public LoginDialog(Shell parent) {
            super(parent);
        }

        public String[] open() {
            Shell parent = getParent();
            shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
            shell.setLayout(new GridLayout(1, false));
            shell.setText(ResourcesManager.getLoginMessage());

            Composite composite = new Composite(shell, SWT.NONE);
            composite.setLayout(new GridLayout(2, false));

            Label labelType = new Label(composite, SWT.NONE);
            labelType.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
            labelType.setText(ResourcesManager.getUserName() + ":");
            loginField = new Text(composite, SWT.BORDER);
            GridData typeTextData = new GridData(GridData.FILL_HORIZONTAL);
            typeTextData.minimumWidth = 200;
            loginField.setLayoutData(typeTextData);

            Label labelName = new Label(composite, SWT.NONE);
            labelName.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
            labelName.setText(ResourcesManager.getPasswordName() + ":");
            passwordField = new Text(composite, SWT.BORDER | SWT.PASSWORD);
            passwordField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            Button buttonLogin = new Button(shell, SWT.PUSH);
            buttonLogin.setText(ResourcesManager.getLoginMessage());
            GridData buttonData = new GridData(GridData.FILL_HORIZONTAL);
            buttonData.widthHint = 100;
            buttonData.horizontalAlignment = GridData.END;
            buttonLogin.setLayoutData(buttonData);
            buttonLogin.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    harvestDataAndClose();
                }
            });

            loginField.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    if (keyEvent.keyCode == 13) {
                        harvestDataAndClose();
                    }
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                }
            });
            passwordField.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    if (keyEvent.keyCode == 13) {
                        harvestDataAndClose();
                    }
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                }
            });

            loginField.setText(ResourcesManager.getDefaultLogin());
            passwordField.setText(ResourcesManager.getDefaultPassword());

            loginField.setFocus();

            shell.pack();
            shell.open();

            Display display = parent.getDisplay();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }

            return returnValue;
        }

        private void harvestDataAndClose() {
            returnValue = new String[] { loginField.getText(), passwordField.getText() };
            shell.dispose();
        }
    }
}
