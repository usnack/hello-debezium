#!/bin/sh

echo ""
echo "========================================================"
echo "================= TRY ADD USER ==================="
echo "========================================================"
echo ""

while getopts "u:p:" var
do
   case "$var" in
       u) NEW_USER=${OPTARG};;
       p) NEW_PWD=${OPTARG};;
   esac
done


echo ""
echo "++++++++++++ ENV List ++++++++++++"
export ORACLE_PDB=ORCLPDB1
echo "+++ [FIXED] ORACLE_PDB : ${ORACLE_PDB}"
echo "+++ [CONFIGURED] ORACLE_PWD : ${ORACLE_PWD}"

echo "+++ NEW_USER : ${NEW_USER}"
echo "+++ NEW_PWD : ${NEW_PWD}"
echo "++++++++++++++++++++++++++++++++++"
echo ""

sqlplus sys/${ORACLE_PWD} as sysdba <<- EOF
  ALTER SESSION SET container=${ORACLE_PDB};
  CREATE USER ${NEW_USER} IDENTIFIED BY ${NEW_PWD};
  GRANT CONNECT TO ${NEW_USER};
  GRANT CREATE SESSION TO ${NEW_USER};
  GRANT CREATE TABLE TO ${NEW_USER};
  GRANT CREATE SEQUENCE to ${NEW_USER};
  ALTER USER ${NEW_USER} QUOTA 100M on users;
  exit;
EOF

echo ""
echo "========================================================"
echo "================= DONE ADD USER ==============="
echo "========================================================"
echo ""