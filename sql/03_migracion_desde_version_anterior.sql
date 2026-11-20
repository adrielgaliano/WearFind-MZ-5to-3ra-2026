-- ============================================================
--  WearFind · Migración (SOLO si ya tenés datos de la versión anterior)
--
--  La versión anterior guardaba el dueño en prenda.usuario_id.
--  Este script lo pasa a la tabla intermedia usuario_prenda.
--  Hacer backup antes:  mysqldump -u root -p wearfind > backup.sql
-- ============================================================
USE wearfind;

-- 1) Crear la tabla intermedia si todavía no existe
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

-- 2) Copiar las relaciones existentes (idempotente)
INSERT IGNORE INTO usuario_prenda (usuario_id, prenda_id)
SELECT usuario_id, id
FROM prenda
WHERE usuario_id IS NOT NULL;

-- 3) Quitar la columna vieja. Primero hay que averiguar el nombre de su FK
--    (Hibernate la nombra automáticamente, ej. FK1abc...):
--      SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE
--      WHERE TABLE_SCHEMA='wearfind' AND TABLE_NAME='prenda'
--        AND COLUMN_NAME='usuario_id' AND REFERENCED_TABLE_NAME IS NOT NULL;
--    Luego (reemplazá <nombre_fk>):
--      ALTER TABLE prenda DROP FOREIGN KEY <nombre_fk>;
--      ALTER TABLE prenda DROP COLUMN usuario_id;
