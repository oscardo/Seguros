# API de Gestión de Pólizas

## Arquitectura y Decisiones de Diseño

- **Rich Domain Model**: Las reglas de negocio (renovación, restricciones de riesgos, cancelación en cascada) residen dentro de las entidades `Poliza` y `Riesgo`.
- **Inyección de Dependencias**: Uso de constructor injection con Lombok `@RequiredArgsConstructor`.
- **Separación de responsabilidades**:
  - **Filtros**: Seguridad (API Key validation)
  - **Entidades**: Lógica de dominio
  - **Servicios**: Orquestación y transaccionalidad
  - **Controladores**: HTTP handling
  - **Repositorios**: Persistencia

## Requisitos Previos

- Java 21+
- Maven 3.8+

## Ejecución

```bash
./mvnw spring-boot:run
```

## Configuración de Seguridad

Header requerido: `x-api-key: 123456`

## Endpoints Principales

- `GET /polizas?tipo=INDIVIDUAL&estado=ACTIVA`
- `GET /polizas/{id}/riesgos`
- `POST /polizas/{id}/renovar`
- `POST /polizas/{id}/cancelar`
- `POST /polizas/{id}/riesgos` (solo COLECTIVA)
- `POST /riesgos/{id}/cancelar`
- `POST /core-mock/evento` (mock externo)
