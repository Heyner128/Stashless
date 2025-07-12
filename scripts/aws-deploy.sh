#!/bin/bash
DB_USER=$(aws secretsmanager get-secret-value --secret-id stashless/prod/db | grep -oP '(?<=username\\":\\").*?(?=\\",)' )
DB_PASSWORD=$(aws secretsmanager get-secret-value --secret-id stashless/prod/db | grep -oP '(?<=password\\":\\").*?(?=\\",)' )
echo "DB_USER=${DB_USER}" >> .env
echo "DB_PASSWORD=${DB_PASSWORD}" >> .env
export PORT=4400
PROJECT_NAME=stashless
docker compose -p ${PROJECT_NAME} up --detach --build
sleep 20
curl --fail --retry 5 --retry-delay 10 --retry-connrefused http://localhost:${PORT}