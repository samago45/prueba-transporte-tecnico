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
-- Usar una subconsulta directa para evitar problemas con variables
INSERT INTO usuario_rol (usuario_id, roles)
SELECT id, 'ADMIN' 
FROM usuario 
WHERE username = 'admin'
AND NOT EXISTS (
    SELECT 1 
    FROM usuario_rol ur 
    WHERE ur.usuario_id = usuario.id 
    AND ur.roles = 'ADMIN'
); 