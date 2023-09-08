FROM oracle/database:19.3.0-ee-logminer-snapshot
ENV DEFAULT_LOGMINER_USER=""
ENV DEFAULT_LOGMINER_PWD=""
COPY scripts/addLogminerUser.sh addLogminerUser.sh
COPY scripts/startup/Z.101.createDefaultLogminerUser.sh /opt/oracle/scripts/startup/Z.101.createDefaultLogminerUser.sh
RUN rm -f /opt/oracle/scripts/startup/Z.100.setUpLogminer.sh