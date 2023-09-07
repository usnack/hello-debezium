#!/bin/sh

echo ""
echo "#####################################################################"
echo "##########              START [ADD USER]                   ##########"
echo "#####################################################################"

while getopts "u:p:" var
do
   case "$var" in
       u) NEW_USER=${OPTARG};;
       p) NEW_PWD=${OPTARG};;
   esac
done

echo ""
echo "++++++++++++ VARIABLE LIST ++++++++++++"

echo "+ FIXED"
export ORACLE_PDB=ORCLPDB1
echo "+-- ORACLE_PDB : ${ORACLE_PDB}"

echo "+ CONFIGURED"
echo "+-- ORACLE_PWD : ${ORACLE_PWD}"
echo "+-- NEW_USER : ${NEW_USER}"
echo "+-- NEW_PWD : ${NEW_PWD}"

echo "+++++++++++++++++++++++++++++++++++++++"


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
echo "#####################################################################"
echo "##########              FINISH [ADD USER]                  ##########"
echo "#####################################################################"
