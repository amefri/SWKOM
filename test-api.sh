#!/bin/bash

# URL der API
BASE_URL="http://localhost:8081/api/documents"
DOC_ID="20"

# Funktion zur Überprüfung der HTTP-Statuscodes
check_response() {
  local response_code=$1
  local expected_code=$2
  local operation=$3

  if [[ "$response_code" -eq "$expected_code" ]]; then
    echo "Erfolg: $operation war erfolgreich (HTTP Status: $response_code)"
  else
    echo "Fehler: $operation fehlgeschlagen (HTTP Status: $response_code)"
  fi
}

# 1. POST: Dokument hochladen
echo "1. POST: Dokument hochladen"
post_response=$(curl -X POST "$BASE_URL/post_document" \
  -F "document=Simple Test Document" \
  -F "file=@HelloWorld.pdf" \
  -H "Content-Type: multipart/form-data" \
  -w "%{http_code}" -o /dev/null -s)

check_response "$post_response" "201" "Dokument hochladen"
echo -e "\n"

# 2. GET: Alle Dokumente abrufen
echo "2. GET: Alle Dokumente abrufen"
get_response=$(curl -X GET "$BASE_URL" \
  -H "Accept: application/json" -s)

if [[ -n "$get_response" ]]; then
  echo "Erfolg: Dokumente erfolgreich abgerufen:"
  echo "$get_response"  # JSON-Ausgabe ohne Formatierung
else
  echo "Fehler: Dokumente konnten nicht abgerufen werden."
fi
echo -e "\n"

# 3. GET: Ein spezifisches Dokument abrufen
echo "3. GET: Ein spezifisches Dokument abrufen"
get_doc_response=$(curl -X GET "$BASE_URL/$DOC_ID" \
  -H "Accept: application/json" \
  -s -o response.json)

response_code=$(curl -X GET "$BASE_URL/$DOC_ID" \
  -H "Accept: application/json" \
  -w "%{http_code}" -o /dev/null -s)

if [[ "$response_code" -eq 200 ]]; then
  echo "Erfolg: Dokument mit ID $DOC_ID erfolgreich abgerufen:"
  cat response.json  # JSON-Ausgabe ohne Formatierung
else
  echo "Fehler: Dokument mit ID $DOC_ID konnte nicht abgerufen werden (HTTP Status: $response_code)"
fi
rm -f response.json
echo -e "\n"

# 4. PUT: Dokument aktualisieren
echo "4. PUT: Dokument aktualisieren"
put_response=$(curl -X PUT "$BASE_URL/$DOC_ID" \
  -H "Content-Type: application/json" \
  -d '{"title": "Updated Test Document"}' \
  -w "%{http_code}" -o /dev/null -s)

check_response "$put_response" "204" "Dokument aktualisieren"
echo -e "\n"

# 5. DELETE: Dokument löschen
echo "5. DELETE: Dokument löschen"

delete_response=$(curl -X DELETE "$BASE_URL/$DOC_ID" \
  -w "%{http_code}" -o /dev/null -s)

check_response "$delete_response" "204" "Dokument löschen"
echo -e "\n"
echo "2. GET: Alle Dokumente abrufen nachdem Dokument gelöscht wurde"
get_response=$(curl -X GET "$BASE_URL" \
  -H "Accept: application/json" -s)

if [[ -n "$get_response" ]]; then
  echo "Erfolg: Dokumente erfolgreich abgerufen:"
  echo "$get_response"  # JSON-Ausgabe ohne Formatierung
else
  echo "Fehler: Dokumente konnten nicht abgerufen werden."
fi
echo -e "\n"
