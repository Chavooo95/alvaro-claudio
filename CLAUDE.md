# CLAUDE.md — Tienda Online de Juegos de Mesa

> Documento de contexto para futuras sesiones. Escrito desde la perspectiva de un desarrollador full-stack senior que diseña el sistema aplicando **Domain-Driven Design** y **arquitectura hexagonal (Ports & Adapters)**. Lee este fichero antes de tocar código: define el "porqué" del proyecto, su lenguaje ubicuo y los límites entre contextos.

---

## 1. Visión del producto

E-commerce especializado en **juegos de mesa** (Catán, Bomb, Trivial, Carcassonne, Wingspan, etc.). El sistema cubre el ciclo completo de venta: catálogo navegable, carrito, checkout, pagos, gestión de stock, envíos y postventa.

**Diferenciadores del dominio** (no son un e-commerce genérico):

- Cada juego tiene atributos propios del dominio: nº de jugadores (min/max), edad recomendada, duración estimada de partida, complejidad (peso BGG), idioma, expansiones compatibles, dependencia de idioma del material.
- Existen **expansiones** que dependen de un juego base: no se pueden vender de forma aislada sin advertencia, y el stock está relacionado.
- Promociones tipo "bundle" (juego base + expansión) habituales en el sector.
- Stock por edición/idioma del mismo título (ej. *Catán edición 25 aniversario ES* vs *EN*).

Estos matices son los que justifican modelar el dominio con DDD en lugar de un CRUD plano.

---

## 2. Stack técnico

| Capa            | Tecnología                                |
|-----------------|--------------------------------------------|
| Backend         | **Java 21 + Spring Boot 3.x**              |
| Frontend        | **React 18 + TypeScript + Vite**           |
| Persistencia    | **PostgreSQL 16**                          |
| Build backend   | Maven o Gradle (a confirmar)               |
| Build frontend  | Vite + pnpm/npm                            |
| Tests backend   | JUnit 5 + AssertJ + Testcontainers + ArchUnit |
| Tests frontend  | Vitest + React Testing Library + Playwright (E2E) |
| Migraciones     | Flyway                                     |
| Contenedores    | Docker + docker-compose para desarrollo    |

Decisiones cerradas. Cualquier desviación debe justificarse explícitamente.

---

## 3. Arquitectura: DDD + Hexagonal

### 3.1 Principios rectores

1. **El dominio no conoce a nadie.** No depende de Spring, JPA, Jackson, HTTP ni de la base de datos. Java puro.
2. **Dependencias hacia dentro.** Infrastructure → Application → Domain. Nunca al revés.
3. **Puertos en el dominio/aplicación, adaptadores en infraestructura.** Los puertos son interfaces; los adaptadores las implementan.
4. **Casos de uso explícitos.** Cada acción del usuario o del sistema es un *use case* (clase con un único método público). Nada de "services" gordos.
5. **Modelo rico, no anémico.** Las entidades y agregados encapsulan invariantes; no son DTOs con getters/setters.
6. **Lenguaje ubicuo.** El código habla el idioma del negocio (`Game`, `Cart`, `Order`, `placeOrder`, no `ItemEntity.save()`).

### 3.2 Capas

```
┌─────────────────────────────────────────────────┐
│  infrastructure (adapters)                      │
│  · REST controllers, JPA repos, Stripe client,  │
│    config Spring, mappers, listeners de cola    │
└──────────────────┬──────────────────────────────┘
                   │ implementa puertos
┌──────────────────▼──────────────────────────────┐
│  application                                    │
│  · Use cases (PlaceOrder, AddItemToCart, …)     │
│  · Puertos (in: UseCases / out: Repositories,   │
│    PaymentGateway, EventPublisher)              │
│  · Application services orquestadores           │
└──────────────────┬──────────────────────────────┘
                   │ usa
┌──────────────────▼──────────────────────────────┐
│  domain                                         │
│  · Aggregates, Entities, Value Objects          │
│  · Domain events, Domain services               │
│  · Reglas de negocio e invariantes              │
│  · CERO dependencias externas                   │
└─────────────────────────────────────────────────┘
```

### 3.3 Bounded Contexts

Identificados por análisis del dominio. Cada contexto es un módulo (o submódulo Maven/Gradle) independiente con su propio modelo. **No compartir entidades entre contextos**; comunicarse vía eventos de dominio o IDs.

| Bounded Context   | Responsabilidad principal                                              | Agregado raíz       |
|-------------------|------------------------------------------------------------------------|---------------------|
| **Catalog**       | Juegos, expansiones, categorías, búsqueda, atributos del juego         | `Game`              |
| **Inventory**     | Stock por SKU/edición, reservas durante checkout, reposición            | `StockItem`         |
| **Pricing**       | Precios, descuentos, bundles, promociones                              | `PriceList` / `Promotion` |
| **Cart**          | Carrito del usuario (sesión o autenticado), líneas, totales provisionales | `Cart`              |
| **Ordering**      | Checkout, creación de pedido, estado del pedido, facturación            | `Order`             |
| **Payments**      | Integración con pasarela (Stripe/Redsys), capturas, reembolsos          | `Payment`           |
| **Shipping**      | Tarifas, transportistas, tracking, direcciones                          | `Shipment`          |
| **Customers**     | Cuenta de usuario, perfil, direcciones, historial                       | `Customer`          |
| **IAM**           | Autenticación, autorización, sesiones, JWT                              | `UserAccount`       |

**Contextos que se comunican mucho** (Ordering ↔ Inventory ↔ Payments) son los más sensibles: pensar bien si la integración es síncrona (puerto + adaptador) o asíncrona (evento de dominio + listener).

### 3.4 Estructura de carpetas (backend)

Una propuesta. Un módulo por bounded context, y dentro de cada uno la tríada `domain / application / infrastructure`.

```
backend/
├── pom.xml (o build.gradle.kts)
├── bootstrap/                       ← arranca Spring, wiring final
│   └── src/main/java/com/boardgames/bootstrap/
├── shared-kernel/                   ← VOs comunes (Money, EmailAddress…)
│   └── src/main/java/com/boardgames/shared/
└── contexts/
    ├── catalog/
    │   ├── domain/                  ← Game, GameId, Designer, PlayerCount…
    │   ├── application/             ← SearchGames, GetGameDetail, puertos
    │   └── infrastructure/          ← JPA, REST, Elasticsearch adapter
    ├── inventory/
    │   ├── domain/
    │   ├── application/
    │   └── infrastructure/
    ├── ordering/
    │   ├── domain/                  ← Order, OrderLine, OrderStatus…
    │   ├── application/             ← PlaceOrder, CancelOrder…
    │   └── infrastructure/
    ├── payments/
    ├── cart/
    ├── customers/
    ├── pricing/
    ├── shipping/
    └── iam/
```

Reglas duras a verificar con **ArchUnit**:

- `domain` no importa de `application` ni `infrastructure`.
- `application` no importa de `infrastructure`.
- Ningún contexto importa el `domain` de otro contexto (sólo eventos publicados o IDs).
- Nada de `@Component`, `@Service`, `@Entity`, `@Autowired` en `domain`.

### 3.5 Estructura de carpetas (frontend)

Frontend organizado también por **feature/bounded context**, no por tipo técnico.

```
frontend/
├── src/
│   ├── app/                 ← router, providers, layout
│   ├── shared/              ← UI kit, hooks, utils, i18n
│   ├── features/
│   │   ├── catalog/         ← listado, ficha de juego, filtros
│   │   ├── cart/
│   │   ├── checkout/
│   │   ├── account/
│   │   └── auth/
│   └── api/                 ← cliente HTTP tipado (zod/openapi)
└── vite.config.ts
```

Hooks y servicios de un feature no se importan desde otro feature: si hace falta compartir, sube a `shared/`.

---

## 4. Lenguaje ubicuo (glosario)

Términos que **deben** aparecer tal cual en el código y la UI. Si encuentras sinónimos en código (`product` en lugar de `game`, `client` en lugar de `customer`), es deuda — renombrar.

- **Game** — Juego de mesa. Tiene una o varias *editions*.
- **Edition** — Edición concreta de un juego (idioma, año, formato). Es lo que se vende.
- **Expansion** — Juego que requiere un *base game* para jugarse.
- **SKU** — Identificador único de una edición vendible.
- **StockItem** — Disponibilidad física de un SKU.
- **Reservation** — Reserva temporal de stock durante el checkout.
- **Cart** — Carrito antes de convertirse en pedido. Es efímero.
- **Order** — Pedido confirmado. Es inmutable salvo por transiciones de estado.
- **OrderLine** — Línea de un pedido (SKU + cantidad + precio congelado).
- **Payment** — Intento/registro de cobro contra una `Order`.
- **Shipment** — Envío físico asociado a una `Order`.
- **Customer** — Cliente registrado. ≠ `UserAccount` (credenciales).
- **Bundle** — Combinación promocional de varios SKUs a precio conjunto.

---

## 5. Convenciones de código

### Backend

- Java 21: aprovechar `record`, `sealed`, pattern matching, virtual threads donde aplique.
- Value Objects como `record` con validación en el *compact constructor*.
- IDs tipados (`GameId`, `OrderId`), nunca `UUID`/`Long` desnudos cruzando capas.
- Excepciones de dominio extienden de una base por contexto (`OrderingDomainException`); las de infraestructura nunca suben sin traducirse.
- Use cases reciben un *command* (record) y devuelven un *result* (record). Nada de pasar 7 parámetros sueltos.
- Mappers explícitos entre `domain` ↔ `entity JPA` ↔ `DTO REST`. No reutilizar la misma clase para las tres cosas.
- Transacciones se abren en la capa de aplicación (use case), no en repositorios.

### Frontend

- TypeScript estricto (`strict: true`, `noUncheckedIndexedAccess: true`).
- Tipos del API generados desde OpenAPI/Swagger del backend — no escribir tipos a mano que dupliquen contratos.
- Componentes: server state con TanStack Query; client state local; **no** Redux salvo necesidad real.
- Formularios con React Hook Form + Zod.
- Accesibilidad (a11y) no es opcional: roles ARIA, foco, contraste.

---

## 6. Estrategia de testing

| Nivel              | Backend                                    | Frontend                  |
|--------------------|--------------------------------------------|---------------------------|
| Unit (dominio)     | JUnit puro, sin Spring, sin mocks de libs  | Vitest                    |
| Unit (aplicación)  | JUnit + mocks de puertos (Mockito)         | Vitest + MSW              |
| Integración        | `@SpringBootTest` + Testcontainers (PG)    | RTL + MSW                 |
| Arquitectura       | ArchUnit (reglas de la sección 3.4)        | ESLint rules              |
| Contrato           | Spring Cloud Contract o Pact               | OpenAPI typecheck         |
| E2E                | —                                          | Playwright                |

**Regla:** un cambio en `domain` que no rompe ningún test es sospechoso. El dominio es donde más cobertura se exige.

---

## 7. Estado actual del proyecto

A día de **2026-06-05**, el repositorio está vacío salvo este `CLAUDE.md` y un fichero `main` placeholder. **Nada está implementado todavía.** Cuando se empiece a programar:

1. Inicializar el proyecto Java (Maven/Gradle) con el layout multi-módulo de la sección 3.4.
2. Inicializar el frontend con Vite + React + TS.
3. Levantar `docker-compose.yml` con Postgres.
4. Empezar por el contexto **Catalog** (lectura) — es el menos acoplado y desbloquea el resto.
5. Después **Cart → Inventory (reservas) → Ordering → Payments**.

No saltar a infraestructura sin tener el modelo de dominio del contexto en cuestión cubierto con tests.

---

## 8. Cómo colaborar conmigo (Claude) en este repo

- Cuando me pidas implementar una funcionalidad, **identifica primero el bounded context** y respeta sus límites.
- Si una funcionalidad cruza contextos, propón la integración (evento de dominio vs. llamada síncrona) antes de codificar.
- Si ves código que viola las reglas de dependencia o el lenguaje ubicuo, márcalo aunque no sea el foco de la tarea — pero **no refactorices fuera del alcance pedido** sin preguntar.
- Mantén el modelo de dominio **rico**. Si te encuentras escribiendo `if (order.getStatus() == X) order.setStatus(Y)` desde un service, eso es lógica que pertenece al agregado `Order`.

---

*Este documento es un mapa, no un dogma. Cuando una decisión arquitectónica cambie, actualizar aquí antes que en otra parte.*
