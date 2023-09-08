#!/bin/bash

if [ "${DEFAULT_LOGMINER_USER}" != "" ] && [ "${DEFAULT_LOGMINER_PWD}" != "" ]; then
  echo ""
  echo "#####################################################################"
  echo "##########              START [ADD DEFAULT LOGMINER USER]  ##########"
  echo "#####################################################################"


  echo ""
  echo "++++++++++++ VARIABLE LIST ++++++++++++"

  echo "+ CONFIGURED"
  echo "+-- DEFAULT_LOGMINER_USER : ${DEFAULT_LOGMINER_USER}"
  echo "+-- DEFAULT_LOGMINER_PWD : ${DEFAULT_LOGMINER_PWD}"

  echo "+++++++++++++++++++++++++++++++++++++++"

  ~/addLogminerUser.sh -u ${DEFAULT_LOGMINER_USER} -p ${DEFAULT_LOGMINER_PWD}

  echo ""
  echo "#####################################################################"
  echo "##########              FINISH [ADD DEFAULT LOGMINER USER] ##########"
  echo "#####################################################################"
fi


