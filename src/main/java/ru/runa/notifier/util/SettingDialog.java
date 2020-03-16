/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.runa.notifier.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import ru.runa.notifier.GUI;

/**
 *
 * @author e.sladkov
 */
public class SettingDialog extends Dialog {

    private Shell shell;

    private Text hostField;

    private Text portField;

    private Text loginField;
    
    private Text passwordField;
    
    private Text checkTasksTimeoutField;
    
    private Text autoClosePopupTimeoutField;
    
    private Text unreadTasksNotificationTimeoutField;
            
    private Composite body =  null;
    
    private Composite pnlButtons = null; 
    
    public SettingDialog(Shell parent) {
        super(parent);
    }

    private void createBodyLeft(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.BORDER);

        GridLayout fillLayout = new GridLayout();
        fillLayout.numColumns = 2;
        composite.setLayout(fillLayout);
      
        
        Label label = new Label(composite, SWT.CENTER);
        label.setText(ResourcesManager.getLabelProtocol());
        
        Group buttonRbGroup = new Group(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        buttonRbGroup.setLayout(gridLayout);
        buttonRbGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      
        String items[] = {"http", "https", "auto"};
        for (String item : items) {
            Button button = new Button(buttonRbGroup, SWT.RADIO);
            button.setText(item);
            button.setSelection(item.equals(GUI.setting.getProtocol()));
            button.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    Button source = (Button) e.getSource();
                    GUI.setting.setProtocol(source.getText());
                }
            });
        }
        
        new Label(composite, SWT.CENTER).setText(ResourcesManager.getLabelHost());
        hostField = new Text(composite, SWT.BORDER);
        hostField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        hostField.setText(GUI.setting.getHost());

        new Label(composite, SWT.CENTER).setText(ResourcesManager.getLabelPort());
        portField = new Text(composite, SWT.BORDER);
        portField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        portField.setText(GUI.setting.getPort());
        
        
        SelectionListener soundButtonListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Button button = ((Button) event.widget);
                GUI.setting.setSoundsEnabled(button.getSelection());
            }
        };
               
        
        new Label(composite, SWT.CENTER).setText(ResourcesManager.getLabelCheckTasks());
        checkTasksTimeoutField = new Text(composite, SWT.BORDER);
        checkTasksTimeoutField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        checkTasksTimeoutField.setText(Integer.toString(GUI.setting.getCheckTasksTimeout()/1000));
        
        new Label(composite, SWT.CENTER).setText(ResourcesManager.getLabelPopupAutoclose());
        autoClosePopupTimeoutField = new Text(composite, SWT.BORDER);
        autoClosePopupTimeoutField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        autoClosePopupTimeoutField.setText(Integer.toString(GUI.setting.getAutoClosePopupTimeout()/1000));
        
        new Label(composite, SWT.CENTER).setText(ResourcesManager.getLabelUnreadTasks());
        unreadTasksNotificationTimeoutField = new Text(composite, SWT.BORDER);
        unreadTasksNotificationTimeoutField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        unreadTasksNotificationTimeoutField.setText(Integer.toString(GUI.setting.getUnreadTasksNotificationTimeout()/1000));
        
        Button soundEnabled = new Button(composite, SWT.CHECK);
        soundEnabled.setText(ResourcesManager.getLabelSoundEnabled());
        soundEnabled.setSelection(GUI.setting.isSoundsEnabled());
        soundEnabled.addSelectionListener(soundButtonListener);
        
    }
    
     private void createBodyRight(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.BORDER);
        GridLayout fillLayout = new GridLayout();
        fillLayout.numColumns = 2;
        composite.setLayout(fillLayout);

        Label label = new Label(composite, SWT.CENTER);
        label.setText(ResourcesManager.getLabelAuthType());
        
        Group buttonRbGroup = new Group(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        buttonRbGroup.setLayout(gridLayout);
        buttonRbGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        
        String items[] =  ResourcesManager.getAuthTypes();// {"userinput", "sspiKerberos", "kerberos"};

        for (String item : items) {
            Button button = new Button(buttonRbGroup, SWT.RADIO);
            button.setText(item);
            button.setSelection(item.equals(GUI.setting.getAuthenticationType()));
            button.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    Button source = (Button) e.getSource();
                    System.out.println(source.getText());
                }

            });
        }
        
        SelectionListener loginSilentlyButtonListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Button button = ((Button) event.widget);
                GUI.setting.setLoginSilently(button.getSelection());
            }
        ;
        };
        
        new Label(composite, SWT.CENTER).setText(ResourcesManager.getUserName() + ":");
        loginField = new Text(composite, SWT.BORDER);
        loginField.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));
        loginField.setText(GUI.setting.getLogin());
        
        new Label(composite, SWT.CENTER).setText(ResourcesManager.getPasswordName() + ": ");
        passwordField = new Text(composite, SWT.BORDER | SWT.PASSWORD);
        passwordField.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));
        passwordField.setText(GUI.setting.getPassword());
        
        Button loginSilently = new Button(composite, SWT.CHECK);
        loginSilently.setText(ResourcesManager.getLabelLoginSilently());
        loginSilently.setSelection(GUI.setting.isLoginSilently());
        loginSilently.addSelectionListener(loginSilentlyButtonListener);
  
    }
    
    private void createPanelCenter(Composite parent)
    {
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL | 
                                         GridData.FILL_VERTICAL);
        gridData.grabExcessVerticalSpace = true;
        body = new Composite (parent, SWT.NONE);
        body.setLayoutData(gridData);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns   = 2;
        gridLayout.marginHeight = 0; 
        gridLayout.marginWidth  = 0;
        body.setLayout(gridLayout);

        SashForm sashForm = new SashForm(body, SWT.NONE);
        sashForm.setOrientation(SWT.HORIZONTAL);
        gridData = new GridData(GridData.FILL_HORIZONTAL | 
                                GridData.FILL_VERTICAL);
        gridData.horizontalSpan = 1;
        sashForm.setLayoutData(gridData);

        createBodyLeft(sashForm);
        createBodyRight(sashForm);

        sashForm.setWeights(new int[] { 6, 5 });
    }
    
    private void createPanelControls(Composite parent)
    {
        // Панель кнопок управления
        pnlButtons = new Composite(parent, SWT.NONE);

        GridData gridData = new GridData(SWT.LEFT);
        gridData.heightHint = 36;
        pnlButtons.setLayoutData(gridData);

        // Размещение кнопки в панели pnlButtons
        pnlButtons.setLayout(new FillLayout());
      
        Button buttonApply = new Button(pnlButtons, SWT.PUSH);
        buttonApply.setText(ResourcesManager.getLabelApply());
       
        buttonApply.addListener(SWT.Selection, (Event event) -> {
            GUI.setting.setHost(hostField.getText());
            GUI.setting.setPort(portField.getText());
            
            GUI.setting.setLogin(loginField.getText());
            GUI.setting.setPassword(passwordField.getText());
            
            GUI.setting.setCheckTasksTimeout(Integer.parseInt(checkTasksTimeoutField.getText()));
            GUI.setting.setAutoClosePopupTimeout(Integer.parseInt(autoClosePopupTimeoutField.getText()));
            GUI.setting.setUnreadTasksNotificationTimeout(Integer.parseInt(unreadTasksNotificationTimeoutField.getText()));
            
            GUI.setting.save();
            shell.dispose();
        });

        Button buttonCancel = new Button(pnlButtons, SWT.PUSH);
        buttonCancel.setText(ResourcesManager.getLabelCancel());
        buttonCancel.addListener(SWT.Selection, (Event event) -> {
            shell.dispose();
        });
        
    }
    
    public void open() {
        Shell parent = getParent();
        shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
        shell.setText(ResourcesManager.getLabelSetting());
        
        GridLayout gridLayout      = new GridLayout();
        gridLayout.numColumns      = 1;
        gridLayout.marginHeight    = 1;
        gridLayout.marginWidth     = 1;
        gridLayout.verticalSpacing = 2;
        shell.setLayout(gridLayout);
        
        GUI.setting.read();
        
        createPanelCenter(shell);
        
        createPanelControls(shell);
        
        shell.pack();
        shell.open();

        org.eclipse.swt.widgets.Display display = parent.getDisplay();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

    }

}
