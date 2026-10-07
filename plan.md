twar# Upgrade Plan: college-administration-system (20260913054252)

- **Generated**: 2026-09-13 05:42:52
- **HEAD Branch**: N/A (not a Git repository)
- **HEAD Commit ID**: N/A (not a Git repository)

## Available Tools

**JDKs**
- JDK 17.0.19: C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot\bin (current project JDK, baseline)
- JDK 25.0.4.1: C:\Users\Admin\AppData\Roaming\Code\User\globalStorage\pleiades.java-extension-pack-jdk\java\25\bin (target)

**Build Tools**
- Maven 3.9.16: C:\apache-maven-3.9.16\bin

> Version control is unavailable because the workspace is not a Git repository; changes will remain in the working directory.

## Guidelines

- Upgrade the Java runtime to the latest LTS version requested by the user.
- Run in auto-execution mode.

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: N/A (version control unavailable)
- Run tests before and after the upgrade: true

## Upgrade Goals

- Java 25

## Technology Stack

| Technology/Dependency | Current | Min Compatible Version | Why Incompatible |
| --------------------- | ------- | ---------------------- | ---------------- |
| Java | 17 | 25 | User requested Java 25 LTS |
| Spring Boot | 3.2.4 | 3.2.4 | No framework upgrade requested; existing code uses Spring Boot 3 APIs |
| JavaFX | 17.0.6 | 17.0.6 | Existing JavaFX line remains compatible with the Java source level change |
| Maven | 3.9.16 | 3.9.0 | Current Maven is compatible with Java 25 |
| javafx-maven-plugin | 0.0.8 | 0.0.8 | Existing plugin remains sufficient for the targeted source level |

## Derived Upgrades

- Set Maven's `java.version` property to `25`; Spring Boot's compiler configuration will use this value.
- Retain JavaFX 17.0.6 and Spring Boot 3.2.4 because the requested change is limited to the Java runtime and no source-level incompatibility was found in the project.
- Use the installed Maven 3.9.16 and JDK 25.0.4.1; no environment installation is required.

## Impact Analysis

### Dependency Changes

| File | Dependency | Current | Action | Target | Reason |
|------|------------|---------|--------|--------|--------|
| pom.xml | `java.version` property | 17 | upgrade | 25 | Select Java 25 for Maven compiler configuration |

### Source Code Changes

| File | Location | Current | Required Change | Reason |
|------|----------|---------|-----------------|--------|
| None | N/A | No Java 25-incompatible APIs found during targeted scan | No source changes | The project source contains ordinary Spring/JavaFX classes and has no internal JDK imports or removed Java APIs |

### Configuration Changes

| File | Property/Setting | Current | Required Change | Reason |
|------|------------------|---------|-----------------|--------|
| pom.xml | `java.version` | 17 | Change to 25 | Maven compiler and Spring Boot build configuration must target Java 25 |

### CI/CD Changes

| File | Location | Current | Required Change |
|------|----------|---------|-----------------|
| None | N/A | No CI/CD files detected | No change |

### Risks & Warnings

- **JavaFX runtime compatibility**: JavaFX artifacts remain pinned to 17.0.6 while the runtime moves to Java 25. **Mitigation**: Run clean compilation and the complete Maven test suite on JDK 25; retain the existing JavaFX line unless execution exposes a concrete runtime issue.
- **Spring Boot support matrix**: Spring Boot 3.2.4 predates Java 25. **Mitigation**: Verify compilation and tests on JDK 25; do not broaden the upgrade to Spring Boot without a demonstrated compatibility failure because that would change framework behavior beyond the request.
- **Version control unavailable**: The workspace cannot create a branch or commit. **Mitigation**: Keep a detailed progress and summary record under `.github/modernize/java-upgrade/20260913054252/`.

## Upgrade Steps

- Step 1: Setup Environment
  - **Rationale**: Confirm the installed target JDK and Maven toolchain before modifying project files.
  - **Changes to Make**: No project changes; use JDK 25.0.4.1 and Maven 3.9.16.
  - **Verification**: List JDKs and Maven; expected target JDK and Maven are available.

- Step 2: Setup Baseline
  - **Rationale**: Capture pre-upgrade compilation and test status using the current Java 17 configuration.
  - **Changes to Make**: No project changes.
  - **Verification**: `mvn clean compile test-compile -q && mvn clean test -q` using JDK 17; expected baseline is recorded.

- Step 3: Upgrade Java Runtime Target
  - **Rationale**: Apply the only required project change, updating Maven's Java target from 17 to 25.
  - **Changes to Make**: Apply the Dependency and Configuration Changes above in `pom.xml`.
  - **Verification**: `mvn clean test-compile -q` using JDK 25; main and test code must compile.

- Step 4: CVE Validation
  - **Rationale**: Check direct dependency versions after the upgrade and remediate any reported vulnerabilities without unrelated dependency changes.
  - **Changes to Make**: Upgrade only dependencies reported with actionable CVEs.
  - **Verification**: Scan direct dependencies, compile, and rescan; expected no actionable CVEs remain or each limitation is documented.

- Step 5: Final Validation
  - **Rationale**: Confirm the target runtime, clean build, and full test suite meet the upgrade success criteria.
  - **Changes to Make**: Fix any Java 25-specific compilation or test failures discovered during validation.
  - **Verification**: `mvn clean test-compile -q`, `mvn clean test -q`, and `mvn clean verify -Djacoco.skip=false` using JDK 25; expected 100% tests pass.
