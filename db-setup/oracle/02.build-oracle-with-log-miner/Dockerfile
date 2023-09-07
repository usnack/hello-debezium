FROM oracle/database:19.3.0-ee-fast
ENV ORACLE_PWD=logminer
COPY scripts/addLogminerUser.sh addLogminerUser.sh
COPY scripts/setUpLogminer.sh /opt/oracle/scripts/startup/901.setUpLogminer.sh