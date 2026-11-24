USE wearfind;

-- Contraseña de los usuarios de prueba: 123456
INSERT INTO usuario (username, email, password) VALUES
    ('lucia',  'lucia@wearfind.com',  'He9oRVu37tG5sOrfsPIglg==:R7TLcjH8iHUPefdxikqI9j9KW3seeMgDKdvxK901NUo='),
    ('martin', 'martin@wearfind.com', 'TZyp+lmQn6yl/PJiy9n6dQ==:+QYGXcY34iSnyuNs/pAsOUEXc0uf5SsjFg5EQeCP+GY='),
    ('sofia',  'sofia@wearfind.com',  'NHoiaQ+jUfJPAfXWNM2MWQ==:Xgqqe67B5sG2oDwOhK6MYRTw9ONBjqcaI7YBGwMgZw4=');

INSERT INTO prenda (nombre, descripcion, color, estacion, talle, marca, url_imagen, favorito) VALUES
    ('Campera de jean',     'Campera clásica de jean con botones metálicos', 'Azul',   'Otoño',       'M',     'Levis',     NULL, 1),
    ('Remera básica',       'Remera de algodón cuello redondo',               'Blanco', 'Todo el año', 'S',     'Uniqlo',    NULL, 0),
    ('Sweater de lana',     'Sweater grueso tejido, ideal para el frío',      'Cámel',  'Invierno',    'L',     'Zara',      NULL, 1),
    ('Short de lino',       'Short liviano con cintura elástica',             'Beige',  'Verano',      'M',     'Mango',   NULL, 0),
    ('Vestido floral',      'Vestido midi estampado',                         'Rosa',   'Primavera',   'S',     'H&M',       NULL, 1),
    ('Pantalón cargo',      'Pantalón cargo con bolsillos laterales',         'Verde',  'Todo el año', 'L',     'Bershka',   NULL, 0),
    ('Zapatillas urbanas',  'Zapatillas de cuero blanco',                     'Blanco', 'Todo el año', 'Único', 'Adidas',    NULL, 1),
    ('Bufanda tejida',      'Bufanda larga de lana',                          'Bordó',  'Invierno',    'Único', NULL,        NULL, 0),
    ('Campera de cuero',    'Campera negra estilo motociclista',              'Negro',  'Otoño',       'M',     'Pull&Bear', NULL, 0),
    ('Musculosa deportiva', 'Musculosa de secado rápido',                     'Gris',   'Verano',      'M',     'Nike',      NULL, 0);

INSERT INTO usuario_prenda (usuario_id, prenda_id)
SELECT u.id, p.id
FROM (
    SELECT 'lucia'  AS username, 'Campera de jean'    AS nombre UNION ALL
    SELECT 'lucia',              'Remera básica'                UNION ALL
    SELECT 'lucia',              'Vestido floral'               UNION ALL
    SELECT 'lucia',              'Zapatillas urbanas'           UNION ALL
    SELECT 'martin',             'Sweater de lana'              UNION ALL
    SELECT 'martin',             'Pantalón cargo'               UNION ALL
    SELECT 'martin',             'Campera de cuero'             UNION ALL
    SELECT 'martin',             'Zapatillas urbanas'           UNION ALL
    SELECT 'sofia',              'Short de lino'                UNION ALL
    SELECT 'sofia',              'Remera básica'
) a
JOIN usuario u ON u.username = a.username
JOIN prenda  p ON p.nombre   = a.nombre;