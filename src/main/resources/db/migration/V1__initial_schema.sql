-- Creación de tablas iniciales

-- Tabla de Usuarios
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100),
    activo BOOLEAN DEFAULT true,
    refresh_token VARCHAR(255),
    refresh_token_expiry_date TIMESTAMP,
    created_by VARCHAR(50),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP
);

-- Tabla de Roles de Usuario
CREATE TABLE IF NOT EXISTS usuario_rol (
    usuario_id BIGINT NOT NULL,
    roles VARCHAR(20) NOT NULL,
    PRIMARY KEY (usuario_id, roles),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabla de Conductores
CREATE TABLE IF NOT EXISTS conductor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    licencia VARCHAR(20) NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT true,
    created_by VARCHAR(50),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP
);

-- Tabla de Vehículos
CREATE TABLE IF NOT EXISTS vehiculo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(20) NOT NULL UNIQUE,
    capacidad DECIMAL(10,2) NOT NULL,
    activo BOOLEAN DEFAULT true,
    conductor_id BIGINT,
    created_by VARCHAR(50),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP,
    FOREIGN KEY (conductor_id) REFERENCES conductor(id)
);

-- Tabla de Rutas
CREATE TABLE IF NOT EXISTS ruta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    punto_origen VARCHAR(100) NOT NULL,
    punto_destino VARCHAR(100) NOT NULL,
    distancia_km DOUBLE NOT NULL,
    tiempo_estimado_minutos INTEGER NOT NULL,
    activa BOOLEAN DEFAULT true,
    created_by VARCHAR(50),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP
);

-- Tabla de Pedidos
CREATE TABLE IF NOT EXISTS pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descripcion TEXT NOT NULL,
    peso DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    vehiculo_id BIGINT,
    conductor_id BIGINT,
    created_by VARCHAR(50),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP,
    FOREIGN KEY (vehiculo_id) REFERENCES vehiculo(id),
    FOREIGN KEY (conductor_id) REFERENCES conductor(id)
);

-- Tabla de Mantenimientos
CREATE TABLE IF NOT EXISTS mantenimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehiculo_id BIGINT NOT NULL,
    fecha_programada TIMESTAMP NOT NULL,
    fecha_realizada TIMESTAMP,
    tipo VARCHAR(20),
    descripcion TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    observaciones TEXT,
    created_by VARCHAR(50),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP,
    FOREIGN KEY (vehiculo_id) REFERENCES vehiculo(id)
); 