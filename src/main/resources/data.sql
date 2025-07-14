-- Insertar un usuario administrador con contraseña 'password' hasheada con BCrypt
-- El hash corresponde a la contraseña "password"
INSERT INTO usuario (username, password) VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqR2e5RzTTNhrnmLTYx.b4BOswOK') ON DUPLICATE KEY UPDATE password = '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqR2e5RzTTNhrnmLTYx.b4BOswOK';

-- Asignar el rol de ADMIN al usuario 'admin'
-- Primero, obtenemos el ID del usuario 'admin'
SET @admin_id = (SELECT id FROM usuario WHERE username = 'admin');

-- Insertar el rol, evitando duplicados
INSERT INTO usuario_rol (usuario_id, roles)
SELECT @admin_id, 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM usuario_rol WHERE usuario_id = @admin_id AND roles = 'ROLE_ADMIN'); 