# IPESA Pinturas

Sistema de gestión de pinturas desarrollado en Java Swing con MySQL.

## Requisitos

* Java JDK 17 o superior
* MySQL Server 8.0 o superior
* IntelliJ IDEA (recomendado)
* MySQL Connector/J

## Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/CesarAle31/ProgramaJavaPinturaDB.git
cd ProgramaJavaPinturaDB
```

### 2. Configurar la base de datos

Abrir MySQL y ejecutar:

```sql
SOURCE dbPintura_Completo.sql;
```

o importar el archivo `pinturadb.sql` desde MySQL Workbench.

Verificar que exista la base de datos:

```sql
SHOW DATABASES;
```

### 3. Configurar conexión

Editar:

```text
src/com/ipesapinturas/utils/DatabaseConnection.java
```

y ajustar:

```java
private static final String URL = "jdbc:mysql://localhost:3306/pinturadb";
private static final String USER = "root";
private static final String PASSWORD = "TU_PASSWORD";
```

según la configuración local.

### 4. Configurar dependencias

Asegurarse de que el archivo:

```text
lib/mysql-connector-j-9.7.0.jar
```

esté presente y agregado al proyecto.

## Ejecución

### Desde IntelliJ IDEA (recomendado)

1. Abrir el proyecto.
2. Esperar a que IntelliJ indexe los archivos.
3. Abrir:

```text
src/com/ipesapinturas/ui/MainFrame.java
```

4. Ejecutar:

```java
MainFrame.main()
```

mediante el botón Run (▶).

### Desde terminal

Windows:

```powershell
.\run.bat
```

Linux/Mac:

```bash
chmod +x run.sh
./run.sh
```

## Estructura del proyecto

```text
src/
└── com/ipesapinturas/
    ├── dao/
    ├── models/
    ├── services/
    ├── ui/
    │   └── panels/
    └── utils/
```

## Tecnologías

* Java Swing
* JDBC
* MySQL
* IntelliJ IDEA

