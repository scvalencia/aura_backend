#!/bin/sh
'/Applications/Postgres.app/Contents/Versions/9.3/bin'/psql -p5432
JAVA_OPTS=-Dhttps.port=9443 activator run