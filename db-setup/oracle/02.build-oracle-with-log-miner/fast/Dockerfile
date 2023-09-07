FROM oracle/database:19.3.0-ee-logminer-snapshot
ENV ORACLE_PWD=logminer
ENV LOGMINER_USER=c##logminer
ENV LOGMINER_PWD=logminer
RUN rm -rf /opt/oracle/scripts/startup/901.setUpLogminer.sh
COPY initLogminerUser.sh /opt/oracle/scripts/startup/902.initLogminerUser.sh