@echo off
set "PATH=%PATH%;%JAVA_HOME%\bin\"
start /b javaw -jar %~f0 %*
exit
