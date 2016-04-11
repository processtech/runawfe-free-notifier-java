; Java Launcher
;--------------

!include functions.nsi

Name "Runa Notifier"
Caption "Runa Java Launcher"
Icon "..\img\runa.ico"
OutFile "runa_tasks.exe"

SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow

/** Some Vars that are used */
var command
var vm_param
var url_param
var profile_param
var splash_param

Section ""

  /**
   *	Entry Point
   */

      /* Check for parameter -profile */
      !insertmacro GetParameterValue "-profile " ""
      Pop $R0
      StrCpy $profile_param $R0

      /* Check for parameter -vm */
      !insertmacro GetParameterValue2 "-vm " ""
      Pop $R0
      StrCpy $vm_param $R0

      /* Check for parameter -noSplash */
      !insertmacro GetParameterValue3 "-showSplash " "true"
      Pop $R0
      StrCpy $splash_param $R0

      /* Check for URL parameter */
      !insertmacro GetParameterValue4 "-url " ""
      Pop $R0
      StrCpy $url_param $R0

      /* If -vm is set and javaw.exe exists, use that as JVM */
      IfFileExists "$vm_param/bin/javaw.exe" UseCustomJVM UseDefaultJVM


  /**
   *	Label: Use the custom Java VM that was given as parameter
   */
  UseCustomJVM:

      StrCpy $command '"$vm_param/bin/javaw.exe" -Xmx134217728 -Djava.library.path=. -cp .;rtn.jar '

      StrCmp $profile_param '' AppendNoSplash AppendProfile


  /**
   *	Label: Use the default Java VM
   */
  UseDefaultJVM:

      /* Search for the JRE */
      Call GetJRE
      Pop $R0

      StrCpy $command '"$R0" -Xmx134217728 -Djava.library.path=. -cp .;rtn.jar '

      StrCmp $profile_param '' AppendNoSplash AppendProfile


  /**
   *	Label: Append Profile if set
   */
  AppendProfile:

      StrCpy $command '$command -Duser.home="$profile_param" '

      Goto AppendNoSplash


  /**
   *	Label: Append NoSplash if set
   */
  AppendNoSplash:

      StrCmp $splash_param 'true' AppendUrl ProceedSplash

      ProceedSplash:

      StrCpy $command '$command -Dnet.sourceforge.rssowl.noSplash '

      Goto AppendUrl


  /**
   *	Label: Append URL
   */
  AppendUrl:

      StrCpy $command '$command ru.runa.notifier.PlatformLoader $url_param'

      Goto LaunchCommand


  /**
   *	Label: Launch Command
   */
  LaunchCommand:

  SetOutPath $EXEDIR

  IfFileExists "rtn.jar" Proceed ErrorMessageFileNotFound

  /**
   *	Label: Proceed with execution
   */
  Proceed:
  	Exec $command
  	IfErrors ErrorMessageJavaNotFound Finish


  /**
   *	Label: Java was not found
   */
  ErrorMessageJavaNotFound:
  	MessageBox MB_ICONSTOP "Could not launch Runa Tasks Notifier. Please refer to INSTALL.txt."
  	Goto Finish


  /**
   *	Label: RSSOwl.jar was not found
   */
  ErrorMessageFileNotFound:
  	MessageBox MB_ICONSTOP "The executable 'runa_tasks.exe' has to be in the same directory where 'rtn.jar' is located."
  	Goto Finish


  /**
   *	Label: Finish
   */
  Finish:


SectionEnd