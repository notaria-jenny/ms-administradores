# ms-administradores

Microservicio de gestión de administradores de la Notaría Jenny. Expone un CRUD REST
sobre el personal de la notaría (notarios, oficiales y funcionarias), con validación de
RUT chileno, contraseñas hasheadas con BCrypt y documentación OpenAPI.

Forma parte de un sistema de microservicios junto a `ms-clientes` y `APIGateway`.

## Stack

- Java 25 · Spring Boot 4.1.0
- Spring Data JPA · MySQL
- Spring Security (BCrypt para el hash de contraseñas)
- Spring HATEOAS · springdoc-openapi 3.0.3 · Spring Boot Actuator
- Lombok · DataFaker (datos de prueba)

## Requisitos

- JDK 25
- MySQL con una base de datos `db_administradores` creada
- Maven (o el wrapper `./mvnw` incluido)

## Configuración

La conexión se define por variables de entorno, con valores por defecto para desarrollo local:

| Variable      | Por defecto                                      |
|---------------|--------------------------------------------------|
| `DB_URL`      | `jdbc:mysql://localhost:3306/db_administradores` |
| `DB_USER`     | `root`                                           |
| `DB_PASSWORD` | *(vacío)*                                        |

El servicio corre en el puerto **8081**.

> `ddl-auto` está en `create-drop`: el esquema se recrea en cada arranque y los datos se
> pierden al detener la aplicación. Es intencional para desarrollo.

## Cómo levantarlo

Local:

```bash
./mvnw spring-boot:run
```

Con Docker:

```bash
docker build -t ms-administradores . && docker run -p 8081:8081 ms-administradores
```

Con el perfil `dev` activo (el de por defecto), el `DataLoader` puebla la base con 10
administradores generados con DataFaker. Todos quedan con la contraseña `Admin123!`.

## Documentación

- Swagger UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs
- Health check: http://localhost:8081/actuator/health

## Endpoints

Ruta base: `/api/v2/administradores`

### CRUD

| Método  | Ruta                   | Descripción                              |
|---------|------------------------|------------------------------------------|
| `POST`  | `/`                    | Crear administrador                      |
| `PUT`   | `/{id}`                | Actualizar datos (sin contraseña ni RUT) |
| `PATCH` | `/{id}/password`       | Cambiar contraseña                       |
| `PATCH` | `/{id}/toggle-activo`  | Activar o desactivar                     |

### Búsquedas

| Método | Ruta              | Descripción         |
|--------|-------------------|---------------------|
| `GET`  | `/{id}`           | Buscar por ID       |
| `GET`  | `/email/{email}`  | Buscar por email    |
| `GET`  | `/rut/{rut}`      | Buscar por RUT      |

### Listados

| Método | Ruta                              | Descripción                        |
|--------|-----------------------------------|------------------------------------|
| `GET`  | `/`                               | Todos, ordenados por nombre        |
| `GET`  | `/paginado?page=0&size=20`        | Paginado y ordenable               |
| `GET`  | `/buscar?nombre=`                 | Filtrar por nombre (parcial)       |
| `GET`  | `/rol/{rol}`                      | Filtrar por rol                    |
| `GET`  | `/activos?estado=activo`          | Filtrar por estado                 |
| `GET`  | `/fecha?desde=&hasta=`            | Por rango de fecha de creación     |
| `GET`  | `/contar/rol/{rol}`               | Contar por rol                     |

Las respuestas individuales incluyen enlaces HATEOAS (`self`, `toggle-activo`, `todos`).

## Modelo

| Campo             | Tipo        | Notas                                    |
|-------------------|-------------|------------------------------------------|
| `idAdministrador` | `Long`      | Autogenerado                             |
| `nombreCompleto`  | `String`    | Máx. 200                                 |
| `rut`             | `String`    | Único, validado con dígito verificador   |
| `email`           | `String`    | Único, formato validado                  |
| `telefono`        | `String`    | Máx. 20                                  |
| `password`        | `String`    | Hasheada con BCrypt, nunca se expone     |
| `rol`             | `Rol`       | `NOTARIO`, `OFICIAL`, `FUNCIONARIAS`     |
| `activo`          | `Boolean`   | `true` al crear                          |
| `fechaCreacion`   | `LocalDate` | Asignada por el sistema                  |

### Validación de RUT

El formato canónico de almacenamiento es **sin puntos, con guion y dígito verificador en
mayúscula**: `12345678-5`, `9876543-K`.

- El dígito verificador se valida con el algoritmo módulo 11.
- Antes de comparar duplicados y de guardar, el RUT se normaliza: se recortan espacios
  accidentales y la `k` pasa a mayúscula. Así la unicidad no depende de la collation de MySQL.
- Los RUT con puntos (`12.345.678-5`) se rechazan con `400`. Dar formato para mostrar es
  responsabilidad del frontend.
- El RUT es **inmutable**: identifica a la persona, por lo que el `PUT` no permite modificarlo.

## Manejo de errores

Todas las respuestas de error comparten el mismo formato (`timestamp`, `status`, `error`):

| Código | Cuándo ocurre                                                  |
|--------|----------------------------------------------------------------|
| `400`  | Validación fallida, rol inexistente, rango de fechas invertido  |
| `404`  | El administrador no existe                                      |
| `409`  | Email o RUT ya registrado                                       |
| `500`  | Error inesperado (el detalle queda en el log, no en la respuesta) |