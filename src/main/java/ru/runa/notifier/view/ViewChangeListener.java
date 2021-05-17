package ru.runa.notifier.view;

import java.util.EventListener;

public interface ViewChangeListener extends EventListener {

    void showInfoPathFormView();
    
    void showBrowserView(String startPageUrl);
    
}
