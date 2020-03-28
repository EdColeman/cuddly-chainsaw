# creating ssl certificates and truststore

Generate a certificate and private key [see stackoverflow question: How to create a self-signed certificate with OpenSSL](https://stackoverflow.com/questions/10175812/how-to-create-a-self-signed-certificate-with-openssl/41366949#41366949)

```openssl req -x509 -newkey rsa:4096 -sha256 -days 3650 -nodes   -keyout example.key -out example.crt -subj "/CN=example.com"   -addext "subjectAltName=DNS:example.com,DNS:example.net,IP:127.0.0.1" ```

add the cert to a truststore
  
```keytool -keystore keystore -import -alias jetty -file example.crt -trustcacerts```

keytool -keystore keystore -alias jetty -genkey -keyalg RSA
Enter keystore password:  
Re-enter new password: 
What is your first and last name?
  [Unknown]:  localhost
What is the name of your organizational unit?
  [Unknown]:  
What is the name of your organization?
  [Unknown]:  
What is the name of your City or Locality?
  [Unknown]:  
What is the name of your State or Province?
  [Unknown]:  
What is the two-letter country code for this unit?
  [Unknown]:  
Is CN=localhost, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown correct?
  [no]:  yes

Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 90 days
	for: CN=localhost, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown
