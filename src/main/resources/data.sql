-- Insertar usuario administrador por defecto
-- Contraseña: 'admin123' (hasheada con BCrypt)
INSERT INTO usuario (username, password, nombre, email, activo, created_by, created_date, last_modified_by, last_modified_date)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'Administrador', 'admin@transporte.com', true, 'SYSTEM', NOW(), 'SYSTEM', NOW())
ON DUPLICATE KEY UPDATE
    password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa',
    nombre = 'Administrador',
    email = 'admin@transporte.com',
    activo = true,
    last_modified_by = 'SYSTEM',
    last_modified_date = NOW();

-- Asignar rol ADMIN al usuario administrador
INSERT INTO usuario_rol (usuario_id, roles)
SELECT id, 'ADMIN' FROM usuario WHERE username = 'admin'
AND NOT EXISTS (
    SELECT 1 FROM usuario_rol ur WHERE ur.usuario_id = usuario.id AND ur.roles = 'ADMIN'
); 