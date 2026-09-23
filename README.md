# Pedidos360 - Microservicio BFF (Backend For Frontend)

Microservicio intermedio en **Spring Boot 3** que actúa como BFF y puerta de enlace hacia los microservicios de Catálogo y Pedidos, aplicando seguridad centralizada con **Microsoft Entra ID (OAuth2 Resource Server)** y control de acceso basado en roles (RBAC).

---

## 🏛️ Funcionalidades

- **Seguridad JWT**: Valida tokens emitidos por Microsoft Entra ID usando sus claves públicas JWKS.
- **Autorización por Roles**: Convierte los App Roles de Entra ID (`Admin`, `Operador`, `Cliente`) en `ROLE_*` de Spring Security y protege endpoints con `@PreAuthorize`.
- **Ruteo & Orquestación**: Expone endpoints unificados hacia el Frontend Angular y delega en:
  - Microservicio de Catálogo (`http://localhost:8082` o variable `CATALOG_SERVICE_URL`)
  - Microservicio de Pedidos (`http://localhost:8081` o variable `ORDERS_SERVICE_URL`)
- **CORS Centralizado**: Permite orígenes autorizados (`http://localhost:4200` y AWS).

---

## ⚙️ Variables de Entorno

| Variable | Descripción | Valor por Defecto |
| :--- | :--- | :--- |
| `SERVER_PORT` | Puerto de escucha | `8080` |
| `ENTRA_TENANT_ID` | Tenant ID de Microsoft Entra ID | `aeb5f35e-b0f1-4661-9bf0-506586dad4f1` |
| `ENTRA_API_CLIENT_ID`| Client ID / Audience de la API | `6bb50e2a-1850-4994-88b4-ce9327a8a1ff` |
| `CATALOG_SERVICE_URL`| URL del microservicio de Catálogo | `http://localhost:8082` |
| `ORDERS_SERVICE_URL` | URL del microservicio de Pedidos | `http://localhost:8081` |

---

## 🚀 Compilación y Ejecución

```bash
# Compilar JAR
./mvnw clean package -DskipTests

# Ejecutar localmente
java -jar target/ms-pedidos360-bff-0.0.1-SNAPSHOT.jar
```
