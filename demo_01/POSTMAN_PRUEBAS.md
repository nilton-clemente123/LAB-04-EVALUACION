# Pruebas POSTMAN — Módulo 4: Médicos y Especialidades

Base URL: `http://localhost:8080`

La API se prueba con Postman. Todas las respuestas son JSON.

---

## Requisitos previos

1. Iniciar MySQL (XAMPP) en el puerto 3306.
2. Asegurar que la base de datos `medicos_db` exista o se cree automáticamente.
3. Levantar la aplicación:

```powershell
$env:JAVA_HOME='C:\Users\Alumno\.vscode\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64'
cd C:\Users\Alumno\Desktop\PROYECTOLAB\demo_01
.\mvnw.cmd spring-boot:run
```

4. La base de datos se inicializa automáticamente con especialidades (Cardiología, Pediatría, Medicina Interna, Dermatología), 3 médicos (MED-001..003) y una relación de ejemplo.

---

## 1. RF-MED-01 — Registrar médico

- **Método:** POST
- **URL:** `http://localhost:8080/api/medicos`
- **Headers:** `Content-Type: application/json`

**Body (JSON):**
```json
{
  "codigo": "MED-004",
  "tipoDocumento": "DNI",
  "numeroDocumento": "44556677",
  "nombres": "Ana Lucia",
  "apellidoPaterno": "Torres",
  "apellidoMaterno": "Ramirez",
  "cmp": "CMP-44444",
  "estado": "ACTIVO"
}
```

**Respuesta esperada:** `201 Created` con el médico creado (incluye `id`).

**Pruebas negativas:**
- Código duplicado (`MED-001`) → `409 Conflict`.
- CMP duplicado (`CMP-11111`) → `409 Conflict`.
- Documento duplicado (`12345678`) → `409 Conflict`.
- Campo obligatorio vacío → `400 Bad Request` con `fieldErrors`.
- Estado inválido (`"estado": "XXX"`) → `400 Bad Request`.

---

## 2. RF-MED-02 — Modificar médico

- **Método:** PUT
- **URL:** `http://localhost:8080/api/medicos/{id}`

**Body (JSON):**
```json
{
  "codigo": "MED-004",
  "tipoDocumento": "DNI",
  "numeroDocumento": "44556677",
  "nombres": "Ana Lucia Modificada",
  "apellidoPaterno": "Torres",
  "apellidoMaterno": "Ramirez",
  "cmp": "CMP-44444",
  "estado": "ACTIVO"
}
```

**Respuesta esperada:** `200 OK` con los datos actualizados.

**Pruebas:**
- ID inexistente (`/api/medicos/999`) → `404 Not Found`.
- CMP de otro médico → `409 Conflict`.

---

## 3. RF-MED-03 — Consultar médicos

- **GET todos:** `http://localhost:8080/api/medicos` → `200 OK` (lista JSON).
- **GET por ID:** `http://localhost:8080/api/medicos/1` → `200 OK`.
- **GET ID inexistente:** `http://localhost:8080/api/medicos/999` → `404 Not Found`.

---

## 4. RF-MED-04 — Activar/desactivar médico

- **Método:** PATCH
- **URL:** `http://localhost:8080/api/medicos/{id}/estado`

**Body (JSON) — desactivar:**
```json
{ "estado": "INACTIVO" }
```

**Body (JSON) — activar:**
```json
{ "estado": "ACTIVO" }
```

**Respuesta esperada:** `200 OK` con el médico y su nuevo estado.

**Pruebas:**
- ID inexistente → `404 Not Found`.
- Valor inválido (`"estado": "XXX"`) → `400 Bad Request`.

---

## 5. RF-MED-07 — Registrar especialidad

- **Método:** POST
- **URL:** `http://localhost:8080/api/especialidades`

**Body (JSON):**
```json
{
  "codigo": "ESP-005",
  "nombre": "Neurología",
  "descripcion": "Atención del sistema nervioso",
  "duracionConsulta": 40,
  "estado": "ACTIVA"
}
```

**Respuesta esperada:** `201 Created`.

**Pruebas negativas:**
- Código duplicado (`ESP-001`) → `409 Conflict`.
- Nombre duplicado (`Cardiología`) → `409 Conflict`.
- Duración `0` o negativa → `400 Bad Request`.
- Estado inválido → `400 Bad Request`.

---

## 6. RF-MED-08 — Modificar especialidad

- **Método:** PUT
- **URL:** `http://localhost:8080/api/especialidades/{id}`

**Body (JSON):**
```json
{
  "codigo": "ESP-005",
  "nombre": "Neurología Clínica",
  "descripcion": "Atención del sistema nervioso y cerebro",
  "duracionConsulta": 45,
  "estado": "ACTIVA"
}
```

**Respuesta esperada:** `200 OK`.

**Pruebas:**
- ID inexistente → `404 Not Found`.
- Código de otra especialidad → `409 Conflict`.

---

## 7. RF-MED-09 — Activar/desactivar especialidad

- **Método:** PATCH
- **URL:** `http://localhost:8080/api/especialidades/{id}/estado`

**Body (JSON):**
```json
{ "estado": "INACTIVA" }
```

**Respuesta esperada:** `200 OK`.

---

## 8. RF-MED-10 — Configurar duración de consulta

- **Método:** PATCH
- **URL:** `http://localhost:8080/api/especialidades/{id}/duracion`

**Body (JSON) — Pediatría a 20 min:**
```json
{ "duracionConsulta": 20 }
```

**Body (JSON) — Cardiología a 30 min:**
```json
{ "duracionConsulta": 30 }
```

**Respuesta esperada:** `200 OK` con la duración actualizada.

**Pruebas negativas:**
- `0` → `400 Bad Request`.
- Negativo → `400 Bad Request`.
- ID inexistente → `404 Not Found`.

---

## Matriz RF → Endpoint

| RF | Método | Endpoint |
|----|--------|----------|
| RF-MED-01 | POST | `/api/medicos` |
| RF-MED-02 | PUT | `/api/medicos/{id}` |
| RF-MED-03 | GET | `/api/medicos` y `/api/medicos/{id}` |
| RF-MED-04 | PATCH | `/api/medicos/{id}/estado` |
| RF-MED-07 | POST | `/api/especialidades` |
| RF-MED-08 | PUT | `/api/especialidades/{id}` |
| RF-MED-09 | PATCH | `/api/especialidades/{id}/estado` |
| RF-MED-10 | PATCH | `/api/especialidades/{id}/duracion` |