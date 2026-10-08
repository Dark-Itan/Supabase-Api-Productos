# Supabase Productos API

API RESTful que consume Supabase (PostgREST) para gestionar productos. Desarrollada con Spring Boot 4, RestClient y arquitectura en capas siguiendo principios SOLID.

**Autor:** Geovany Guadalupe Gomez Rodas
**Repositorio:** https://github.com/Dark-Itan/Supabase-Api-Productos

---

## Tabla de Contenidos

1. [Descripción](#1-descripción)
2. [Tecnologías Utilizadas](#2-tecnologías-utilizadas)
3. [Arquitectura del Proyecto](#3-arquitectura-del-proyecto)
4. [Base de Datos en Supabase](#4-base-de-datos-en-supabase)
5. [Endpoints de la API](#5-endpoints-de-la-api)
6. [Ejemplos de Peticiones](#6-ejemplos-de-peticiones)
7. [Ejecución Local](#7-ejecución-local)
8. [Errores Detectados y Correcciones](#8-errores-detectados-y-correcciones)
9. [Mejoras Aplicadas](#9-mejoras-aplicadas)
10. [Colección de Postman](#10-colección-de-postman)
11. [Referencias](#11-referencias)

---

## 1. Descripción

Aplicación Spring Boot que actúa como **cliente HTTP** consumiendo la API REST autogenerada por **Supabase** (PostgREST) para gestionar productos. Supabase es una plataforma BaaS basada en PostgreSQL que expone automáticamente una API RESTful para cada tabla creada.

El proyecto demuestra cómo consumir una API de terceros (Database-as-a-Service) sin usar JDBC, aplicando buenas prácticas de desarrollo como arquitectura en capas, inyección por constructor, DTOs y manejo centralizado de errores.

---

## 2. Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 LTS | Lenguaje de programación |
| Spring Boot | 4.1.1 | Framework principal |
| Spring Web MVC | 7.0.9 | REST API |
| Spring Security | 7.1.1 | Seguridad y CORS |
| Spring Validation | 4.1.1 | Validaciones |
| RestClient | 7.0.9 | Cliente HTTP para consumir Supabase |
| JdkClientHttpRequestFactory | - | Factory HTTP que soporta PUT/PATCH/DELETE |
| Supabase | - | Base de datos PostgreSQL como servicio |
| Gradle | 9.7.1 | Build Tool |
| Dotenv Java | 3.0.0 | Carga de variables de entorno |

---

## 3. Arquitectura del Proyecto

El proyecto sigue una **arquitectura en capas** con separación clara de responsabilidades:

```
┌─────────────────────────────────────┐
│  Controller (HTTP)                  │
│  - Recibe peticiones                │
│  - Valida DTOs                      │
│  - Retorna ResponseEntity           │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Service (Negocio)                  │
│  - Lógica de negocio                │
│  - Orquesta llamadas a Supabase     │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  RestClient (Infraestructura)       │
│  - Consume API de Supabase          │
│  - Agrega headers de autenticación  │
└─────────────────────────────────────┘
```

### Estructura de Paquetes

```
src/main/java/com/ejemplo/productos/
├── SupabaseProductosApiApplication.java
├── config/
│   ├── SupabaseProperties.java
│   ├── RestClientConfig.java
│   ├── SecurityConfig.java
│   └── DotenvConfig.java
├── controller/
│   ├── HomeController.java
│   └── ProductoController.java
├── dto/
│   ├── ProductoDTO.java
│   ├── CrearProductoRequest.java
│   ├── ActualizarProductoRequest.java
│   └── ErrorResponse.java
├── exception/
│   ├── ProductoNotFoundException.java
│   ├── SupabaseException.java
│   └── GlobalExceptionHandler.java
└── service/
    ├── ProductoService.java
    └── impl/
        └── ProductoServiceImpl.java
```

**Principios aplicados:**
- **SRP (Single Responsibility):** Cada clase tiene una responsabilidad única.
- **DIP (Dependency Inversion):** Inyección por constructor, no `@Autowired` en campos.
- **ISP (Interface Segregation):** Interface `ProductoService` separada de su implementación.
- **Clean Code:** Nombres descriptivos, métodos pequeños, sin código espagueti.

---

## 4. Base de Datos en Supabase

### 4.1 Tecnología

- **Motor:** PostgreSQL (gestionado por Supabase)
- **API:** PostgREST (autogenerada)
- **Hosting:** Supabase (nube, plan gratuito)
- **Autenticación:** API Key (Publishable key) + RLS

### 4.2 Estructura de la Tabla `productos`

| Columna | Tipo | Restricciones | Descripción |
|---------|------|---------------|-------------|
| id | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Identificador único |
| nombre | TEXT | NOT NULL | Nombre del producto |
| precio | NUMERIC(10,2) | NOT NULL | Precio del producto |
| stock | INTEGER | NOT NULL, DEFAULT 0 | Cantidad en inventario |
| created_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() | Fecha de creación |

### 4.3 Script SQL

```sql
CREATE TABLE productos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre TEXT NOT NULL,
    precio NUMERIC(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### 4.4 Políticas RLS (Row Level Security)

```sql
CREATE POLICY "Permitir lectura publica"
ON productos FOR SELECT TO public USING (true);

CREATE POLICY "Permitir insercion publica"
ON productos FOR INSERT TO public WITH CHECK (true);

CREATE POLICY "Permitir actualizacion publica"
ON productos FOR UPDATE TO public USING (true) WITH CHECK (true);

CREATE POLICY "Permitir eliminacion publica"
ON productos FOR DELETE TO public USING (true);
```

### 4.5 Configuración de Conexión

La conexión se configura mediante **variables de entorno**:

| Variable | Descripción |
|----------|-------------|
| SUPABASE_URL | URL base de la API REST (`https://xxx.supabase.co/rest/v1`) |
| SUPABASE_KEY | Publishable key de Supabase |

---

## 5. Endpoints de la API

### 5.1 Endpoints del Backend (Spring Boot)

| Método | Endpoint | Descripción | Códigos |
|--------|----------|-------------|---------|
| GET | `/api/v1/productos` | Listar todos los productos | 200 |
| GET | `/api/v1/productos/{id}` | Obtener producto por UUID | 200, 404 |
| POST | `/api/v1/productos` | Crear nuevo producto | 201, 400 |
| PUT | `/api/v1/productos/{id}` | Actualizar producto | 200, 400, 404 |
| DELETE | `/api/v1/productos/{id}` | Eliminar producto | 204, 404 |

### 5.2 Detalle de Códigos de Estado

| Código | Significado | Cuándo ocurre |
|--------|-------------|---------------|
| 200 OK | Éxito | Operación exitosa |
| 201 Created | Creado | POST exitoso |
| 204 No Content | Sin contenido | DELETE exitoso |
| 400 Bad Request | Datos inválidos | Validación fallida |
| 404 Not Found | No encontrado | UUID inexistente |
| 503 Service Unavailable | Servicio no disponible | Error de comunicación con Supabase |

---

## 6. Ejemplos de Peticiones

### 6.1 Listar todos los productos

**cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/productos
```

**Respuesta exitosa (200 OK):**
```json
[
    {
        "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
        "nombre": "Laptop HP",
        "precio": 15000.50,
        "stock": 10
    }
]
```

---

### 6.2 Obtener producto por ID

**cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/productos/a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Respuesta error (404 Not Found):**
```json
{
    "status": 404,
    "message": "El producto solicitado no existe.",
    "timestamp": "2026-10-07T19:43:07.226378"
}
```

---

### 6.3 Crear producto

**cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/productos \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Monitor Samsung",
    "precio": 8000.00,
    "stock": 15
  }'
```

**Respuesta exitosa (201 Created):**
```json
{
    "id": "51162bf4-3bdd-45b2-8d83-a36ce53b60a9",
    "nombre": "Monitor Samsung",
    "precio": 8000.00,
    "stock": 15
}
```

**Respuesta error (400 Bad Request):**
```json
{
    "nombre": "El nombre es obligatorio",
    "precio": "El precio debe ser mayor a 0",
    "stock": "El stock no puede ser negativo"
}
```

---

### 6.4 Actualizar producto

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/v1/productos/51162bf4-3bdd-45b2-8d83-a36ce53b60a9 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Monitor Samsung Actualizado",
    "precio": 7500.00,
    "stock": 12
  }'
```

**Respuesta exitosa (200 OK):**
```json
{
    "id": "51162bf4-3bdd-45b2-8d83-a36ce53b60a9",
    "nombre": "Monitor Samsung Actualizado",
    "precio": 7500.00,
    "stock": 12
}
```

---

### 6.5 Eliminar producto

**cURL:**
```bash
curl -X DELETE http://localhost:8080/api/v1/productos/51162bf4-3bdd-45b2-8d83-a36ce53b60a9
```

**Respuesta exitosa (204 No Content):**
```
(sin body)
```

---

## 7. Ejecución Local

### 7.1 Requisitos Previos

- **Java 21** instalado
- **Git** instalado
- **Cuenta en Supabase** (gratuita)

### 7.2 Pasos

#### 1. Clonar el repositorio

```bash
git clone https://github.com/Dark-Itan/Supabase-Api-Productos.git
cd Supabase-Api-Productos
```

#### 2. Configurar Supabase

1. Crear cuenta en https://supabase.com/
2. Crear proyecto
3. Crear tabla `productos` (ver Script SQL en sección 4.3)
4. Configurar políticas RLS (ver sección 4.4)
5. Obtener credenciales:
    - Project URL
    - Publishable key

#### 3. Configurar variables de entorno

Crear archivo `.env` en la raíz:

```properties
SUPABASE_URL=https://tu-proyecto.supabase.co/rest/v1
SUPABASE_KEY=tu-publishable-key
```

#### 4. Ejecutar la aplicación

**Windows (PowerShell):**
```powershell
./run.ps1
```

**Linux/Mac:**
```bash
./gradlew bootRun
```

#### 5. Verificar

Abrir en el navegador:
```
http://localhost:8080
```

Debería mostrar: `Supabase Productos API - Corriendo correctamente`

---

## 8. Errores Detectados y Correcciones

Durante el desarrollo se detectaron **4 errores** en el código propuesto por el PDF. A continuación se documentan las correcciones aplicadas:

### 8.1 Error: URI mal escrito en el PDF

**Código original (PDF):**
```java
.uri("/products?select $= *$")
```

**Problema:** Sintaxis inválida de PostgREST.

**Corrección:**
```java
.uri("/productos?select=*")
```

**Justificación:** El operador correcto es `?select=*` (sin espacios ni `$`).

---

### 8.2 Error: Método PATCH no soportado

**Código original (PDF):**
```java
supabaseRestClient.patch()
```

**Problema:** `Invalid HTTP method: PATCH`. El `SimpleClientHttpRequestFactory` de Spring Boot 4.1.1 no soporta PATCH.

**Corrección:**
```java
HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(properties.getTimeout()))
        .build();

JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);

// En el Service
supabaseRestClient.put()
```

**Justificación:** `JdkClientHttpRequestFactory` (Java 11+) soporta todos los métodos HTTP.

---

### 8.3 Error: `@NotNull` en String

**Código original (PDF):**
```java
@NotNull(message = "El nombre es obligatorio")
String nombre
```

**Problema:** `@NotNull` solo valida que no sea `null`, pero permite strings vacíos.

**Corrección:**
```java
@NotBlank(message = "El nombre es obligatorio")
String nombre
```

**Justificación:** `@NotBlank` valida que no sea `null`, vacío ni solo espacios.

---

### 8.4 Error: Inconsistencia en nombre de tabla

**Código original (PDF):** `/products`

**Problema:** El PDF mezcla `productos` (español) y `products` (inglés).

**Corrección:** Usar `/productos` en todo el proyecto.

**Justificación:** Coherencia con la tabla creada en Supabase.

---

## 9. Mejoras Aplicadas

Además de corregir los errores, se aplicaron las siguientes mejoras:

### 9.1 Configuración tipada con `@ConfigurationProperties`

```java
@Configuration
@ConfigurationProperties(prefix = "supabase")
public class SupabaseProperties {
    private String url;
    private String key;
    private int timeout = 5000;
}
```

### 9.2 Timeouts configurables

```java
factory.setConnectTimeout(properties.getTimeout());
factory.setReadTimeout(properties.getTimeout());
```

### 9.3 Manejo centralizado de errores

`GlobalExceptionHandler` con `@ControllerAdvice` maneja:
- `ProductoNotFoundException` → 404
- `SupabaseException` → 503
- `MethodArgumentNotValidException` → 400
- `Exception` → 500

### 9.4 Mensajes humanos vs técnicos

**Cliente:** `"El producto solicitado no existe."`
**Backend:** `WARN - Producto no encontrado con ID: xxx`

### 9.5 Logs estructurados con SLF4J

### 9.6 Variables de entorno con `.env`

### 9.7 Validación con Bean Validation

### 9.8 Separación `service/` y `service/impl/`

---

## 10. Colección de Postman

### 10.1 Importar la Colección

1. Abrir Postman
2. Click en **"Import"**
3. Seleccionar `postman_collection.json`
4. Click en **"Import"**

### 10.2 Importar el Entorno

1. Click en **"Environments"** (barra lateral)
2. Click en **"Import"**
3. Seleccionar `postman_environment.json`
4. Click en **"Import"**
5. Seleccionar el entorno `Supabase Productos API`

### 10.3 Ejecutar la Colección

1. Click derecho en la colección
2. Click en **"Run collection"**
3. Click en **"Run Supabase Productos API"**

**Resultado esperado:** 7/7 peticiones exitosas.

---

## 11. Referencias

- Supabase. (2026). *Supabase Documentation*. https://supabase.com/docs
- PostgREST. (2026). *PostgREST Documentation*. https://postgrest.org/
- Spring Boot. (2025). *Calling REST Services*. https://docs.spring.io/spring-boot/reference/io/rest-client.html
- Spring Boot. (2025). *Validation*. https://docs.spring.io/spring-boot/reference/io/validation.html
- Spring Security. (2025). *CORS Configuration*. https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html

---

## Licencia

Este proyecto es de uso educativo.