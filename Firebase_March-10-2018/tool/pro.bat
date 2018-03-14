ECHO OFF

IF EXIST build GOTO skip_build
MKDIR build
MKDIR build\src
MKDIR build\src\resource

:skip_build
IF NOT EXIST jar MKDIR jar
IF NOT EXIST lib MKDIR lib
IF NOT EXIST javadoc MKDIR javadoc
IF EXIST src GOTO skip_src
MKDIR src
MKDIR src\resource

:skip_src
IF EXIST tool GOTO skip_tool
MKDIR tool
MKDIR tool\ant

:skip_tool
MOVE candr.bat tool
MOVE pro.bat tool
