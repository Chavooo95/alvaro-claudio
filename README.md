# Tienda Online de Juegos de Mesa

E-commerce de juegos de mesa (Catán, Bomb, Trivial, Carcassonne…). Backend en **Java 21 + Spring Boot**, persistencia en **PostgreSQL**, arquitectura **DDD + Hexagonal**. Frontend en React + TypeScript + Vite (aún sin inicializar).

> Para entender el **porqué** del diseño (bounded contexts, lenguaje ubicuo, capas, convenciones) lee primero [`CLAUDE.md`](./CLAUDE.md). Este README cubre solo el **cómo** de levantar y operar el proyecto en local.

---

## Estado actual

- ✅ Backend multi-módulo Maven (`bootstrap` + `contexts/catalog`).
- ✅ Bounded context **Catalog** con CRUD del agregado `Game` (dominio + aplicación + JPA + REST).
- ✅ Migraciones Flyway, docker-compose con Postgres 16.
- ✅ Tests unitarios de dominio (JUnit 5 + AssertJ) — 8/8 verdes.
- 🚧 Otros contextos (Inventory, Cart, Ordering, Payments…): pendientes.
- 🚧 Frontend: pendiente.
- 🚧 Tests de integración (Testcontainers) y reglas ArchUnit: pendientes.

---

## Prerrequisitos

| Herramienta       | Versión   | Comprobación              |
|-------------------|-----------|----------------------------|
| **JDK**           | 21        | `javac -version`           |
| **Maven**         | 3.9.x     | `mvn -version`             |
| **Docker**        | 24+       | `docker --version`         |
| **Docker Compose**| v2        | `docker compose version`   |

> Si vas a desarrollar en **Windows**, recomiendo usar **WSL2 + Docker Desktop** (te ahorras todas las peculiaridades de rutas/shells). Si prefieres Windows "nativo", abajo tienes los comandos para PowerShell.

### Si no tienes JDK 21 y Maven

#### Linux (Ubuntu/Debian) con `sudo`

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk maven
```

#### macOS con [Homebrew](https://brew.sh)

```bash
brew install openjdk@21 maven
sudo ln -sfn $(brew --prefix)/opt/openjdk@21/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-21.jdk
```

#### Windows con [winget](https://learn.microsoft.com/windows/package-manager/winget/) (Admin no necesario)

```powershell
winget install --id EclipseAdoptium.Temurin.21.JDK
winget install --id Apache.Maven
```

Cierra y reabre la terminal después de instalar para que el `PATH` se refresque.

#### Linux/macOS sin `sudo` (instalación user-local — la que usa este repo)

```bash
mkdir -p ~/.local/share && cd ~/.local/share

# JDK 21 (Eclipse Temurin) — cambia 'linux' por 'mac' en macOS si haces falta
curl -fsSL "https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jdk/hotspot/normal/eclipse?project=jdk" -o jdk21.tar.gz
tar -xzf jdk21.tar.gz && rm jdk21.tar.gz

# Maven 3.9.9
curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz -o maven.tar.gz
tar -xzf maven.tar.gz && rm maven.tar.gz
```

#### Windows sin admin (PowerShell, instalación user-local)

```powershell
$base = "$env:USERPROFILE\.local\share"
New-Item -ItemType Directory -Force -Path $base | Out-Null
Set-Location $base

# JDK 21
Invoke-WebRequest "https://api.adoptium.net/v3/binary/latest/21/ga/windows/x64/jdk/hotspot/normal/eclipse?project=jdk" -OutFile jdk21.zip
Expand-Archive jdk21.zip -DestinationPath . ; Remove-Item jdk21.zip

# Maven 3.9.9
Invoke-WebRequest "https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip" -OutFile maven.zip
Expand-Archive maven.zip -DestinationPath . ; Remove-Item maven.zip
```

### Variables de entorno

Necesitas exponer `JAVA_HOME` y añadir `bin` de JDK y Maven al `PATH` **solo si** has usado la ruta "sin sudo / sin admin". Con apt/brew/winget ya quedan configurados.

#### Linux/macOS (bash/zsh)

```bash
export JAVA_HOME=$HOME/.local/share/jdk-21.0.11+10
export PATH=$JAVA_HOME/bin:$HOME/.local/share/apache-maven-3.9.9/bin:$PATH
```

Persiste añadiéndolo a `~/.zshrc` o `~/.bashrc`.

#### Windows PowerShell (sesión actual)

```powershell
$env:JAVA_HOME = "$env:USERPROFILE\.local\share\jdk-21.0.11+10"
$env:Path = "$env:JAVA_HOME\bin;$env:USERPROFILE\.local\share\apache-maven-3.9.9\bin;$env:Path"
```

Persiste (solo para tu usuario, no requiere admin):

```powershell
[Environment]::SetEnvironmentVariable("JAVA_HOME", "$env:USERPROFILE\.local\share\jdk-21.0.11+10", "User")
$newPath = "$env:USERPROFILE\.local\share\jdk-21.0.11+10\bin;$env:USERPROFILE\.local\share\apache-maven-3.9.9\bin;" + [Environment]::GetEnvironmentVariable("Path","User")
[Environment]::SetEnvironmentVariable("Path", $newPath, "User")
```

Reabre la terminal después.

#### Windows cmd.exe

```cmd
set JAVA_HOME=%USERPROFILE%\.local\share\jdk-21.0.11+10
set PATH=%JAVA_HOME%\bin;%USERPROFILE%\.local\share\apache-maven-3.9.9\bin;%PATH%
```

> Las versiones exactas (`jdk-21.0.11+10`, `apache-maven-3.9.9`) cambian con cada release: ajusta los nombres de carpeta a los que tengas descargados.

---

## Estructura del repositorio

```
ProyectoAlavaro/
├── CLAUDE.md                # Mapa de arquitectura y lenguaje ubicuo
├── README.md                # Este fichero
├── docker-compose.yml       # Postgres 16 para desarrollo
├── pom.xml                  # Parent POM (multi-módulo)
├── bootstrap/               # Punto de arranque Spring Boot
│   └── src/main/
│       ├── java/com/boardgames/bootstrap/BoardGamesApplication.java
│       └── resources/
│           ├── application.yml
│           └── db/migration/V1__create_games_table.sql
└── contexts/
    └── catalog/             # Bounded context Catalog
        └── src/
            ├── main/java/com/boardgames/catalog/
            │   ├── domain/          # Game, GameId, Money, PlayerCount, repos…
            │   ├── application/     # Use cases + commands
            │   └── infrastructure/  # JPA adapter + REST controller
            └── test/java/…/domain/  # Tests unitarios
```

---

## Puesta en marcha — paso a paso

### 1. Levantar PostgreSQL

```bash
docker compose up -d postgres
```

Verifica que está sano:

```bash
docker compose ps
docker compose logs postgres --tail=20
```

Conexión por defecto (definida en `docker-compose.yml` y `application.yml`):

| Parámetro | Valor             |
|-----------|--------------------|
| Host      | `localhost`        |
| Puerto    | `5432`             |
| BBDD      | `boardgames`       |
| Usuario   | `boardgames`       |
| Password  | `boardgames`       |

### 2. Compilar y ejecutar tests

Desde la raíz del repo:

```bash
mvn clean package
```

Esto:
1. Compila los dos módulos (`catalog`, `bootstrap`).
2. Ejecuta los tests unitarios de dominio (8 tests).
3. Empaqueta `bootstrap/target/bootstrap-0.1.0-SNAPSHOT.jar` (fat-jar ejecutable de Spring Boot).

Solo tests:

```bash
mvn test
```

Solo un módulo:

```bash
mvn -pl contexts/catalog test
```

### 3. Arrancar la aplicación

```bash
java -jar bootstrap/target/bootstrap-0.1.0-SNAPSHOT.jar
```

O en modo dev (con recarga, sin empaquetar):

```bash
mvn -pl bootstrap spring-boot:run
```

En el arranque, **Flyway aplicará `V1__create_games_table.sql`** automáticamente contra la BBDD.

La API queda en `http://localhost:8080`.

---

## API actual: CRUD de juegos

Base path: `/api/games`

| Método  | Ruta              | Descripción                | Códigos               |
|---------|-------------------|----------------------------|-----------------------|
| `POST`  | `/api/games`      | Crear un juego             | `201` / `400`         |
| `GET`   | `/api/games`      | Listar todos los juegos    | `200`                 |
| `GET`   | `/api/games/{id}` | Obtener uno por id (UUID)  | `200` / `404`         |
| `PUT`   | `/api/games/{id}` | Actualizar uno             | `200` / `400` / `404` |
| `DELETE`| `/api/games/{id}` | Eliminar uno               | `204` / `404`         |

### Ejemplos

> **Windows PowerShell**: `curl` está aliased a `Invoke-WebRequest`, que **no acepta** las mismas flags. Usa `curl.exe` (que viene en Windows 10/11) en lugar de `curl` para que los ejemplos de abajo funcionen tal cual.
> **Windows cmd.exe**: dentro de `cmd`, escribe el JSON en una sola línea o escapa las comillas con `\"`.

Crear (Linux/macOS, Git Bash, WSL):

```bash
curl -i -X POST http://localhost:8080/api/games \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Catán",
    "designer": "Klaus Teuber",
    "minPlayers": 3,
    "maxPlayers": 4,
    "minAge": 10,
    "durationMinutes": 90,
    "priceAmount": 39.95,
    "priceCurrency": "EUR"
  }'
```

Crear (PowerShell):

```powershell
curl.exe -i -X POST http://localhost:8080/api/games `
  -H 'Content-Type: application/json' `
  -d '{ \"title\": \"Catán\", \"designer\": \"Klaus Teuber\", \"minPlayers\": 3, \"maxPlayers\": 4, \"minAge\": 10, \"durationMinutes\": 90, \"priceAmount\": 39.95, \"priceCurrency\": \"EUR\" }'
```

Alternativa "nativa" en PowerShell con `Invoke-RestMethod` (sin escapado raro):

```powershell
$body = @{
  title = "Catán"
  designer = "Klaus Teuber"
  minPlayers = 3
  maxPlayers = 4
  minAge = 10
  durationMinutes = 90
  priceAmount = 39.95
  priceCurrency = "EUR"
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/games `
  -ContentType 'application/json' -Body $body
```

Listar:

```bash
curl http://localhost:8080/api/games
```

```powershell
Invoke-RestMethod http://localhost:8080/api/games
```

Obtener uno:

```bash
curl http://localhost:8080/api/games/<UUID>
```

Actualizar (Linux/macOS):

```bash
curl -X PUT http://localhost:8080/api/games/<UUID> \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Catán - Edición 25 Aniversario",
    "designer": "Klaus Teuber",
    "minPlayers": 3,
    "maxPlayers": 4,
    "minAge": 10,
    "durationMinutes": 90,
    "priceAmount": 59.95,
    "priceCurrency": "EUR"
  }'
```

Actualizar (PowerShell):

```powershell
Invoke-RestMethod -Method Put -Uri http://localhost:8080/api/games/<UUID> `
  -ContentType 'application/json' -Body (@{
    title = "Catán - Edición 25 Aniversario"
    designer = "Klaus Teuber"
    minPlayers = 3; maxPlayers = 4
    minAge = 10; durationMinutes = 90
    priceAmount = 59.95; priceCurrency = "EUR"
  } | ConvertTo-Json)
```

Borrar:

```bash
curl -X DELETE http://localhost:8080/api/games/<UUID>
```

```powershell
Invoke-RestMethod -Method Delete -Uri http://localhost:8080/api/games/<UUID>
```

### Errores

Respuesta tipo *problem* para `400` y `404`:

```json
{
  "timestamp": "2026-06-05T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": ["title: must not be blank"]
}
```

---

## Comandos útiles

```bash
# Parar Postgres (conserva el volumen)
docker compose stop postgres

# Borrar todo el estado (¡destruye datos!)
docker compose down -v

# Conectarse al Postgres del contenedor
docker compose exec postgres psql -U boardgames -d boardgames

# Limpiar build artifacts
mvn clean

# Saltar tests al empaquetar (úsalo solo si sabes lo que haces)
mvn package -DskipTests
```

---

## Convenciones rápidas

- **Dominio sin Spring/JPA**: cualquier import de `org.springframework.*` o `jakarta.persistence.*` en `com.boardgames.catalog.domain` es deuda y debe eliminarse.
- **IDs tipados**: nunca `UUID` desnudo cruzando capas — usa `GameId`.
- **Use cases con un único método público** (`execute`). Si hace falta un segundo, probablemente es otro use case.
- **DTOs ≠ entidades JPA ≠ agregados de dominio.** Tres clases distintas, mappers explícitos.

Detalles ampliados en [`CLAUDE.md`](./CLAUDE.md).

---

## Roadmap inmediato

1. Reglas **ArchUnit** que bloqueen violaciones de capa y dependencias entre contextos.
2. Tests de integración con **Testcontainers** (Postgres) para el adapter JPA y el controller.
3. **Paginación** y filtros en `GET /api/games`.
4. **OpenAPI** (springdoc) en `/swagger-ui.html`.
5. Siguiente bounded context: **Cart** o **Inventory** (orden según el flujo de checkout).
6. Frontend: inicializar Vite + React + TS y consumir el API.
