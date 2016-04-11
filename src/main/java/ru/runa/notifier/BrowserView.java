package ru.runa.notifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

public class BrowserView extends Composite {
    private Browser browser;

    public BrowserView(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(2, false));

        browser = new Browser(this, SWT.FILL);
        browser.setVisible(false);
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.horizontalSpan = 2;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        browser.setLayoutData(data);

        final Label status = new Label(this, SWT.NONE);
        data = new GridData(GridData.FILL_HORIZONTAL);
        status.setLayoutData(data);

        final ProgressBar progressBar = new ProgressBar(this, SWT.NONE);
        data = new GridData();
        data.horizontalAlignment = GridData.END;
        progressBar.setLayoutData(data);

        browser.addProgressListener(new ProgressListener() {

            @Override
            public void changed(ProgressEvent event) {
                if (event.total == 0) {
                    return;
                }
                int ratio = event.current * 100 / event.total;
                progressBar.setSelection(ratio);
            }

            @Override
            public void completed(ProgressEvent event) {
                progressBar.setSelection(event.total);
            }

        });

        browser.addStatusTextListener(new StatusTextListener() {
            @Override
            public void changed(StatusTextEvent event) {
                if (event.text.contains("password")) {
                    status.setText("Processing authentication");
                } else {
                    status.setText(event.text);
                }
            }
        });
    }

    public Browser getBrowser() {
        return browser;
    }

}
