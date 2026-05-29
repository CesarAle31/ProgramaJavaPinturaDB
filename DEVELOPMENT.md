# Configuración local de la base de datos

La aplicación usa MySQL local mediante JDBC. La configuración ya no está fija en el código: se puede cambiar sin recompilar.

## 1. Crear o importar la base de datos

Si ya tienes el archivo `descripcionTablasPinturadb.txt` o un respaldo SQL de `pinturadb`, impórtalo en tu MySQL local. Si necesitas crear una base compatible con el proyecto, ejecuta:

```bash
mysql -u root -p < sql/pinturadb_schema.sql
```

El script crea la base `pinturadb`, las tablas que usa el sistema y un usuario inicial:

- Usuario: `admin`
- Contraseña: `admin`

## 2. Configurar credenciales locales

Copia el archivo de ejemplo:

```bash
cp db.properties.example db.properties
```

Edita `db.properties` con los datos de tu MySQL local:

```properties
db.host=localhost
db.port=3306
db.name=pinturadb
db.user=root
db.password=tu_contrasena
```

También puedes configurar con variables de entorno sin crear archivo:

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=pinturadb
export DB_USER=root
export DB_PASSWORD=tu_contrasena
```

Prioridad de configuración, de mayor a menor:

1. Propiedades de Java `-Ddb.*`.
2. Variables de entorno `DB_*`.
3. Archivo `db.properties`.
4. Valores por defecto del proyecto.

## 3. Compilar y ejecutar

Linux/macOS/Git Bash:

```bash
./run.sh
```

Windows:

```bat
run.bat
```

Si prefieres compilar manualmente:

```bash
javac -cp "lib/*" -d bin src/com/ipesapinturas/models/*.java src/com/ipesapinturas/utils/*.java src/com/ipesapinturas/dao/*.java src/com/ipesapinturas/services/*.java src/com/ipesapinturas/ui/*.java src/com/ipesapinturas/ui/panels/*.java
java -cp "bin:lib/*" com.ipesapinturas.ui.LoginFrame
```
