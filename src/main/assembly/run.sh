#export MOZILLA_FIVE_HOME=/usr/lib/firefox
#export LD_LIBRARY_PATH=/usr/lib/firefox

java -Dorg.eclipse.swt.browser.UseWebKitGTK=true -cp ".:rtn.jar:swt-gtk.jar" ru.runa.notifier.PlatformLoader 
