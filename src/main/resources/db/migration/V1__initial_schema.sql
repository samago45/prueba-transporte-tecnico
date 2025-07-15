-- Tabla de Usuarios
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN DEFAULT true,
    refresh_token VARCHAR(255),
    refresh_token_expiry_date TIMESTAMP,
    created_by VARCHAR(50),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP
);

-- Tabla de Conductores
CREATE TABLE conductor (
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
CREATE TABLE vehiculo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(10) NOT NULL UNIQUE,
    capacidad DECIMAL(10,2) NOT NULL,
    activo BOOLEAN DEFAULT true,
    conductor_id BIGINT,
    created_by VARCHAR(50),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP,
    FOREIGN KEY (conductor_id) REFERENCES conductor(id)
);

-- Tabla de Pedidos
CREATE TABLE pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descripcion TEXT NOT NULL,
    peso DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
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
CREATE TABLE mantenimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehiculo_id BIGINT NOT NULL,
    fecha_programada TIMESTAMP NOT NULL,
    fecha_realizada TIMESTAMP,
    tipo VARCHAR(20) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(20) NOT NULL,
    observaciones TEXT,
    created_by VARCHAR(50),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP,
    FOREIGN KEY (vehiculo_id) REFERENCES vehiculo(id)
);

-- Tabla de Rutas
CREATE TABLE ruta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    punto_origen VARCHAR(255) NOT NULL,
    punto_destino VARCHAR(255) NOT NULL,
    distancia_km DOUBLE NOT NULL,
    tiempo_estimado_minutos INTEGER NOT NULL,
    activa BOOLEAN DEFAULT true,
    created_by VARCHAR(50),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(50),
    last_modified_date TIMESTAMP
); 