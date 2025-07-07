# Publishing ChartIQ Android SDK

## Quick Start

1. **Set up Central Portal account** at https://central.sonatype.com/
   - Go to `View Account -> Generate User Token` it will give you mavenCentralUsername and mavenCentralPassword
   - Verify Namespace

2. **GPG Keys**
   ```bash
   gpg --gen-key
   gpg --list-secret-keys --keyid-format SHORT
   gpg --export-secret-keys your_email@example.com > secring.gpg
   ```

3. **Configure credentials** in `gradle.properties`:
   ```properties
   mavenCentralUsername=your_username
   mavenCentralPassword=your_password
   signing.keyId=your_gpg_key_id
   signing.password=your_gpg_password
   signing.secretKeyRingFile=secring.gpg
   ```

4. **Test locally**: `./gradlew publishToMavenLocal`
5. **Dry run Publish**: `../gradlew publishToMavenCentral --dry-run`
6. **Publish**: `./gradlew publishToMavenCentral`

## References

- [Central Portal Guide](https://central.sonatype.org/publish/publish-portal-guide/)
- [Gradle Cookbook](https://cookbook.gradle.org/integrations/maven-central/publishing/) 