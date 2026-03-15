# API de Gestión de Polizas
## Arquitectura y Desiciones de Diseño
- **Rich Domain Model:
- ** Las reglas de negocio (renovación, restricciones de riesgos, cancelación en cascada) 
---  residen dentro de las entidades `Poliza` y `Riesgo`. Esto previene el anti-patrón de "Servicios Anémicos" y centraliza la lógica.
- **gravacion de registros:
  ** Uso de `Records` para el paso de datos.
- **cada quien copn su cada cual:
  ** Separación estricta de responsabilidades 
  -- (Seguridad en Filtros, Dominio en Entidades, Orquestación en Servicios).

## Requisitos Previos
- Java 17+
- Maven/Gradle (dev)

## Ejecución
1. Levantar con `./mvnw spring-boot:run`
2. Configurar en el cliente (Postman/cURL) el header obligatorio: `x-api-key: 1234567890`

## Endpoints Principales
- `GET /polizas?tipo=INDIVIDUAL&estado=ACTIVA`
- `POST /polizas/{id}/renovar` 
- `POST /polizas/{id}/cancelar`
- `POST /polizas/{id}/riesgos` 
