#!/bin/bash
set -e

TEMP_FILE="/tmp/secrets-$(date +%s).yaml"

gpg --decrypt secrets.values.enc.yaml > "$TEMP_FILE"

helm upgrade bankapp ./bankapp-chart \
  -f values.yaml \
  -f "$TEMP_FILE" \
  -n bankapp --install

rm -f "$TEMP_FILE"
