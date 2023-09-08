#!/bin/bash

# Run Oracle Base Container Before This Task
# docker run --rm --name oracle-base oracle/database:19.3.0-ee
# It tasks about 20mins

#docker commit oracle-base oracle/database:19.3.0-ee-snapshot \
#&& \
docker build -t oracle/database:19.3.0-ee-lite .