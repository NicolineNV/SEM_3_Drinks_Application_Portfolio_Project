-- ============================================================
-- SEM 3 Drinks Application Portfolio Project
-- PostgreSQL schema
-- ============================================================

DROP TABLE IF EXISTS user_favorite_cocktail CASCADE;
DROP TABLE IF EXISTS cocktail CASCADE;
DROP TABLE IF EXISTS spirit_flavor_tag CASCADE;
DROP TABLE IF EXISTS liqueur_flavor_tag CASCADE;
DROP TABLE IF EXISTS mixer_flavor_tag CASCADE;
DROP TABLE IF EXISTS syrup_flavor_tag CASCADE;
DROP TABLE IF EXISTS garnish CASCADE;
DROP TABLE IF EXISTS syrup CASCADE;
DROP TABLE IF EXISTS mixer CASCADE;
DROP TABLE IF EXISTS liqueur CASCADE;
DROP TABLE IF EXISTS spirit CASCADE;
DROP TABLE IF EXISTS flavor_tag CASCADE;
DROP TABLE IF EXISTS cocktail_family CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
                       id            BIGSERIAL PRIMARY KEY,
                       username      VARCHAR(50)  NOT NULL UNIQUE,
                       email         VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role          VARCHAR(10)  NOT NULL DEFAULT 'USER'
                           CHECK (role IN ('USER', 'ADMIN')),
                       created_at    TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE cocktail_family (
                                 id          BIGSERIAL PRIMARY KEY,
                                 name        VARCHAR(50)  NOT NULL UNIQUE,
                                 description TEXT
);

CREATE TABLE flavor_tag (
                            id   BIGSERIAL PRIMARY KEY,
                            name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE spirit (
                        id          BIGSERIAL PRIMARY KEY,
                        name        VARCHAR(100) NOT NULL UNIQUE,
                        description TEXT
);

CREATE TABLE liqueur (
                         id          BIGSERIAL PRIMARY KEY,
                         name        VARCHAR(100) NOT NULL UNIQUE,
                         description TEXT
);

CREATE TABLE mixer (
                       id          BIGSERIAL PRIMARY KEY,
                       name        VARCHAR(100) NOT NULL UNIQUE,
                       description TEXT
);

CREATE TABLE syrup (
                       id          BIGSERIAL PRIMARY KEY,
                       name        VARCHAR(100) NOT NULL UNIQUE,
                       description TEXT
);

CREATE TABLE garnish (
                         id          BIGSERIAL PRIMARY KEY,
                         name        VARCHAR(100) NOT NULL UNIQUE,
                         description TEXT
);

CREATE TABLE spirit_flavor_tag (
                                   spirit_id     BIGINT  NOT NULL REFERENCES spirit(id)     ON DELETE CASCADE,
                                   flavor_tag_id BIGINT  NOT NULL REFERENCES flavor_tag(id) ON DELETE CASCADE,
                                   intensity     SMALLINT NOT NULL CHECK (intensity BETWEEN 1 AND 5),
                                   PRIMARY KEY (spirit_id, flavor_tag_id)
);

CREATE TABLE liqueur_flavor_tag (
                                    liqueur_id    BIGINT  NOT NULL REFERENCES liqueur(id)    ON DELETE CASCADE,
                                    flavor_tag_id BIGINT  NOT NULL REFERENCES flavor_tag(id) ON DELETE CASCADE,
                                    intensity     SMALLINT NOT NULL CHECK (intensity BETWEEN 1 AND 5),
                                    PRIMARY KEY (liqueur_id, flavor_tag_id)
);

CREATE TABLE mixer_flavor_tag (
                                  mixer_id      BIGINT  NOT NULL REFERENCES mixer(id)      ON DELETE CASCADE,
                                  flavor_tag_id BIGINT  NOT NULL REFERENCES flavor_tag(id) ON DELETE CASCADE,
                                  intensity     SMALLINT NOT NULL CHECK (intensity BETWEEN 1 AND 5),
                                  PRIMARY KEY (mixer_id, flavor_tag_id)
);

CREATE TABLE syrup_flavor_tag (
                                  syrup_id      BIGINT  NOT NULL REFERENCES syrup(id)      ON DELETE CASCADE,
                                  flavor_tag_id BIGINT  NOT NULL REFERENCES flavor_tag(id) ON DELETE CASCADE,
                                  intensity     SMALLINT NOT NULL CHECK (intensity BETWEEN 1 AND 5),
                                  PRIMARY KEY (syrup_id, flavor_tag_id)
);

CREATE TABLE cocktail (
                          id                 BIGSERIAL PRIMARY KEY,
                          name               VARCHAR(100) NOT NULL UNIQUE,
                          description        TEXT,
                          instructions       TEXT         NOT NULL,
                          cocktail_family_id BIGINT       NOT NULL REFERENCES cocktail_family(id) ON DELETE RESTRICT,
                          spirit_id          BIGINT       REFERENCES spirit(id)                   ON DELETE SET NULL,
                          liqueur_id         BIGINT       REFERENCES liqueur(id)                  ON DELETE SET NULL,
                          mixer_id           BIGINT       REFERENCES mixer(id)                    ON DELETE SET NULL,
                          syrup_id           BIGINT       REFERENCES syrup(id)                    ON DELETE SET NULL,
                          garnish_id         BIGINT       REFERENCES garnish(id)                  ON DELETE SET NULL,
                          created_at         TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE user_favorite_cocktail (
                                        user_id     BIGINT    NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
                                        cocktail_id BIGINT    NOT NULL REFERENCES cocktail(id) ON DELETE CASCADE,
                                        saved_at    TIMESTAMP NOT NULL DEFAULT now(),
                                        PRIMARY KEY (user_id, cocktail_id)
);

CREATE INDEX idx_cocktail_family      ON cocktail(cocktail_family_id);
CREATE INDEX idx_cocktail_spirit      ON cocktail(spirit_id);
CREATE INDEX idx_favorite_user        ON user_favorite_cocktail(user_id);
