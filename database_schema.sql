DROP DATABASE IF EXISTS imc_system_db;
CREATE DATABASE imc_system_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE imc_system_db;

-- 1. TABLA: clasificaciones_imc
-- Catálogo de clasificaciones y rangos según la OMS

CREATE TABLE clasificaciones_imc (
    id_clasificacion INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre_categoria VARCHAR(50) NOT NULL,
    limite_inferior DECIMAL(4,2) NOT NULL,
    limite_superior DECIMAL(4,2) NOT NULL,
    color_hex VARCHAR(10) NOT NULL,
    nivel_riesgo VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

-- 2. TABLA: usuarios
-- Información demográfica, credenciales y estatura base del usuario
-- Restricciones:
-- - Estatura válida: entre 1.00 m y 2.50 m
-- - Edad mínima: 15 años

CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    edad INT NOT NULL CHECK (edad >= 15),
    sexo ENUM('M', 'F') NOT NULL,
    estatura DECIMAL(4,2) NOT NULL CHECK (estatura >= 1.00 AND estatura <= 2.50),
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_username (username)
) ENGINE=InnoDB;

-- 3. TABLA: registros_imc
-- Historial cronológico de cálculos de IMC
-- Restricciones:
-- - Masa corporal (peso) > 0 (no se aceptan valores 0 o negativos)
-- - Consumida mediante Servicio Web REST

CREATE TABLE registros_imc (
    id_registro INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    masa_corporal DECIMAL(5,2) NOT NULL CHECK (masa_corporal > 0),
    estatura DECIMAL(4,2) NOT NULL CHECK (estatura >= 1.00 AND estatura <= 2.50),
    valor_imc DECIMAL(4,2) NOT NULL,
    clasificacion VARCHAR(50) NOT NULL,
    fecha_medicion DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_registros_usuario 
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_reg_usuario (id_usuario),
    INDEX idx_reg_fecha (fecha_medicion)
) ENGINE=InnoDB;


-- INSERCIÓN DE DATOS INICIALES (CATÁLOGO OMS Y PRUEBAS)


INSERT INTO clasificaciones_imc 
(codigo, nombre_categoria, limite_inferior, limite_superior, color_hex, nivel_riesgo) 
VALUES
('BAJO_PESO', 'Bajo Peso', 0.00, 18.49, '#38bdf8', 'Riesgo nutricional aumentado'),
('NORMAL', 'Peso Normal (Saludable)', 18.50, 24.99, '#22c55e', 'Riesgo bajo / promedio'),
('SOBREPESO', 'Sobrepeso', 25.00, 29.99, '#eab308', 'Riesgo cardiovascular aumentado'),
('OBESIDAD_I', 'Obesidad Grado I', 30.00, 34.99, '#f97316', 'Riesgo moderado'),
('OBESIDAD_II', 'Obesidad Grado II', 35.00, 39.99, '#ef4444', 'Riesgo severo'),
('OBESIDAD_III', 'Obesidad Grado III', 40.00, 99.99, '#991b1b', 'Riesgo muy alto / crítico');

-- Usuario de prueba (Edad: 24 años >= 15, Estatura: 1.75 m [1.0m - 2.5m])
INSERT INTO usuarios 
(nombre_completo, username, password_hash, edad, sexo, estatura) 
VALUES 
('Manuel Esau Noriega Rodriguez', 'menr97', 'admin123', 29, 'M', 1.75);

-- Histórico de cálculos iniciales
INSERT INTO registros_imc 
(id_usuario, masa_corporal, estatura, valor_imc, clasificacion, fecha_medicion) 
VALUES
(1, 80.20, 1.75, 26.19, 'Sobrepeso', '2025-01-15 08:00:00'),
(1, 77.80, 1.75, 25.40, 'Sobrepeso', '2025-02-01 09:15:00'),
(1, 75.50, 1.75, 24.65, 'Peso Normal (Saludable)', '2025-02-23 08:30:00');
