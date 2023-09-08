#!/bin/bash

# Run Oracle Base Container Before This Task
# docker run --rm --name oracle-logminer-snapshot -v ./scripts/setUpLogminer.sh:/opt/oracle/scripts/startup/Z.100.setUpLogminer.sh oracle/database:19.3.0-ee-lite


docker commit oracle-logminer-snapshot oracle/database:19.3.0-ee-logminer-snapshot \
&& \
docker build -t oracle/database:19.3.0-ee-logminer .



