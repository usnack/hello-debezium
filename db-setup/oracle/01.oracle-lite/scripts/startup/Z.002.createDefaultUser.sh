#!/bin/sh


if [ "${DEFAULT_USER}" != "" ] && [ "${DEFAULT_PWD}" != "" ]; then
  echo ""
  echo "#####################################################################"
  echo "##########              START [ADD DEFAULT USER]           ##########"
  echo "#####################################################################"

  echo ""
  echo "++++++++++++ VARIABLE LIST ++++++++++++"

  echo "+ CONFIGURED"
  echo "+-- DEFAULT_USER : ${DEFAULT_USER}"
  echo "+-- DEFAULT_PWD : ${DEFAULT_PWD}"

  echo "+++++++++++++++++++++++++++++++++++++++"

  ~/addUser.sh -u ${DEFAULT_USER} -p ${DEFAULT_PWD}

  echo ""
  echo "#####################################################################"
  echo "##########              FINISH [ADD DEFAULT USER]          ##########"
  echo "#####################################################################"

fi