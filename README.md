## console-encrypter
**About:** Console utility that allows you to encrypt/decrypt file using symmetrical algorithm AES. \
**Help:**  
```
usage: console-encrypter
-g,--generate       Generate new AES key
-i,--input <arg>    Input file path
-m,--mode <arg>     Options are: enc/dec. Encryption/decryption mode switch
-o,--output <arg>   Output file path 
```
**Example usage:**
```
mvn clean package
cd target
java -jar console-encrypter-1.0-SNAPSHOT-jar-with-dependencies.jar -m enc -g -i ./classes/sample.jpg -o cipher
java -jar console-encrypter-1.0-SNAPSHOT-jar-with-dependencies.jar -m dec -i ./cipher -o ./classes/sample1.jpg
```