@REM ----------------------------------------------------------------------------
@REM  Copyright 2001-2006 The Apache Software Foundation.
@REM
@REM  Licensed under the Apache License, Version 2.0 (the "License");
@REM  you may not use this file except in compliance with the License.
@REM  You may obtain a copy of the License at
@REM
@REM       http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM  Unless required by applicable law or agreed to in writing, software
@REM  distributed under the License is distributed on an "AS IS" BASIS,
@REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM  See the License for the specific language governing permissions and
@REM  limitations under the License.
@REM ----------------------------------------------------------------------------
@REM
@REM   Copyright (c) 2001-2006 The Apache Software Foundation.  All rights
@REM   reserved.

@echo off

set ERROR_CODE=0

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto WinNTGetScriptDir

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto WinNTGetScriptDir

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto Win9xGetScriptDir
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

:Win9xGetScriptDir
set SAVEDIR=%CD%
%0\
cd %0\..\.. 
set BASEDIR=%CD%
cd %SAVEDIR%
set SAVE_DIR=
goto repoSetup

:WinNTGetScriptDir
set BASEDIR=%~dp0\..

:repoSetup
set REPO=


if "%JAVACMD%"=="" set JAVACMD=java

if "%REPO%"=="" set REPO=%BASEDIR%\repo

set CLASSPATH="%BASEDIR%"\etc;"%REPO%"\maven-compiler-plugin-3.8.1.jar;"%REPO%"\maven-plugin-api-3.0.jar;"%REPO%"\maven-model-3.0.jar;"%REPO%"\sisu-inject-plexus-1.4.2.jar;"%REPO%"\sisu-inject-bean-1.4.2.jar;"%REPO%"\sisu-guice-2.1.7-noaop.jar;"%REPO%"\maven-artifact-3.0.jar;"%REPO%"\plexus-utils-2.0.4.jar;"%REPO%"\maven-core-3.0.jar;"%REPO%"\maven-settings-3.0.jar;"%REPO%"\maven-settings-builder-3.0.jar;"%REPO%"\maven-repository-metadata-3.0.jar;"%REPO%"\maven-model-builder-3.0.jar;"%REPO%"\maven-aether-provider-3.0.jar;"%REPO%"\aether-impl-1.7.jar;"%REPO%"\aether-spi-1.7.jar;"%REPO%"\aether-api-1.7.jar;"%REPO%"\aether-util-1.7.jar;"%REPO%"\plexus-interpolation-1.14.jar;"%REPO%"\plexus-classworlds-2.2.3.jar;"%REPO%"\plexus-component-annotations-1.5.5.jar;"%REPO%"\plexus-sec-dispatcher-1.3.jar;"%REPO%"\plexus-cipher-1.4.jar;"%REPO%"\maven-shared-utils-3.2.1.jar;"%REPO%"\commons-io-2.5.jar;"%REPO%"\maven-shared-incremental-1.1.jar;"%REPO%"\plexus-java-0.9.10.jar;"%REPO%"\asm-6.2.jar;"%REPO%"\qdox-2.0-M8.jar;"%REPO%"\plexus-compiler-api-2.8.4.jar;"%REPO%"\plexus-compiler-manager-2.8.4.jar;"%REPO%"\plexus-compiler-javac-2.8.4.jar;"%REPO%"\commons-lang3-3.12.0.jar;"%REPO%"\j2html-1.4.0.jar;"%REPO%"\reseau-1.0-SNAPSHOT.jar

set ENDORSED_DIR=
if NOT "%ENDORSED_DIR%" == "" set CLASSPATH="%BASEDIR%"\%ENDORSED_DIR%\*;%CLASSPATH%

if NOT "%CLASSPATH_PREFIX%" == "" set CLASSPATH=%CLASSPATH_PREFIX%;%CLASSPATH%

@REM Reaching here means variables are defined and arguments have been captured
:endInit

%JAVACMD% %JAVA_OPTS%  -classpath %CLASSPATH% -Dapp.name="run" -Dapp.repo="%REPO%" -Dapp.home="%BASEDIR%" -Dbasedir="%BASEDIR%" miage.reseau.server.Server %CMD_LINE_ARGS%
if %ERRORLEVEL% NEQ 0 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=%ERRORLEVEL%

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
goto postExec

:endNT
@REM If error code is set to 1 then the endlocal was done already in :error.
if %ERROR_CODE% EQU 0 @endlocal


:postExec

if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%
