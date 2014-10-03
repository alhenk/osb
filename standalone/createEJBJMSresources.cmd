@ECHO OFF
SETLOCAL

CALL config_env.cmd
CALL "%BEA_HOME%/wlserver_10.3/server/bin/setWLSEnv.cmd"

java weblogic.WLST scripts/createJmsResources.py
