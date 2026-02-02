# Pricing Service

Desarrollador: Diego Montalvo

Servicio Spring Boot que consulta el precio aplicable para un producto en una fecha determinada. Implementa arquitectura hexagonal con base de datos H2 en memoria.

## Arquitectura

### Hexagonal (Ports & Adapters)

```
domain/          → Lógica de negocio, modelos, puertos
application/     → Casos de uso, servicios
infrastructure/  → Adaptadores (web, persistencia)
  ├── web/       → Controladores REST (entrada)
  └── persistence/ → Repositorios JPA (salida)
```

### API First

- Especificación OpenAPI en `docs/openapi.yaml`
- Código generado automáticamente desde el contrato
- Documentación sincronizada con implementación
- Swagger UI disponible para pruebas

## Requisitos

- JDK 21+
- Maven Wrapper incluido

## Ejecución

```bash
./mvnw spring-boot:run -pl pricing-infrastructure
```

## Docker

```bash
docker-compose up --build
```

Aplicación disponible en `http://localhost:8080`

## Endpoints

- **API REST**: `GET /api/v1/prices`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI Spec**: `http://localhost:8080/api-docs`
- **H2 Console**: `http://localhost:8080/h2-console` (jdbc:h2:mem:prices, user: sa)

### Ejemplo

```bash
curl "http://localhost:8080/api/v1/prices?brandId=1&productId=35455&applicationDate=2020-06-14T10:00:00"
```

Respuesta:

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 1,
  "startDate": "2020-06-14T00:00:00",
  "endDate": "2020-12-31T23:59:59",
  "amount": 35.5,
  "currency": "EUR"
}
```

## Tests

```bash
./mvnw test
```

**16 tests** en total:

- **1** test de contexto Spring
- **2** tests unitarios (servicio de dominio)
- **7** tests de integración (MockMvc)
- **6** tests E2E (RestAssured)

Cubre los **5 casos del enunciado**:

- ✅ Test 1: 10:00 del día 14 → 35.50€
- ✅ Test 2: 16:00 del día 14 → 25.45€
- ✅ Test 3: 21:00 del día 14 → 35.50€
- ✅ Test 4: 10:00 del día 15 → 30.50€
- ✅ Test 5: 21:00 del día 16 → 38.95€

## Características Técnicas

### Dependencias Justificadas

- **Spring Boot 3.3.8**: Framework base
- **Spring Data JPA**: Persistencia
- **H2**: Base de datos en memoria
- **Lombok**: Reducción boilerplate
- **SpringDoc OpenAPI**: Documentación y UI
- **OpenAPI Generator**: Generación de código desde spec
- **RestAssured**: Tests E2E
