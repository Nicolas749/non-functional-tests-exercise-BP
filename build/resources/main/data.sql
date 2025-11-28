-- Datos iniciales para testing y desarrollo
-- Se cargan automáticamente al iniciar la aplicación con H2

INSERT INTO clientes (nombre, email, telefono, fecha_registro, activo) VALUES
('Carlos Rodríguez', 'carlos@example.com', '0991234567', CURRENT_TIMESTAMP, true),
('Ana María Torres', 'ana.torres@example.com', '0997654321', CURRENT_TIMESTAMP, true),
('Pedro Sánchez', 'pedro.sanchez@example.com', '0993456789', CURRENT_TIMESTAMP, true),
('Laura Martínez', 'laura.martinez@example.com', '0999876543', CURRENT_TIMESTAMP, true),
('Miguel Ángel López', 'miguel.lopez@example.com', '0992345678', CURRENT_TIMESTAMP, true);
