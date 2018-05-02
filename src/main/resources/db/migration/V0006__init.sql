DROP TABLE IF EXISTS users CASCADE;


CREATE TABLE IF NOT EXISTS users
(
  id   BIGSERIAL PRIMARY KEY,

  login    TEXT NOT NULL UNIQUE,
  email    TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL,

  fullname TEXT DEFAULT '',
  avatar   TEXT DEFAULT '',

  points   BIGINT DEFAULT 0
);


CREATE TABLE IF NOT EXISTS waves
(
  id  BIGSERIAL PRIMARY KEY,

  positions  NUMERIC(5, 2)[] NOT NULL,
  delays     NUMERIC(5, 2)[] NOT NULL,
  speed      NUMERIC(5, 2)[] NOT NULL,

  complexity NUMERIC(5, 2) DEFAULT 00.001
);
