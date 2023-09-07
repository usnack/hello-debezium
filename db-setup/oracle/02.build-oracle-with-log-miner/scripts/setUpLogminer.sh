#!/bin/sh

echo ""
echo "========================================================"
echo "================= TRY LOGMINER SETUP ==================="
echo "========================================================"
echo ""

echo ""
echo "++++++++++++ ENV List ++++++++++++"
export ORACLE_SID=ORCLCDB
echo "+++ [FIXED] ORACLE_SID : ${ORACLE_SID}"
export ORACLE_PDB=ORCLPDB1
echo "+++ [FIXED] ORACLE_PDB : ${ORACLE_PDB}"
echo "+++ [CONFIGURED] ORACLE_PWD : ${ORACLE_PWD}"
echo "++++++++++++++++++++++++++++++++++"
echo ""



mkdir -p /opt/oracle/oradata/recovery_area

sqlplus /nolog <<- EOF
	CONNECT sys/${ORACLE_PWD} AS SYSDBA
	alter system set db_recovery_file_dest_size = 10G;
	alter system set db_recovery_file_dest = '/opt/oracle/oradata/recovery_area' scope=spfile;
	shutdown immediate
	startup mount
	alter database archivelog;
	alter database open;
        -- Should show "Database log mode: Archive Mode"
	archive log list
	exit;
EOF


# Enable LogMiner required database features/settings
sqlplus sys/${ORACLE_PWD} as sysdba <<- EOF
  ALTER DATABASE ADD SUPPLEMENTAL LOG DATA (ALL) COLUMNS;
  ALTER PROFILE DEFAULT LIMIT FAILED_LOGIN_ATTEMPTS UNLIMITED;
  exit;
EOF

# Create Log Miner Tablespace and User
sqlplus sys/${ORACLE_PWD} as sysdba <<- EOF
  CREATE TABLESPACE LOGMINER_TBS DATAFILE '/opt/oracle/oradata/${ORACLE_SID}/logminer_tbs.dbf' SIZE 25M REUSE AUTOEXTEND ON MAXSIZE UNLIMITED;
  exit;
EOF

sqlplus sys/${ORACLE_PWD} as sysdba <<- EOF
  ALTER SESSION SET container=${ORACLE_PDB};
  CREATE TABLESPACE LOGMINER_TBS DATAFILE '/opt/oracle/oradata/${ORACLE_SID}/${ORACLE_PDB}/logminer_tbs.dbf' SIZE 25M REUSE AUTOEXTEND ON MAXSIZE UNLIMITED;
  exit;
EOF

echo ""
echo "========================================================"
echo "================= DONE LOGMINER SETUP =================="
echo "========================================================"
echo ""