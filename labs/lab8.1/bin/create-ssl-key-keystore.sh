#!/usr/bin/env bash
set -e

# Common Variables for SSL Key Gen
CERT_OUTPUT_PATH="$PWD/resources/opt/kafka/conf/certs"
KEY_STORE="$CERT_OUTPUT_PATH/kafka.keystore"
TRUST_STORE="$CERT_OUTPUT_PATH/kafka.truststore"
PASSWORD=kafka123
KEY_KEY_PASS="$PASSWORD"
KEY_STORE_PASS="$PASSWORD"
TRUST_KEY_PASS="$PASSWORD"
TRUST_STORE_PASS="$PASSWORD"
CLUSTER_NAME=kafka
CERT_AUTH_FILE="$CERT_OUTPUT_PATH/ca-cert"
CLUSTER_CERT_FILE="$CERT_OUTPUT_PATH/${CLUSTER_NAME}-cert"
D_NAME="CN=CloudDurable Image $CLUSTER_NAME cluster, OU=Fenago, O=Fenago"
D_NAME="${D_NAME}, L=San Francisco, ST=CA, C=USA, DC=fenago, DC=com"
DAYS_VALID=365

mkdir -p "$CERT_OUTPUT_PATH"


## TODO Use keytool to generate cluster certificate into a keystore
echo "Create the cluster key for cluster communication."
# HINT: keytool -genkey -keyalg RSA -alias "${CLUSTER_NAME}_cluster" \
#    -keystore "$KEY_STORE" -storepass "$KEY_STORE_PASS" \
#    -keypass "$KEY_KEY_PASS" -dname  "$D_NAME" -validity "$DAYS_VALID"

## TODO Use openssl to Generate a Certificate Authority - CA
echo "Create the Certificate Authority (CA) file to sign keys."
# HINT: openssl req -new -x509 -keyout ca-key -out "$CERT_AUTH_FILE" \
#    -days "$DAYS_VALID" \
#    -passin pass:"$PASSWORD" -passout pass:"$PASSWORD" \
#    -subj "/C=US/ST=CA/L=San Francisco/O=Engineering/CN=fenago.com"

## TODO Use keytool to import CA into Kafka’s truststore
echo "Import the Certificate Authority file into the trust store."
# HINT keytool -keystore "$TRUST_STORE" -alias CARoot \
#    -import -file "$CERT_AUTH_FILE" \
#    -storepass "$TRUST_STORE_PASS" -keypass "$TRUST_KEY_PASS" \
#    -noprompt

## TODO Use keytool to sign a cluster certificate with the CA
echo "Export the cluster certificate from the key store."
# HINT: keytool -keystore "$KEY_STORE" -alias "${CLUSTER_NAME}_cluster" \
#    -certreq -file "$CLUSTER_CERT_FILE" \
#    -storepass "$KEY_STORE_PASS" -keypass "$KEY_KEY_PASS" -noprompt

echo "Sign the cluster certificate with the CA."
# HINT: openssl x509 -req -CA "$CERT_AUTH_FILE" -CAkey ca-key \
#    -in "$CLUSTER_CERT_FILE" -out "${CLUSTER_CERT_FILE}-signed" \
#    -days "$DAYS_VALID" -CAcreateserial -passin pass:"$PASSWORD"

## TODO Use keytool to import CA and Signed Cluster Certificate into Kafka’s keystore
echo "Import the Certificate Authority (CA) file into the key store."
# HINT: keytool -keystore "$KEY_STORE" -alias CARoot -import -file "$CERT_AUTH_FILE" \
#    -storepass "$KEY_STORE_PASS" -keypass "$KEY_KEY_PASS" -noprompt

echo "Import the Signed Cluster Certificate into the key store."
# HINT: keytool -keystore "$KEY_STORE" -alias "${CLUSTER_NAME}_cluster" \
#    -import -file "${CLUSTER_CERT_FILE}-signed" \
#    -storepass "$KEY_STORE_PASS" -keypass "$KEY_KEY_PASS" -noprompt




