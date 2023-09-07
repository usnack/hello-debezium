#!/bin/sh

echo ""
echo "========================================================"
echo "================= TRY PASSWORD SETUP ==================="
echo "========================================================"
echo ""

echo "++++++++++ ORACLE_PWD : ${ORACLE_PWD}"

/home/oracle/setPassword.sh ${ORACLE_PWD}



echo ""
echo "========================================================"
echo "================= DONE PASSWORD SETUP =================="
echo "========================================================"
echo ""

sqlplus sys/top_secret as sysdba <<- EOF
  ALTER SESSION SET container=ORCLPDB1;
  CREATE USER debezium IDENTIFIED BY dbz;
  GRANT CONNECT TO debezium;
  GRANT CREATE SESSION TO debezium;
  GRANT CREATE TABLE TO debezium;
  GRANT CREATE SEQUENCE to debezium;
  ALTER USER debezium QUOTA 100M on users;
  exit;
EOF