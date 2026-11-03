-- Esquema de la práctica JDBC (plataforma de música)
-- Pensado para H2. Se ejecuta al arrancar la aplicación; es idempotente.

-- Raíz: Usuario
CREATE TABLE IF NOT EXISTS usuario (
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email  VARCHAR(150) NOT NULL UNIQUE
);

-- Raíz: Artista
CREATE TABLE IF NOT EXISTS artista (
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    pais   VARCHAR(60) NOT NULL
);

-- Raíz: Cancion (1 artista : N canciones)
CREATE TABLE IF NOT EXISTS cancion (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo     VARCHAR(150) NOT NULL,
    duracion   INT NOT NULL CHECK (duracion > 0),
    artista_id BIGINT NOT NULL,
    FOREIGN KEY (artista_id) REFERENCES artista(id) ON DELETE CASCADE
);

-- Raíz: Playlist (1 usuario : N playlists)
CREATE TABLE IF NOT EXISTS playlist (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300),
    usuario_id  BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Parte interna del agregado Playlist (sin repositorio propio)
-- Resuelve la relación N:M Playlist-Cancion y guarda la posición.
CREATE TABLE IF NOT EXISTS playlist_cancion (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    posicion    INT NOT NULL CHECK (posicion >= 0),
    playlist_id BIGINT NOT NULL,
    cancion_id  BIGINT NOT NULL,
    FOREIGN KEY (playlist_id) REFERENCES playlist(id) ON DELETE CASCADE,
    FOREIGN KEY (cancion_id)  REFERENCES cancion(id)  ON DELETE CASCADE
);
