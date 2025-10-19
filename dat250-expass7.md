## What was accomplished
- Implemented a multi\-stage build:
  - A builder stage (Gradle with matching JDK) to produce the `bootJar`.
  - A minimal runtime stage that only contains the runnable artifact and a JRE.
- Ensured the app runs as a non\-root user in the final image:
  - Created an unprivileged system user and group.
  - Copied the jar into a dedicated `/app` directory and set ownership.
  - Switched to that user with `USER` before running the JVM.
- Optimized layer caching:
  - Copied build files first (`gradlew`, `gradle/`, `build.gradle*`, `settings.gradle*`) to fetch dependencies before adding sources.
  - Used Gradle `--no-daemon` and avoided running tests in the container build.
- Aligned Java versions:
  - Built with JDK 21 and used a JRE 21 runtime to avoid `UnsupportedClassVersionError`.

## Build and run commands that were used
- Build the image:
  - `docker buildx build -t demo:latest -f Dockerfile .`
- Run locally mapping port 8080:
  - `docker run --rm -p 8080:8080 demo:latest`

## Notes on issues that were encountered
- BuildKit I/O error (`metadata_v2.db: input/output error`):
  - Often resolved by restarting Docker Desktop or clearing build cache.
- `UnsupportedClassVersionError`:
  - Caused by mismatched JDK/JRE versions; fixed by using the same major Java version for both build and runtime.

## Security & size considerations
- Final image contains only the JAR and a slim JRE (no build tools or sources).
- Running as non\-root reduces attack surface.
- Recommended additional steps:
  - Scan final images with Trivy or similar.
  - Consider distroless runtime images for even smaller attack surface.
  - Limit Linux capabilities and apply runtime policies (seccomp/AppArmor) in deployment.

## Minimal checklist
- [x] Multi\-stage Dockerfile implemented
- [x] Final image contains only runnable artifact and JRE
- [x] App runs as a non\-root user
- [x] Port exposed and ENTRYPOINT configured
- [x] Verified Java version compatibility
