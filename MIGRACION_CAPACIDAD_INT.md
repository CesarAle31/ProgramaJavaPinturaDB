# 🔄 Migración: Cambio de Capacidad VARCHAR(100) → INT

## 📊 Resumen de Cambios en Base de Datos

El archivo `alter_table_pintura.sql` realiza los siguientes cambios:

### 1️⃣ Corrección de Datos
```sql
UPDATE pintura
SET capacidad = 1
WHERE capacidad IS NULL OR capacidad = '' OR CAST(capacidad AS DECIMAL(10,2)) <= 0;
```
- Convierte valores inválidos a **1** (mínimo permitido)
- Se ejecuta ANTES de cambiar el tipo de dato

### 2️⃣ Cambio de Estructura
```sql
ALTER TABLE pintura
MODIFY capacidad INT NOT NULL;
```
- **Antes**: `varchar(100)` (permite texto como "abc", "-5", etc.)
- **Después**: `int` (solo números enteros positivos)

### 3️⃣ Validaciones Agregadas (CHECK Constraints)
```sql
ALTER TABLE pintura
ADD CONSTRAINT chk_pintura_capacidad CHECK (capacidad > 0);
ADD CONSTRAINT chk_pintura_stock CHECK (stock >= 0);
ADD CONSTRAINT chk_pintura_costo CHECK (costo > 0);
ADD CONSTRAINT chk_pintura_nombre_not_empty CHECK (TRIM(nombre) != '');
ADD CONSTRAINT chk_pintura_color_not_empty CHECK (TRIM(color) != '');
ADD CONSTRAINT chk_pintura_presentacion_not_empty CHECK (TRIM(presentacion) != '');
```

---

## 💻 CAMBIOS REQUERIDOS EN JAVA

### Impacto en Modelo `Producto.java`

**Cambiar tipo de dato:**
```java
// ANTES
private String capacidad;

public String getCapacidad() { return capacidad; }
public void setCapacidad(String capacidad) { this.capacidad = capacidad; }

// DESPUÉS
private int capacidad;

public int getCapacidad() { return capacidad; }
public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
```

---

### Impacto en `ProductoDAO.java`

#### Método `agregar()`
```java
// ANTES (CallableStatement con String)
cstmt.setString(5, p.getCapacidad());

// DESPUÉS (CallableStatement con INT)
cstmt.setInt(5, p.getCapacidad());
```

#### Método `actualizar()`
```java
// ANTES (CallableStatement con String)
cstmt.setString(6, p.getCapacidad());

// DESPUÉS (CallableStatement con INT)
cstmt.setInt(6, p.getCapacidad());
```

#### Método `mapearProducto()`
```java
// ANTES
producto.setCapacidad(rs.getString("capacidad"));

// DESPUÉS
producto.setCapacidad(rs.getInt("capacidad"));
```

#### Método `buscar()`
```java
// Nota: Este método busca por LIKE en capacidad
// Ahora que es INT, podría necesitar ajuste

// ANTES (búsqueda de texto)
String sql = "SELECT * FROM pintura WHERE ... capacidad LIKE ? ...";

// DESPUÉS (búsqueda numérica)
// Opción 1: Buscar por valor exacto
String sql = "SELECT * FROM pintura WHERE ... CAST(capacidad AS CHAR) LIKE ? ...";

// O Opción 2: Cambiar lógica de búsqueda si es aplicable
String sql = "SELECT * FROM pintura WHERE nombre LIKE ? OR color LIKE ? OR presentacion LIKE ?";
```

---

## 📝 Procedimientos Almacenados (Sin cambios en sintaxis)

**`sp_agregar_pintura` y `sp_actualizar_pintura`**

Los procedimientos ya están adaptados para `INT` porque en SQL se usan genéricamente:

```sql
-- ANTES (VARCHAR(100))
IN p_capacidad VARCHAR(100),
...
capacidad = p_capacidad,

-- DESPUÉS (INT) - El tipo se define en el CREATE PROCEDURE
IN p_capacidad INT,
...
capacidad = p_capacidad,
```

**Necesita actualización en `stored_procedures_pintura.sql`:**

```sql
-- En sp_agregar_pintura:
CREATE PROCEDURE sp_agregar_pintura(
    IN p_claveClasificacion INT UNSIGNED,
    IN p_idProveedor INT UNSIGNED,
    IN p_nombre VARCHAR(100),
    IN p_color VARCHAR(100),
    IN p_capacidad INT,              -- CAMBIO: INT (antes VARCHAR(100))
    IN p_stock INT,
    IN p_costo DECIMAL(15,2),
    IN p_presentacion VARCHAR(50)
)

-- En sp_actualizar_pintura:
CREATE PROCEDURE sp_actualizar_pintura(
    IN p_idPintura INT UNSIGNED,
    IN p_claveClasificacion INT UNSIGNED,
    IN p_idProveedor INT UNSIGNED,
    IN p_nombre VARCHAR(100),
    IN p_color VARCHAR(100),
    IN p_capacidad INT,              -- CAMBIO: INT (antes VARCHAR(100))
    IN p_stock INT,
    IN p_costo DECIMAL(15,2),
    IN p_presentacion VARCHAR(50)
)
```

---

## 🔧 Checklist de Migración

### Paso 1: Base de Datos
- [ ] Ejecutar `alter_table_pintura.sql` en MariaDB
- [ ] Verificar: `DESCRIBE pintura;` (capacidad debe ser INT)
- [ ] Verificar constraints: `SHOW CREATE TABLE pintura;`

### Paso 2: Modelo Java
- [ ] Cambiar `private String capacidad;` → `private int capacidad;`
- [ ] Actualizar getters/setters de `capacidad`

### Paso 3: ProductoDAO.java
- [ ] `agregar()`: Cambiar `setString(5, ...)` → `setInt(5, ...)`
- [ ] `actualizar()`: Cambiar `setString(6, ...)` → `setInt(6, ...)`
- [ ] `mapearProducto()`: Cambiar `rs.getString("capacidad")` → `rs.getInt("capacidad")`
- [ ] `buscar()`: Ajustar lógica de búsqueda si es necesario

### Paso 4: Interfaz Gráfica (ProductosPanel.java, etc.)
- [ ] Ajustar campos de entrada para capacidad (solo números)
- [ ] Validar rango: capacidad > 0
- [ ] Cambiar parseo: `Integer.parseInt()` en lugar de mantenerlo como String

### Paso 5: Triggers
- [ ] Los triggers existentes siguen siendo válidos
- [ ] Opcionalmente: Simplificar validaciones de capacidad (ya no necesita REGEXP)

---

## ✅ Ventajas de esta Migración

| Aspecto | Antes (VARCHAR) | Después (INT) |
|--------|-----------------|---------------|
| **Seguridad** | Permite "-5", "abc" | Solo valores > 0 |
| **Performance** | Búsqueda lenta (CAST) | Búsqueda rápida |
| **Espacio** | 100 bytes por campo | 4 bytes por campo |
| **Validación** | En triggers/Java | En BD (CHECK) + triggers |
| **Consistencia** | Riesgo de datos inválidos | Garantizada a nivel de BD |

---

## ⚠️ Notas Importantes

1. **Valores inválidos se corrigen a 1** antes de cambiar el tipo
2. **No hay pérdida de datos** - solo se ajustan valores inválidos
3. **Los triggers BEFORE INSERT/UPDATE siguen activos**
4. **Las búsquedas pueden necesitar ajuste** dependiendo de la lógica de negocio
5. **El cambio es irreversible** en producción (guardar backup antes)

---

## 🔄 Orden de Ejecución Recomendado

```
1. Backup de BD
2. Ejecutar alter_table_pintura.sql
3. Actualizar Producto.java
4. Actualizar ProductoDAO.java
5. Actualizar stored_procedures_pintura.sql (solo comentario de tipo)
6. Actualizar interfaz gráfica si es necesario
7. Recompilar y probar
8. Deploy
```

