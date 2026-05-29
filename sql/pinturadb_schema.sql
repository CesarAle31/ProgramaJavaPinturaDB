CREATE DATABASE IF NOT EXISTS pinturadb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE pinturadb;

CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre_completo VARCHAR(120) NOT NULL,
  usuario VARCHAR(60) NOT NULL UNIQUE,
  contrasena VARCHAR(120) NOT NULL,
  rol VARCHAR(40) NOT NULL DEFAULT 'Vendedor',
  telefono VARCHAR(25),
  acceso VARCHAR(20) NOT NULL DEFAULT 'Activo',
  fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS clientes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre_completo VARCHAR(120) NOT NULL,
  email VARCHAR(120),
  telefono VARCHAR(25),
  direccion VARCHAR(255),
  estado VARCHAR(20) NOT NULL DEFAULT 'Activo',
  fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_clientes_estado (estado)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS proveedores (
  id INT AUTO_INCREMENT PRIMARY KEY,
  razon_social VARCHAR(150) NOT NULL,
  telefono VARCHAR(25),
  direccion VARCHAR(255),
  municipio VARCHAR(100),
  fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS productos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  color VARCHAR(80) NOT NULL,
  linea VARCHAR(80),
  capacidad INT NOT NULL DEFAULT 0,
  presentacion VARCHAR(80),
  costo DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  precio_venta DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  stock INT NOT NULL DEFAULT 0,
  proveedor_id INT,
  fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_productos_stock (stock),
  CONSTRAINT fk_productos_proveedor
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ventas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  folio VARCHAR(30) NOT NULL UNIQUE,
  fecha DATE NOT NULL,
  cliente_id INT,
  usuario_id INT NOT NULL,
  total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
  estado VARCHAR(20) NOT NULL DEFAULT 'Completada',
  fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_ventas_fecha (fecha),
  CONSTRAINT fk_ventas_cliente
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
    ON UPDATE CASCADE
    ON DELETE SET NULL,
  CONSTRAINT fk_ventas_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detalles_venta (
  id INT AUTO_INCREMENT PRIMARY KEY,
  venta_id INT NOT NULL,
  producto_id INT NOT NULL,
  cantidad INT NOT NULL,
  precio_unitario DECIMAL(10, 2) NOT NULL,
  subtotal DECIMAL(10, 2) NOT NULL,
  INDEX idx_detalles_venta (venta_id),
  CONSTRAINT fk_detalles_venta
    FOREIGN KEY (venta_id) REFERENCES ventas(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_detalles_producto
    FOREIGN KEY (producto_id) REFERENCES productos(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB;


INSERT INTO usuarios (nombre_completo, usuario, contrasena, rol, telefono, acceso)
SELECT 'Administrador', 'admin', 'admin', 'Administrador', NULL, 'Activo'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE usuario = 'admin');
