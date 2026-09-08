# FMGP Generative Design

## Overview
This is a Scala-based generative design project with a split architecture:
- **Core**: Cross-platform (JVM/JS) geometry models and logic
- **Webapp**: Laminar/Scala.js frontend (Vite)
- **Controller**: Akka/gRPC backend services
- **Syntax**: DSL for geometric operations

## Development Environment

### Prerequisites
- JDK 11+
- sbt 1.x
- Node.js 16+ (for frontend)
- Docker (optional, for Envoy/gRPC-web)

### Key Commands

| Task | Command | Description |
|------|---------|-------------|
| **Build & Test** | `sbt testAll` | Run tests for both JVM and JS modules |
| **Test JVM** | `sbt testJVM` | Run only JVM tests |
| **Test JS** | `sbt testJS` | Run only JS tests |
| **Run Webapp** | `sbt webapp/fastLinkJS && npm run dev` | Link JS code and start Vite server |
| **Start REPL** | `sbt repl/console` | Start interactive REPL (load scripts with `:load script.sc`) |
| **Format Code** | `sbt scalafmt` | Format Scala code |
| **Generate Proto** | `sbt protosJVM/compile` | Generate Scala code from Protobuf definitions |

### Project Structure

| Directory | Module | Description |
|-----------|--------|-------------|
| `modules/01-model` | `model` | Core data models (Cross JS/JVM), Circe encoders |
| `modules/01-protos` | `protos` | Protobuf definitions & generated code (gRPC) |
| `modules/02-core` | `geometryCoreJS` | Geometry logic, Three.js bindings, WebGL helpers |
| `modules/02-syntax` | `syntax` | DSL for creating geometries (Cross JS/JVM) |
| `modules/02-controller` | `controller` | Backend services (Akka HTTP, gRPC) |
| `modules/03-prebuilt` | `prebuilt` | Example geometries and algorithms |
| `modules/04-webapp` | `webapp` | Main frontend application (Laminar, Vite) |
| `modules/04-repl` | `repl` | Console/Scripting environment |

## Code Patterns & Conventions

### Scala
- **Version**: Scala 3.3.7
- **Style**: Functional programming with ZIO and Akka.
- **JSON**: Circe for JSON serialization/deserialization.
- **Frontend**: Laminar for UI, Scala.js for compilation to JS.
- **Interop**: Uses `ScalablyTyped` for TypeScript definition imports (Three.js, etc.).

### Three.js Integration
- The project wraps Three.js for 3D rendering.
- `modules/02-core` contains the bindings and utilities.
- Types are managed via `ScalablyTyped` (configured in `build.sbt`).

### gRPC & Protobuf
- `.proto` files are in `modules/01-protos/src/main/protobuf`.
- `ScalaPB` is used for code generation.
- The web client uses `grpc-web` to communicate with the backend.

### Testing
- **Framework**: `munit` is the primary testing library.
- **Location**: Tests are in `src/test/scala` within each module.
- **Running**: Use `sbt test` in a specific project or `sbt testAll` for everything.

## Gotchas
- **Multiple Platforms**: Logic is often shared between JS and JVM. Be careful with platform-specific dependencies.
- **Vite & Scala.js**: The frontend build pipeline is `sbt fastLinkJS` -> `Vite`. You must run the sbt link command before Vite will see changes.
- **Imports**: Pay attention to imports, especially with the modular structure.
- **Envoy**: gRPC-web requires an Envoy proxy to translate HTTP/1.1 to gRPC (config in `envoy.yaml`).
