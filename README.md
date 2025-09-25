# crypto-vault-service

This is a simple Spring Boot service that provides encryption and decryption of values using AES encryption. It uses a
master key stored in a file to derive encryption keys.

## Start up the services

Start spring cloud services and keycloak by running

navigate to:

```
docker/docker-compose.yml
```
run command:
```run
docker compose up -d
```

Then start the crypto vault service by running:

```run
mvn spring-boot:run -Pkeycloak
```

## API Usage example

### Authenticate

Request:

```
curl --location 'http://keycloak-web:8090/realms/openleap-realm/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_secret=2a6O8wicYg5NWXlcN34rT5KpRzQ6F7x5' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'scope=cvs.read' \
--data-urlencode 'client_id=cvs'
```

### Encrypting

Request:

```
curl --location 'localhost:8080/api/cvs/encrypt' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKT0REczN0YXVwVGQ4NGh3aWh6VHZVZkpJTm1vcEoyVmxGcnBuQmNrdExBIn0.eyJleHAiOjE3NTg3OTU1MDcsImlhdCI6MTc1ODc5NTIwNywianRpIjoiYWExNTgyZTctZTc1ZS00ZDg4LWEzYmYtMTc4NTAxNTI2YjljIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrLXdlYjo4MDkwL3JlYWxtcy9vcGVubGVhcC1yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI5ZmYyYmM5ZC01ZDgwLTQzNWUtYTc1Ni1lYjNlYTUxMGQ2ZjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjdnMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1vcGVubGVhcC1yZWFsbSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcmVwb3J0LXNlcnZpY2UgcHJvZmlsZSBkbV9yZWFkIGN2cy5yZWFkIiwiY2xpZW50SG9zdCI6IjE5Mi4xNjguNjUuMSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtb3BlbmxlYXAtcmVhbG0iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWN2cyIsImNsaWVudEFkZHJlc3MiOiIxOTIuMTY4LjY1LjEiLCJjbGllbnRfaWQiOiJjdnMifQ.CKLn0YeM8Y4i4aW88JDhInzv7PvgG9HXfauxjPkZtGPnjzjYYDbHO58IJ-x8IKJl6tXTWdyoAVkwV5jYFlbwjYXN9w29XVfgrIBW5PVUx_9QEfIZp-2rLWBeup9HdUBhivqWAtxOQPnYd51bB6p55dG5hJa0LTuZWllWqbfgP0i2PlaQVFfJP9hws_wTb5i9ksC3iKH7esSWRj9yUymoVVDJlE7uArUzSnLDV6pz8MUnAgMpN9muGOs3Cn7tEZKLFuIP2Wh1E4IBJ7HspMG17QShAkbM52z4WDgsbay6jEao6kLDN5-O-K9ecXHwpO9xoRD-Y3cev2tEk6_V50b_pg' \
--header 'Cookie: JSESSIONID=2C4EC00429737DCC378868E5F473079F' \
--data '{
    "value":"123"
}'
```

Response:

```
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 24
Date: Tue, 28 Nov 2023 21:14:33 GMT

Nss566rC1oNgVlBFSmUwBw==
```

### Decrypting

Request:

```
curl --location 'localhost:8080/api/cvs/decrypt' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKT0REczN0YXVwVGQ4NGh3aWh6VHZVZkpJTm1vcEoyVmxGcnBuQmNrdExBIn0.eyJleHAiOjE3NTg3OTU1MDcsImlhdCI6MTc1ODc5NTIwNywianRpIjoiYWExNTgyZTctZTc1ZS00ZDg4LWEzYmYtMTc4NTAxNTI2YjljIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrLXdlYjo4MDkwL3JlYWxtcy9vcGVubGVhcC1yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI5ZmYyYmM5ZC01ZDgwLTQzNWUtYTc1Ni1lYjNlYTUxMGQ2ZjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjdnMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1vcGVubGVhcC1yZWFsbSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcmVwb3J0LXNlcnZpY2UgcHJvZmlsZSBkbV9yZWFkIGN2cy5yZWFkIiwiY2xpZW50SG9zdCI6IjE5Mi4xNjguNjUuMSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtb3BlbmxlYXAtcmVhbG0iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWN2cyIsImNsaWVudEFkZHJlc3MiOiIxOTIuMTY4LjY1LjEiLCJjbGllbnRfaWQiOiJjdnMifQ.CKLn0YeM8Y4i4aW88JDhInzv7PvgG9HXfauxjPkZtGPnjzjYYDbHO58IJ-x8IKJl6tXTWdyoAVkwV5jYFlbwjYXN9w29XVfgrIBW5PVUx_9QEfIZp-2rLWBeup9HdUBhivqWAtxOQPnYd51bB6p55dG5hJa0LTuZWllWqbfgP0i2PlaQVFfJP9hws_wTb5i9ksC3iKH7esSWRj9yUymoVVDJlE7uArUzSnLDV6pz8MUnAgMpN9muGOs3Cn7tEZKLFuIP2Wh1E4IBJ7HspMG17QShAkbM52z4WDgsbay6jEao6kLDN5-O-K9ecXHwpO9xoRD-Y3cev2tEk6_V50b_pg' \
--header 'Cookie: JSESSIONID=9DBCAEFACC456FADD41B699484EFD207' \
--data '{
    "value":"Nss566rC1oNgVlBFSmUwBw=="
}'
```

Response:

```
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 7
Date: Tue, 28 Nov 2023 21:16:51 GMT

123
```

### Encrypting with user session and id

Request:

```
curl --location 'localhost:8080/api/cvs/encrypt' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKT0REczN0YXVwVGQ4NGh3aWh6VHZVZkpJTm1vcEoyVmxGcnBuQmNrdExBIn0.eyJleHAiOjE3NTg3OTU1MDcsImlhdCI6MTc1ODc5NTIwNywianRpIjoiYWExNTgyZTctZTc1ZS00ZDg4LWEzYmYtMTc4NTAxNTI2YjljIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrLXdlYjo4MDkwL3JlYWxtcy9vcGVubGVhcC1yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI5ZmYyYmM5ZC01ZDgwLTQzNWUtYTc1Ni1lYjNlYTUxMGQ2ZjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjdnMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1vcGVubGVhcC1yZWFsbSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcmVwb3J0LXNlcnZpY2UgcHJvZmlsZSBkbV9yZWFkIGN2cy5yZWFkIiwiY2xpZW50SG9zdCI6IjE5Mi4xNjguNjUuMSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtb3BlbmxlYXAtcmVhbG0iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWN2cyIsImNsaWVudEFkZHJlc3MiOiIxOTIuMTY4LjY1LjEiLCJjbGllbnRfaWQiOiJjdnMifQ.CKLn0YeM8Y4i4aW88JDhInzv7PvgG9HXfauxjPkZtGPnjzjYYDbHO58IJ-x8IKJl6tXTWdyoAVkwV5jYFlbwjYXN9w29XVfgrIBW5PVUx_9QEfIZp-2rLWBeup9HdUBhivqWAtxOQPnYd51bB6p55dG5hJa0LTuZWllWqbfgP0i2PlaQVFfJP9hws_wTb5i9ksC3iKH7esSWRj9yUymoVVDJlE7uArUzSnLDV6pz8MUnAgMpN9muGOs3Cn7tEZKLFuIP2Wh1E4IBJ7HspMG17QShAkbM52z4WDgsbay6jEao6kLDN5-O-K9ecXHwpO9xoRD-Y3cev2tEk6_V50b_pg' \
--header 'Cookie: JSESSIONID=9DBCAEFACC456FADD41B699484EFD207' \
--data '{
    "value":"123",
    "iv":"1abc7969-6c7d-4395-9aa3-b1c7d169aa37:e4aa2cf0-efe3-420f-a28a-20f3d409c27b"
}'
```

Response:

```
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 24
Date: Tue, 28 Nov 2023 21:14:33 GMT

bV89WA/RFZr3LDhO6DyKxw==
```

### Decrypting with user session and id

Request:

```
curl --location 'localhost:8080/api/cvs/decrypt' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKT0REczN0YXVwVGQ4NGh3aWh6VHZVZkpJTm1vcEoyVmxGcnBuQmNrdExBIn0.eyJleHAiOjE3NTg4MDAyMTIsImlhdCI6MTc1ODc5OTkxMiwianRpIjoiMGFjMWM0NmMtOGQ3ZS00NTMyLTkyYmUtYTZhMTZjMTY4ODAxIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrLXdlYjo4MDkwL3JlYWxtcy9vcGVubGVhcC1yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI5ZmYyYmM5ZC01ZDgwLTQzNWUtYTc1Ni1lYjNlYTUxMGQ2ZjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjdnMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1vcGVubGVhcC1yZWFsbSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcmVwb3J0LXNlcnZpY2UgcHJvZmlsZSBkbV9yZWFkIGN2cy5yZWFkIiwiY2xpZW50SG9zdCI6IjE5Mi4xNjguNjUuMSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtb3BlbmxlYXAtcmVhbG0iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWN2cyIsImNsaWVudEFkZHJlc3MiOiIxOTIuMTY4LjY1LjEiLCJjbGllbnRfaWQiOiJjdnMifQ.gSWdIsmJXJgQTRzRqP1UarUUK1YhEGrI7hODyLnjMaBCgtr2wkcb5vFL1Bgnrpo3rGkWu9f51M8g0VYS2AJ8-plZ6MwhcQdfzlMobi2XwqRHZYGFdunR93--hilxqYmc7iUVAdPMGVVlKvIWYm0CGRxVRPytEkpAT5ReFz5_9g1OD5IH3OmPuj85R_LCk3Tq8rr3COrs1WEp4a-H1aK8xXmjI45qI3eYKJcSjVnQYdG5pwwGdrLVrDyM4xdrhmm5s3Z6eOu7d8HdX-ezOLKEf7CiHlPRvw8jZ2wK-xIqsbv4OIb1S067Gv14togOoacvVWV-_reWl6HylFYy518dgw' \
--header 'Cookie: JSESSIONID=9DBCAEFACC456FADD41B699484EFD207' \
--data '{
    "value":"bV89WA/RFZr3LDhO6DyKxw==",
    "iv":"1abc7969-6c7d-4395-9aa3-b1c7d169aa37:e4aa2cf0-efe3-420f-a28a-20f3d409c27b"
}'
```

Response:

```
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 7
Date: Tue, 28 Nov 2023 21:16:51 GMT

123
```

### Encrypting multiple values

Request:

```
curl --location 'localhost:8080/api/cvs/encryptList' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKT0REczN0YXVwVGQ4NGh3aWh6VHZVZkpJTm1vcEoyVmxGcnBuQmNrdExBIn0.eyJleHAiOjE3NTg4MDAyMTIsImlhdCI6MTc1ODc5OTkxMiwianRpIjoiMGFjMWM0NmMtOGQ3ZS00NTMyLTkyYmUtYTZhMTZjMTY4ODAxIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrLXdlYjo4MDkwL3JlYWxtcy9vcGVubGVhcC1yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI5ZmYyYmM5ZC01ZDgwLTQzNWUtYTc1Ni1lYjNlYTUxMGQ2ZjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjdnMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1vcGVubGVhcC1yZWFsbSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcmVwb3J0LXNlcnZpY2UgcHJvZmlsZSBkbV9yZWFkIGN2cy5yZWFkIiwiY2xpZW50SG9zdCI6IjE5Mi4xNjguNjUuMSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtb3BlbmxlYXAtcmVhbG0iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWN2cyIsImNsaWVudEFkZHJlc3MiOiIxOTIuMTY4LjY1LjEiLCJjbGllbnRfaWQiOiJjdnMifQ.gSWdIsmJXJgQTRzRqP1UarUUK1YhEGrI7hODyLnjMaBCgtr2wkcb5vFL1Bgnrpo3rGkWu9f51M8g0VYS2AJ8-plZ6MwhcQdfzlMobi2XwqRHZYGFdunR93--hilxqYmc7iUVAdPMGVVlKvIWYm0CGRxVRPytEkpAT5ReFz5_9g1OD5IH3OmPuj85R_LCk3Tq8rr3COrs1WEp4a-H1aK8xXmjI45qI3eYKJcSjVnQYdG5pwwGdrLVrDyM4xdrhmm5s3Z6eOu7d8HdX-ezOLKEf7CiHlPRvw8jZ2wK-xIqsbv4OIb1S067Gv14togOoacvVWV-_reWl6HylFYy518dgw' \
--header 'Cookie: JSESSIONID=9DBCAEFACC456FADD41B699484EFD207' \
--data '{
    "data": {
        "doc1": "123",
        "doc2": "456"
    },
    "iv": "1abc7969-6c7d-4395-9aa3-b1c7d169aa37:e4aa2cf0-efe3-420f-a28a-20f3d409c27b"
}'
```

Response:

```
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 24
Date: Tue, 28 Nov 2023 21:14:33 GMT

{
    "doc2": "C0s/iPT0HVvfn4CF8jf+bA==",
    "doc1": "bV89WA/RFZr3LDhO6DyKxw=="
}
```

### Decrypting multiple values

Request:

```
curl --location 'localhost:8080/api/cvs/decryptList' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKT0REczN0YXVwVGQ4NGh3aWh6VHZVZkpJTm1vcEoyVmxGcnBuQmNrdExBIn0.eyJleHAiOjE3NTg4MDAyMTIsImlhdCI6MTc1ODc5OTkxMiwianRpIjoiMGFjMWM0NmMtOGQ3ZS00NTMyLTkyYmUtYTZhMTZjMTY4ODAxIiwiaXNzIjoiaHR0cDovL2tleWNsb2FrLXdlYjo4MDkwL3JlYWxtcy9vcGVubGVhcC1yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI5ZmYyYmM5ZC01ZDgwLTQzNWUtYTc1Ni1lYjNlYTUxMGQ2ZjYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjdnMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIi8qIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1vcGVubGVhcC1yZWFsbSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcmVwb3J0LXNlcnZpY2UgcHJvZmlsZSBkbV9yZWFkIGN2cy5yZWFkIiwiY2xpZW50SG9zdCI6IjE5Mi4xNjguNjUuMSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtb3BlbmxlYXAtcmVhbG0iXSwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWN2cyIsImNsaWVudEFkZHJlc3MiOiIxOTIuMTY4LjY1LjEiLCJjbGllbnRfaWQiOiJjdnMifQ.gSWdIsmJXJgQTRzRqP1UarUUK1YhEGrI7hODyLnjMaBCgtr2wkcb5vFL1Bgnrpo3rGkWu9f51M8g0VYS2AJ8-plZ6MwhcQdfzlMobi2XwqRHZYGFdunR93--hilxqYmc7iUVAdPMGVVlKvIWYm0CGRxVRPytEkpAT5ReFz5_9g1OD5IH3OmPuj85R_LCk3Tq8rr3COrs1WEp4a-H1aK8xXmjI45qI3eYKJcSjVnQYdG5pwwGdrLVrDyM4xdrhmm5s3Z6eOu7d8HdX-ezOLKEf7CiHlPRvw8jZ2wK-xIqsbv4OIb1S067Gv14togOoacvVWV-_reWl6HylFYy518dgw' \
--header 'Cookie: JSESSIONID=9DBCAEFACC456FADD41B699484EFD207' \
--data '{
    "data": {
        "doc2": "C0s/iPT0HVvfn4CF8jf+bA==",
        "doc1": "bV89WA/RFZr3LDhO6DyKxw=="
    },
    "iv": "1abc7969-6c7d-4395-9aa3-b1c7d169aa37:e4aa2cf0-efe3-420f-a28a-20f3d409c27b"
}'
```

Response:

```
HTTP/1.1 200
Content-Type: text/plain;charset=UTF-8
Content-Length: 7
Date: Tue, 28 Nov 2023 21:16:51 GMT

{
    "doc2": "456",
    "doc1": "123"
}
```
