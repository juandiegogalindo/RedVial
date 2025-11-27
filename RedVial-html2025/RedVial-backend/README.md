# Red Vial - Backend (Spring Boot)

Proyecto skeleton para el backend del sistema **Red Vial**.

## Qué incluye
- API REST básica para `reportes`, `ofertas` y `contactos`.
- JPA entities + repositories.
- H2 en memoria por defecto. (Configuración para MySQL incluida en comentarios.)
- CORS habilitado en controladores para permitir llamadas desde tu frontend estático.

## Ejecutar
1. Requiere Java 17 y Maven.
2. Desde la carpeta del proyecto:
   ```bash
   mvn spring-boot:run
   ```
3. Endpoints disponibles:
   - `GET /api/reportes`
   - `POST /api/reportes`
   - `GET /api/ofertas`
   - `POST /api/ofertas`
   - `POST /api/contact`

## Cambiar a MySQL
Editar `src/main/resources/application.properties` y descomentar la configuración de MySQL; agregar driver en `pom.xml`.

