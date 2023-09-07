FROM oracle/database:19.3.0-ee-snapshot
ENV ORACLE_PWD="secret"
ENV DEFAULT_USER=""
ENV DEFAULT_PWD=""
COPY scripts/addUser.sh addUser.sh
COPY scripts/startup/Z.001.configurePassword.sh scripts/startup/Z.002.createDefaultUser.sh /opt/oracle/scripts/startup/
