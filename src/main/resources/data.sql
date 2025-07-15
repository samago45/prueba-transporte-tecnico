-- Insertar un usuario administrador con contraseña 'password' hasheada con BCrypt
-- El hash corresponde a la contraseña "password"
INSERT INTO usuario (username, password, nombre, email, activo) 
VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqR2e5RzTTNhrnmLTYx.b4BOswOK', 'Administrador', 'admin@example.com', true) 
ON DUPLICATE KEY UPDATE 
    password = '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqR2e5RzTTNhrnmLTYx.b4BOswOK',
    nombre = 'Administrador',
    email = 'admin@example.com',
    activo = true;

-- Asignar el rol de ADMIN al usuario 'admin'
-- Primero, obtenemos el ID del usuario 'admin'
SET @admin_id = (SELECT id FROM usuario WHERE username = 'admin');

-- Insertar el rol, evitando duplicados
INSERT INTO usuario_rol (usuario_id, roles)
SELECT @admin_id, 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM usuario_rol WHERE usuario_id = @admin_id AND roles = 'ADMIN'); 