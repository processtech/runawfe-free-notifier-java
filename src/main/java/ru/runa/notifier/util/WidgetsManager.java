
package ru.runa.notifier.util;

import org.eclipse.swt.widgets.Widget;

public class WidgetsManager {

	  public static boolean isset(Widget widget) {
		    return (widget != null && !widget.isDisposed());
		  }

}
