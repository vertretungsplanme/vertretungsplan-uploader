!include "MUI2.nsh"

 Name "vertretungsplan-uploader"
 OutFile "dist/vertretungsplan-uploader.exe"

 InstallDir "$PROGRAMFILES64\vertretungsplan-uploader"

 InstallDirRegKey HKCU "Software\vertretungsplan-uploader" ""

 RequestExecutionLevel admin

 !define MUI_ABORTWARNING
 !define MUI_ICON "..\img\icon_on_shape.ico"
 !define MUI_WELCOMEFINISHPAGE_BITMAP "..\img\installer.bmp"
 !define MUI_UNWELCOMEFINISHPAGE_BITMAP "..\img\installer.bmp"

 !insertmacro MUI_PAGE_WELCOME
 !insertmacro MUI_PAGE_LICENSE "..\LICENSE"
 !insertmacro MUI_PAGE_COMPONENTS
 !insertmacro MUI_PAGE_DIRECTORY
 !insertmacro MUI_PAGE_INSTFILES
 !insertmacro MUI_PAGE_FINISH

 !insertmacro MUI_UNPAGE_WELCOME
 !insertmacro MUI_UNPAGE_CONFIRM
 !insertmacro MUI_UNPAGE_INSTFILES
 !insertmacro MUI_UNPAGE_FINISH


 !insertmacro MUI_LANGUAGE "English"

 Section "vertretungsplan-uploader" SecBase
   SetOutPath "$INSTDIR"
   CreateDirectory "$INSTDIR"

   File /r "build\launch4j\*"
   File /r /x deb.tmp "jre"

   CreateDirectory "$SMPROGRAMS\vertretungsplan-uploader"
   CreateShortCut "$SMPROGRAMS\vertretungsplan-uploader\vertretungsplan-uploader.lnk" "$INSTDIR\vertretungsplan-uploader.exe"
   CreateShortCut "$SMPROGRAMS\vertretungsplan-uploader\Uninstall.lnk" "$INSTDIR\uninstall.exe"

   ;Store installation folder
   WriteRegStr HKCU "Software\vertretungsplan-uploader" "" $INSTDIR

   DetailPrint "Register vertretungsplan-uploader URI Handler"
   DeleteRegKey HKCR "vertretungsplan-uploader"
   WriteRegStr HKCR "vertretungsplan-uploader" "" "URL: vertretungsplan-uploader"
   WriteRegStr HKCR "vertretungsplan-uploader" "URL Protocol" "vertretungsplan-uploader setup URLs"
   WriteRegStr HKCR "vertretungsplan-uploader\DefaultIcon" "" "$INSTDIR\vertretungsplan-uploader.exe"
   WriteRegStr HKCR "vertretungsplan-uploader\shell" "" ""
   WriteRegStr HKCR "vertretungsplan-uploader\shell\Open" "" ""
   WriteRegStr HKCR "vertretungsplan-uploader\shell\Open\command" "" "$INSTDIR\vertretungsplan-uploader.exe %1"

   ;Create uninstaller
   WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\vertretungsplan-uploader" "DisplayName" "vertretungsplan-uploader"
   WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\vertretungsplan-uploader" "UninstallString" "$INSTDIR\uninstall.exe"

   WriteUninstaller "$INSTDIR\uninstall.exe"
 SectionEnd

 Section "Desktop Shortcut"
    CreateShortCut "$DESKTOP\vertretungsplan-uploader.lnk" "$INSTDIR\vertretungsplan-uploader.exe" ""
 SectionEnd

 ;Language strings
 LangString DESC_SecBase ${LANG_ENGLISH} "Vertretungsplan.app Uploader"

 ;Assign language strings to sections
 !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
 !insertmacro MUI_DESCRIPTION_TEXT ${SecBase} $(DESC_SecBase)
 !insertmacro MUI_FUNCTION_DESCRIPTION_END

 Section "Uninstall"
   Delete "$INSTDIR\*.jar"
   Delete "$INSTDIR\*.exe"
   RMDIR  /r "$INSTDIR\icons"
   RMDIR  /r "$INSTDIR\vertretungsplan-uploader"
   RMDIR  /r "$INSTDIR\lib"
   RMDIR  /r "$INSTDIR\jre"

   Delete "$SMPROGRAMS\vertretungsplan-uploader\vertretungsplan-uploader.lnk"
   Delete "$SMPROGRAMS\vertretungsplan-uploader\Uninstall.lnk"
   RMDIR "$SMPROGRAMS\vertretungsplan-uploader"
   Delete "$DESKTOP\vertretungsplan-uploader.lnk"

   RMDir "$INSTDIR"

   DeleteRegKey /ifempty HKCU "Software\vertretungsplan-uploader"
   DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\vertretungsplan-uploader"
   DeleteRegKey HKCR "vertretungsplan-uploader"
 SectionEnd