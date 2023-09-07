#!/bin/sh

echo ""
echo "#####################################################################"
echo "##########              START [CONFIGURE PASSWORD]         ##########"
echo "#####################################################################"

echo ""
echo "++++++++++++ VARIABLE LIST ++++++++++++"

echo "+ CONFIGURED"
echo "+-- ORACLE_PWD : ${ORACLE_PWD}"

echo "+++++++++++++++++++++++++++++++++++++++"


~/setPassword.sh ${ORACLE_PWD}

echo ""
echo "#####################################################################"
echo "##########              FINISH [CONFIGURE PASSWORD]        ##########"
echo "#####################################################################"
