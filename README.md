# Transparensbee
An Android-based Certificate Transparency pollinator

Transparensbee is an Android app that lets your phone play a (small) part in
the Certificate Transparency ecosystem. Every hour, it requests a signature
from several Certificate Transparency logs, and then circulates the signatures
to auditor servers. Auditor servers use information from this and other clients
to determine if a log server is working properly, or if it is providing
inconsistent answers.

This app was inspired by
[a conversation on Twitter](https://twitter.com/divergentdave/status/846924002086064128).

### Screenshot
![Screenshot of app](/screenshot.png)

### Installation
No release has been issued yet, so for now you have to install the app from source.

### Development
Transparensbee is primarily developed in Android Studio. To build it outside of
an IDE environment, use Gradle, as follows:

```bash
./gradlew assembleDebug
```

or, on Windows:

```batch
gradlew.bat assembleDebug
```
