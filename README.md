# API de Gestión de Pólizas de Seguros - v0.0.2

API RESTful para la gestión de pólizas de seguros, implementada con Spring Boot 3.2.4 y Java 21.

## Novedades versión 0.0.2

- **Mejoras de fechas**: Campos `fechaInicio`, `fechaFin`, `diaEnvioInformacion` en Póliza
- **Mejoras de hora de tráfico**: Campo `horaTraffico` en Póliza y Riesgo
- **Seguimiento de pasos**: Enum `PasoProceso` para trackear cada fase del proceso (1-6)

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
./mvnw spring-boot:run

## Configuración de Seguridad

Todos los endpoints requieren el header:
x-api-key: 1234567890


## Endpoints

### Pólizas

| Método | Endpoint | Descripción | Paso |
|--------|----------|-------------|------|
| GET | `/polizas?tipo=INDIVIDUAL&estado=ACTIVA` | Listar pólizas por tipo y estado | 1 |
| GET | `/polizas/{id}` | Obtener detalle de póliza | - |
| GET | `/polizas/{id}/riesgos` | Obtener riesgos de una póliza | 2 |
| POST | `/polizas/{id}/renovar` | Renovar póliza (+IPC 5%) | 3 |
| POST | `/polizas/{id}/cancelar` | Cancelar póliza (y sus riesgos) | 4 |
| POST | `/polizas/{id}/riesgos` | Agregar riesgo (solo COLECTIVA) | 5 |

### Riesgos

POST | `/riesgos/{id}/cancelar` | Cancelar un riesgo específico | 6 |

### Mock CORE Externo
 POST | `/core-mock/evento` | Registrar evento de notificación al CORE |

**Payload:**
```json
{
  "evento": "ACTUALIZACION",
  "polizaId": 555
}
```

## Ejemplos de Uso

### Listar pólizas
curl -X GET "http://localhost:8080/polizas?tipo=INDIVIDUAL&estado=ACTIVA" \
  -H "x-api-key: 123456"

### Renovar póliza
curl -X POST "http://localhost:8080/polizas/1/renovar" \
  -H "x-api-key: 123456"

### Cancelar póliza
curl -X POST "http://localhost:8080/polizas/1/cancelar" \
  -H "x-api-key: 123456"

### Agregar riesgo a póliza colectiva
curl -X POST "http://localhost:8080/polizas/1/riesgos" \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{"horaTraffico": "08:30:00"}'

### Cancelar riesgo
curl -X POST "http://localhost:8080/riesgos/1/cancelar" \
  -H "x-api-key: 123456"

## Reglas de Negocio

1. **Individual**: Solo puede tener 1 riesgo asociado.
2. **Renovación**: 
   - Incrementa canon y prima en +5% (IPC)
   - Estado cambia a `RENOVADA`
   - No se puede renovar una póliza cancelada
3. **Cancelación de póliza**: Cancela automáticamente todos sus riesgos asociados.
4. **Agregar riesgo**: Solo permitido para pólizas de tipo `COLECTIVA`.
## Estructura del Proyecto

src/main/java/com/
├── SegurosApplication.java        # Main class + RestTemplate bean
├── config/
│   └── ApiKeyFilter.java          # Filtro de seguridad
├── domain/
│   ├── Poliza.java                # Entidad póliza (v0.0.2)
│   ├── Riesgo.java                # Entidad riesgo (v0.0.2)
│   ├── TipoPoliza.java            # Enum: INDIVIDUAL, COLECTIVA
│   ├── EstadoPoliza.java          # Enum: ACTIVA, RENOVADA, CANCELADA
│   ├── EstadoRiesgo.java          # Enum: ACTIVO, CANCELADO
│   └── PasoProceso.java           # Enum: Seguimiento de pasos (v0.0.2)
├── repository/
│   ├── PolizaRepository.java
│   └── RiesgoRepository.java
├── service/
│   └── PolizaService.java
└── web/
    ├── PolizaController.java
    ├── RiesgoController.java
    └── CoreMockController.java

## Modelo de Datos
### Poliza (v0.0.2)

| id | Long | ID automático |
| tipo | TipoPoliza | INDIVIDUAL / COLECTIVA |
| estado | EstadoPoliza | ACTIVA / RENOVADA / CANCELADA |
| canon | BigDecimal | Canon anual |
| prima | BigDecimal | Prima anual |
| fechaInicio | LocalDate | Fecha de inicio (v0.0.2) |
| fechaFin | LocalDate | Fecha de fin (v0.0.2) |
| diaEnvioInformacion | Integer | Día de envío de información (v0.0.2) |
| horaTraffico | LocalTime | Hora de tráfico (v0.0.2) |
| pasoProceso | PasoProceso | Paso actual del proceso (v0.0.2) |
| riesgos | List<Riesgo> | Riesgos asociados |

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | ID automático |
| estado | EstadoRiesgo | ACTIVO / CANCELADO |
| poliza | Poliza | Póliza asociada |
| horaTraffico | LocalTime | Hora de tráfico (v0.0.2) |

Enum que trackea el avance del proceso:

1. `INICIADO` - Estado inicial
2. `LISTADO` - Paso 1: Póliza listada
3. `RIESGOS_OBTENIDOS` - Paso 2: Riesgos obtenidos
4. `RENOVADA` - Paso 3: Póliza renovada
5. `CANCELADA` - Paso 4: Póliza cancelada
6. `RIESGO_AGREGADO` - Paso 5: Riesgo agregado
7. `RIESGO_CANCELADO` - Paso 6: Riesgo cancelado
8. `COMPLETADO` - Proceso completado

## Tecnologías
- Spring Boot 3.2.4
- Spring Data JPA
- Lombok
- H2 Database
- Java 21

