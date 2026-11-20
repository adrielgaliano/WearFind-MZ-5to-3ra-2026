-- ============================================================
--  WearFind · Esquema MySQL 8
--  Ejecutar con un usuario con permisos de CREATE (ej. root):
--    mysql -u root -p < sql/01_schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS wearfind
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE wearfind;

-- ------------------------------------------------------------
--  usuario
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    username  VARCHAR(255) NULL,
    email     VARCHAR(255) NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
--  prenda  (catálogo de prendas; ya NO guarda el usuario)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS prenda (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(255)  NULL,
    descripcion  VARCHAR(255)  NULL,
    color        VARCHAR(255)  NULL,
    estacion     VARCHAR(255)  NULL,
    talle        VARCHAR(255)  NULL,
    marca        VARCHAR(255)  NULL,
    url_imagen   VARCHAR(1000) NULL,
    favorito     TINYINT(1)    NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
--  usuario_prenda  (tabla intermedia, relación N:M)
--  Un usuario puede tener muchas prendas y una prenda puede
--  estar asignada a varios usuarios. El par es único.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario_prenda (
    id                BIGINT      NOT NULL AUTO_INCREMENT,
    usuario_id        BIGINT      NOT NULL,
    prenda_id         BIGINT      NOT NULL,
    fecha_asignacion  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_usuario_prenda UNIQUE (usuario_id, prenda_id),
    KEY idx_up_prenda (prenda_id),
    CONSTRAINT fk_up_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_up_prenda  FOREIGN KEY (prenda_id)  REFERENCES prenda  (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
