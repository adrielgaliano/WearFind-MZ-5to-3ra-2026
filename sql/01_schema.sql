CREATE DATABASE IF NOT EXISTS wearfind
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE wearfind;

CREATE TABLE IF NOT EXISTS usuario (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    username  VARCHAR(255) NULL,
    email     VARCHAR(255) NULL,
    password  VARCHAR(255) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuario_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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