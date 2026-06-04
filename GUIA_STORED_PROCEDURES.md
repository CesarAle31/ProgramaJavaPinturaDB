# Guía de Implementación: Procedimientos Almacenados + CallableStatement

## 📋 Resumen de Cambios

El código ha sido actualizado para usar **Procedimientos Almacenados (Stored Procedures)** en lugar de SQL directo, invocados mediante `CallableStatement` en Java.

---

## 🗄️ PARTE 1: PROCEDIMIENTOS ALMACENADOS (SQL)

### Archivo: `stored_procedures_pintura.sql`

#### 1️⃣ **sp_agregar_pintura**
```sql
CALL sp_agregar_pintura(
    p_claveClasificacion,
    p_idProveedor,
    p_nombre,
    p_color,
    p_capacidad,
    p_stock,
    p_costo,
    p_presentacion
);
```
- **Parámetros**: 8 entrada (todos excepto idPintura)
- **Acción**: Inserta un nuevo registro en `pintura`
- **Validación**: Triggers BEFORE INSERT validarán datos

---

#### 2️⃣ **sp_buscar_pintura**
```sql
CALL sp_buscar_pintura(p_idPintura);
```
- **Parámetros**: 1 entrada
- **Retorna**: SELECT con todos los campos del registro
- **Uso**: Búsqueda por ID primario

---

#### 3️⃣ **sp_actualizar_pintura**
```sql
CALL sp_actualizar_pintura(
    p_idPintura,                  -- Condición WHERE
    p_claveClasificacion,
    p_idProveedor,
    p_nombre,
    p_color,
    p_capacidad,
    p_stock,
    p_costo,
    p_presentacion
);
```
- **Parámetros**: 9 entrada (incluyendo p_idPintura)
- **Acción**: Actualiza un registro existente
- **Validación**: Triggers BEFORE UPDATE validarán datos

---

#### 4️⃣ **sp_eliminar_pintura**
```sql
CALL sp_eliminar_pintura(p_idPintura);
```
- **Parámetros**: 1 entrada
- **Acción**: Elimina un registro
- **Restricciones**: BEFORE DELETE trigger validará (ej: evitar si hay tickets asociados)

---

## 💻 PARTE 2: CÓDIGO JAVA (ProductoDAO.java)

### Cambios Principales

#### **Antes (PreparedStatement)**
```java
private static final String INSERT_SQL =
    "INSERT INTO pintura (idPintura, claveClasificacion, ...) VALUES (?, ?, ?, ...);";

public void agregar(Producto p) throws SQLException {
    try (Connection conn = DatabaseConnection.getConnection()) {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {
            pstmt.setInt(1, idPintura);
            pstmt.setInt(2, p.getClaveClasificacion());
            // ... más setters
            pstmt.executeUpdate();
        }
    } catch (SQLException e) {
        throw e;
    }
}
```

#### **Después (CallableStatement)**
```java
private static final String CALL_AGREGAR = "{call sp_agregar_pintura(?, ?, ?, ?, ?, ?, ?, ?)}";

public void agregar(Producto p) throws SQLException {
    try (Connection conn = DatabaseConnection.getConnection()) {
        try (CallableStatement cstmt = conn.prepareCall(CALL_AGREGAR)) {
            cstmt.setInt(1, p.getClaveClasificacion());      // 1er parámetro del SP
            cstmt.setInt(2, p.getIdProveedor());             // 2do parámetro del SP
            cstmt.setString(3, p.getNombre().trim());        // 3er parámetro del SP
            cstmt.setString(4, p.getColor().trim());         // 4to parámetro del SP
            cstmt.setString(5, p.getCapacidad());            // 5to parámetro del SP
            cstmt.setInt(6, p.getStock());                   // 6to parámetro del SP
            cstmt.setBigDecimal(7, p.getCostoDecimal());     // 7mo parámetro del SP
            cstmt.setString(8, p.getPresentacion());         // 8vo parámetro del SP
            
            cstmt.executeUpdate();
        }
    } catch (SQLException e) {
        throw e;  // Propaga errores para que JOptionPane los capture
    }
}
```

---

### Métodos Actualizados

| Método | CallableStatement | Parámetros |
|--------|-------------------|-----------|
| `agregar()` | `{call sp_agregar_pintura(...)}` | 8 |
| `buscarPorId()` | `{call sp_buscar_pintura(...)}` | 1 |
| `actualizar()` | `{call sp_actualizar_pintura(...)}` | 9 |
| `eliminar()` | `{call sp_eliminar_pintura(...)}` | 1 |

---

## ⚙️ Instalación en MariaDB

### Paso 1: Ejecutar los Procedimientos
```sql
-- En HeidiSQL, MySQL Workbench o línea de comandos:
source stored_procedures_pintura.sql;

-- O copiar y pegar el contenido del archivo
```

### Paso 2: Verificar que fueron creados
```sql
SHOW PROCEDURE STATUS WHERE db = 'IPESAPinturas';
```

Deberías ver:
```
sp_agregar_pintura
sp_buscar_pintura
sp_actualizar_pintura
sp_eliminar_pintura
```

---

## 🔄 Flujo de Validación

```
Java (ProductoDAO)
    ↓
CallableStatement → Procedimiento Almacenado
    ↓
Trigger BEFORE INSERT/UPDATE/DELETE
    ↓
SIGNAL SQLSTATE '45000' (si hay error)
    ↓
SQLException en Java
    ↓
catch (SQLException e) → JOptionPane.showMessageDialog()
```

---

## ✅ Ventajas de esta Implementación

1. **Separación de Responsabilidades**: Lógica SQL en la BD, no en Java
2. **Reutilización**: Los procedimientos pueden ser llamados desde cualquier aplicación
3. **Seguridad**: Validaciones centralizadas en triggers
4. **Performance**: MariaDB puede compilar y cachear los procedimientos
5. **Mantenibilidad**: Cambios en reglas de negocio sin recompilar Java

---

## 🚀 Ejemplo de Uso Completo

```java
// En tu interfaz gráfica (ej: ProductosPanel.java)

ProductoDAO dao = new ProductoDAO();
Producto p = new Producto();
p.setClaveClasificacion(1);
p.setIdProveedor(5);
p.setNombre("Pintura Blanca Premium");
p.setColor("Blanco");
p.setCapacidad("5.0");
p.setStock(50);
p.setCosto(new BigDecimal("125.75"));
p.setPresentacion("Lata 5L");

try {
    dao.agregar(p);  // Invoca sp_agregar_pintura
    JOptionPane.showMessageDialog(null, "Pintura agregada exitosamente");
} catch (SQLException e) {
    // Error del trigger (ej: "El nombre no puede ser vacío")
    JOptionPane.showMessageDialog(null, 
        "Error: " + e.getMessage(), 
        "Validación de Base de Datos", 
        JOptionPane.ERROR_MESSAGE);
}
```

---

## 📝 Notas Importantes

- **idPintura** es AUTOINCREMENT, no se pasa en `sp_agregar_pintura`
- **capacidad** es `varchar(100)`, no requiere conversión en Java
- **costo** es `decimal(15,2)`, usa `setBigDecimal()` en Java
- Los triggers BEFORE DELETE respetan restricciones de integridad referencial
- Todos los métodos que modifican datos (`agregar`, `actualizar`, `eliminar`) lanzan `SQLException`

---

## 📦 Archivos Generados

- `stored_procedures_pintura.sql` → Procedimientos en SQL
- `ProductoDAO_UPDATED.java` → Referencia completa del DAO actualizado
- `ProductoDAO.java` → Archivo original actualizado en el proyecto

