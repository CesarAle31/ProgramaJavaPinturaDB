# 🚀 INICIO RÁPIDO - Cambio Capacidad VARCHAR → INT

## ⏱️ 5 Pasos en 10 Minutos

### **PASO 1: Backup (Opcional pero Recomendado)**
```sql
-- En HeidiSQL o cliente MySQL
-- Clic derecho en BD → Backup → Guardar archivo
```

---

### **PASO 2: Ejecutar Script SQL** (2 min)
```sql
-- 1. Abre HeidiSQL o MySQL Workbench
-- 2. Selecciona base de datos: IPESAPinturas
-- 3. Nueva pestaña SQL
-- 4. Copia TODO el contenido de este archivo:
--    alter_table_pintura.sql
-- 5. Ejecuta (Ctrl+Enter o botón Run)

-- Esperado: Sin errores, 3 ALTER TABLE completadas
```

---

### **PASO 3: Verificar en BD** (1 min)
```sql
-- Ejecuta estas 3 consultas para verificar:

-- 1. Ver estructura (capacidad debe ser INT)
DESCRIBE pintura;

-- 2. Contar registros con capacidad inválida (debe ser 0)
SELECT COUNT(*) FROM pintura WHERE capacidad <= 0;

-- 3. Ver constraint (debe mostrar varias CHECKs)
SHOW CREATE TABLE pintura;
```

---

### **PASO 4: Actualizar Procedimientos** (1 min)
```sql
-- 1. Nueva pestaña SQL en HeidiSQL
-- 2. Copia TODO el contenido de:
--    stored_procedures_pintura_updated.sql
-- 3. Ejecuta (Ctrl+Enter)

-- Esperado: 4 procedimientos recreados sin errores
```

---

### **PASO 5: Recompilar Java** (5 min)
```bash
# En tu IDE (IntelliJ IDEA, Eclipse, etc.):

# 1. Los archivos ya están actualizados:
#    - Producto.java ✅
#    - ProductoDAO.java ✅

# 2. Rebuild/Recompile proyecto
#    Menú: Build → Rebuild Project (o Ctrl+F9 en IntelliJ)

# 3. Verificar: No debe haber errores rojo de compilación

# 4. Ejecutar la aplicación
#    Menú: Run → Run (o Shift+F10 en IntelliJ)
```

---

## ✨ ¡Listo!

Tu aplicación ahora usa:
- ✅ `capacidad: INT` en BD
- ✅ `capacidad: int` en Java
- ✅ Validaciones a nivel de BD (CHECK constraints)
- ✅ Procedimientos almacenados actualizados

---

## 🆘 Si Algo Falla

### Error: "Column 'capacidad' doesn't support CAST"
→ Ejecutaste los SQL en desorden. Vuelve a ejecutar `alter_table_pintura.sql`

### Error: "Procedure 'sp_agregar_pintura' doesn't exist"
→ No ejecutaste `stored_procedures_pintura_updated.sql`. Hazlo ahora.

### Error de compilación en Java: "incompatible types: String to int"
→ Tus archivos no fueron actualizados correctamente. Copia los cambios de:
   - Producto.java → línea 11
   - ProductoDAO.java → línea 50, 155, 213, 220

### Error: "Received a message of unexpected type"
→ Reinicia IntelliJ IDEA y limpia caché: File → Invalidate Caches

---

## 📞 Validación Final

Prueba esto en tu interfaz gráfica:

```
1. Agregar nueva pintura
   - Nombre: "Test"
   - Color: "Rojo"
   - Capacidad: 5         (ahora es número, no texto)
   - Stock: 10
   - Costo: 100.00
   - Presentación: "Lata 5L"
   ✅ Debe guardarse sin errores

2. Intentar con capacidad negativa
   - Capacidad: -5
   ❌ Debe rechazar con error: "La capacidad debe ser mayor a 0"

3. Buscar pintura
   ✅ Debe encontrar la pintura guardada
```

---

## 📦 Archivos de Referencia

Si necesitas detalles técnicos, consulta:

| Archivo | Para |
|---------|------|
| `RESUMEN_CAMBIOS_CAPACIDAD.md` | Visión completa de cambios |
| `MIGRACION_CAPACIDAD_INT.md` | Detalles técnicos de migración |
| `alter_table_pintura.sql` | Script SQL (ejecutar en BD) |
| `stored_procedures_pintura_updated.sql` | Procedimientos SQL (ejecutar en BD) |

---

## ✅ Antes de Finalizar

- [ ] Script SQL ejecutado en MariaDB sin errores
- [ ] `DESCRIBE pintura;` muestra `capacidad` como INT
- [ ] Procedimientos actualizados sin errores
- [ ] Java recompilado sin errores rojo
- [ ] Aplicación corre y agrega pintura correctamente

¡Listo! 🎉

