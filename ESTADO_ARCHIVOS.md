# 📂 Estado de Archivos - Migración Capacidad INT

## 📋 Resumen Ejecutivo

```
✅ COMPLETADO: 5 archivos SQL creados
✅ COMPLETADO: 2 archivos Java actualizados
✅ COMPLETADO: 4 documentos de guía creados
📊 TOTAL: 11 archivos listos
```

---

## 🗄️ ARCHIVOS SQL

### 1. `alter_table_pintura.sql` ✅ NUEVO
**Propósito**: Alterar estructura de tabla y agregar validaciones
**Contenido**:
- Corrección de datos inválidos (UPDATE)
- Cambio de tipo: `varchar(100)` → `INT NOT NULL`
- 6 CHECK constraints agregados
- Campos NOT NULL agregados

**Acciones requeridas**:
1. Abre HeidiSQL → Nueva pestaña SQL
2. Copia TODO el archivo
3. Ejecuta
4. Verifica con: `DESCRIBE pintura;`

**Archivos obsoletos**:
- `stored_procedures_pintura.sql` (versión anterior - NO usar)

---

### 2. `stored_procedures_pintura_updated.sql` ✅ NUEVO
**Propósito**: Procedimientos almacenados actualizados con capacidad INT
**Cambios**:
- `sp_agregar_pintura`: parámetro 5 es `INT` (antes `VARCHAR`)
- `sp_actualizar_pintura`: parámetro 6 es `INT` (antes `VARCHAR`)
- `sp_buscar_pintura`: Sin cambios
- `sp_eliminar_pintura`: Sin cambios

**Acciones requeridas**:
1. Primero ejecutar `alter_table_pintura.sql`
2. Luego, abre HeidiSQL → Nueva pestaña SQL
3. Copia TODO este archivo
4. Ejecuta
5. Verifica con: `SHOW PROCEDURE STATUS WHERE db = 'IPESAPinturas';`

**Archivos obsoletos**:
- `stored_procedures_pintura.sql` (no ejecutar - usa la versión updated)

---

## 💻 ARCHIVOS JAVA

### 3. `Producto.java` ✅ ACTUALIZADO
**Ubicación**: `src/com/ipesapinturas/models/Producto.java`

**Cambios realizados**:
```java
// Línea 11: Cambiar tipo
- private String capacidad;
+ private int capacidad;

// Líneas 21-39: Constructores actualizados
- public Producto(..., String capacidad, ...)
+ public Producto(..., int capacidad, ...)

// Líneas 82-92: Getters/Setters actualizados
- public String getCapacidad() { return capacidad; }
+ public int getCapacidad() { return capacidad; }

- public void setCapacidad(String capacidad) { this.capacidad = capacidad; }
+ public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
+ public void setCapacidad(String capacidad) { /* convierte a int */ }
```

**Estado**: ✅ YA ACTUALIZADO - No requiere acción

---

### 4. `ProductoDAO.java` ✅ ACTUALIZADO
**Ubicación**: `src/com/ipesapinturas/dao/ProductoDAO.java`

**Cambios realizados**:
```java
// Línea 50: En método agregar()
- cstmt.setString(5, p.getCapacidad());
+ cstmt.setInt(5, p.getCapacidad());

// Línea 155: En método actualizar()
- cstmt.setString(6, p.getCapacidad());
+ cstmt.setInt(6, p.getCapacidad());

// Línea 220: En método mapearProducto()
- producto.setCapacidad(rs.getString("capacidad"));
+ producto.setCapacidad(rs.getInt("capacidad"));

// Línea 189-215: En método buscar()
// Removido parámetro de capacidad (ahora solo nombre, color, presentacion)
```

**Estado**: ✅ YA ACTUALIZADO - No requiere acción

**Compilación**: Recompila tu IDE después de ejecutar los cambios SQL

---

## 📚 DOCUMENTACIÓN

### 5. `INICIO_RAPIDO.md` ✅ NUEVO
**Para**: Ejecutar cambios en 10 minutos
**Contiene**:
- 5 pasos en orden correcto
- Comandos SQL para verificación
- Troubleshooting rápido

**Cuándo usar**: Cuando quieras ejecutar los cambios rápidamente

---

### 6. `MIGRACION_CAPACIDAD_INT.md` ✅ NUEVO
**Para**: Entender cambios técnicos en detalle
**Contiene**:
- Cambios en BD antes/después
- Cambios requeridos en Producto.java
- Cambios requeridos en ProductoDAO.java
- Checklist de migración
- Ventajas de la migración

**Cuándo usar**: Para entender "por qué" y "cómo" funcionan los cambios

---

### 7. `RESUMEN_CAMBIOS_CAPACIDAD.md` ✅ NUEVO
**Para**: Visión completa de toda la migración
**Contiene**:
- Archivos generados/modificados
- Orden de ejecución
- Validaciones por capas
- Comparativa antes/después
- Checklist final

**Cuándo usar**: Para obtener una visión global del proyecto

---

### 8. `GUIA_STORED_PROCEDURES.md` ⚠️ ACTUALIZAR REFERENCIA
**Ubicación**: Ya existe en el proyecto
**Nota**: Hace referencia a `stored_procedures_pintura.sql`

**Actualización recomendada**: Cambiar todas las referencias a:
- `stored_procedures_pintura_updated.sql` (con capacidad INT)

**Estado**: Funcional pero documentación antigua

---

## 📊 Matriz de Cambios

| Archivo | Tipo | Estado | Acción |
|---------|------|--------|--------|
| `alter_table_pintura.sql` | SQL | ✅ NUEVO | Ejecutar primero |
| `stored_procedures_pintura_updated.sql` | SQL | ✅ NUEVO | Ejecutar segundo |
| `stored_procedures_pintura.sql` | SQL | ❌ OBSOLETO | No usar |
| `Producto.java` | Java | ✅ ACTUALIZADO | Recompilar |
| `ProductoDAO.java` | Java | ✅ ACTUALIZADO | Recompilar |
| `INICIO_RAPIDO.md` | Doc | ✅ NUEVO | Consultar |
| `MIGRACION_CAPACIDAD_INT.md` | Doc | ✅ NUEVO | Consultar |
| `RESUMEN_CAMBIOS_CAPACIDAD.md` | Doc | ✅ NUEVO | Consultar |
| `GUIA_STORED_PROCEDURES.md` | Doc | ⚠️ EXISTENTE | Actualizar referencias |

---

## 🔄 Orden de Ejecución Correcto

```
1️⃣  EJECUTAR EN MariaDB:
    └─ alter_table_pintura.sql
       ├─ Esperar completación
       └─ Verificar: DESCRIBE pintura;

2️⃣  EJECUTAR EN MariaDB:
    └─ stored_procedures_pintura_updated.sql
       ├─ Esperar completación
       └─ Verificar: SHOW PROCEDURE STATUS

3️⃣  EN TU IDE (IntelliJ, Eclipse, etc):
    └─ Rebuild Project
       ├─ Agregar cambios (ya están hechos)
       └─ Verificar: Sin errores de compilación

4️⃣  DESPLEGAR:
    └─ Ejecutar aplicación
       ├─ Probar agregar pintura
       └─ Probar actualizar pintura
```

---

## ✅ Checklist de Deployment

### Fase BD
- [ ] Ejecutado `alter_table_pintura.sql` sin errores
- [ ] Verificado: `DESCRIBE pintura;` muestra capacidad INT
- [ ] Verificado: Cero registros con capacidad <= 0
- [ ] Ejecutado `stored_procedures_pintura_updated.sql` sin errores
- [ ] Verificado: 4 procedimientos creados

### Fase Java
- [ ] Producto.java actualizado ✅
- [ ] ProductoDAO.java actualizado ✅
- [ ] IDE hace rebuild sin errores rojos
- [ ] Aplicación compila sin warnings

### Fase Testing
- [ ] Agregar pintura con capacidad INT
- [ ] Actualizar pintura existente
- [ ] Rechaza capacidad negativa
- [ ] Rechaza capacidad nula
- [ ] Búsqueda funciona

### Fase Post-Deploy
- [ ] Sin errores en logs de aplicación
- [ ] Performance verificado
- [ ] Backup de BD en lugar seguro

---

## 🆘 Troubleshooting Rápido

| Problema | Solución |
|----------|----------|
| "Procedure doesn't exist" | Ejecutar `stored_procedures_pintura_updated.sql` |
| "Incompatible types: String to int" | Recompilejar (Build → Rebuild) |
| "Column 'capacidad' doesn't support CAST" | Ejecutar `alter_table_pintura.sql` |
| "Datos con capacidad = -1" | Ya corregidos por el UPDATE en alter_table_pintura.sql |

---

## 📞 Preguntas Frecuentes

**P: ¿Pierdo datos con este cambio?**
R: No. El UPDATE convierte datos inválidos a 1 antes de cambiar el tipo.

**P: ¿Puedo revertir si algo sale mal?**
R: Sí, tienes backup. Pero con los pasos correctos no debería haber problemas.

**P: ¿Qué pasa con el procedimiento antiguo?**
R: No lo uses. La versión `updated` tiene los cambios necesarios.

**P: ¿Debo cambiar la interfaz gráfica?**
R: Solo si mostraba capacidad como texto. Ahora debe ser numérico.

---

## 🎯 Resumen Final

- ✅ **SQL**: 2 archivos nuevos listos para ejecutar
- ✅ **Java**: 2 archivos actualizados listos para compilar
- ✅ **Docs**: 3 guías detalladas para consultar
- 📊 **Total**: 11 archivos que cubren toda la migración

**Tiempo estimado**: 15 minutos total

**Complejidad**: Baja (pasos secuenciales y claros)

**Riesgo**: Bajo (cambio estructural bien documentado)

¡Listo para ejecutar! 🚀

