
drop index if exists users_login_idx;
drop index if exists users_email_idx;
drop index if exists solo_games_user_id_idx;

CREATE TABLE IF NOT EXISTS users
(
  id   BIGSERIAL PRIMARY KEY,

  login    TEXT NOT NULL UNIQUE,
  email    TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL,

  fullname TEXT DEFAULT '',
  avatar   TEXT DEFAULT '',

  points   BIGINT DEFAULT 0,
  coins    BIGINT DEFAULT 1000
);

create table if not exists friends
(
    id1 bigint references users(id),
    id2 bigint references users(id),

    unique (id1, id2)
);

create table if not exists friendship_requests
(
    id bigserial primary key,

    sender bigint references users(id),
    friend bigint references users(id),

    unique (sender, friend)
);

CREATE TABLE IF NOT EXISTS mobs
(
  id bigserial primary key,

  level int default 1,

  name        text not null,
  description text default ''
);


CREATE TABLE IF NOT EXISTS rounds
(
  id  BIGSERIAL PRIMARY KEY,

  parent     bigint default 0,

  waves      BIGINT[] NOT NULL,

  complexity BIGINT DEFAULT 0
);


CREATE TABLE IF NOT EXISTS solo_games
(
  id  BIGSERIAL PRIMARY KEY,

  datetime   TIMESTAMP WITH TIME ZONE ,

  user_id    BIGINT NOT NULL REFERENCES users(id),
  points     BIGINT NOT NULL DEFAULT 0,

  rounds     BIGINT[]
);

CREATE TABLE IF NOT EXISTS multi_games
(
  id  BIGSERIAL PRIMARY KEY,

  datetime   TIMESTAMP WITH TIME ZONE ,

  users_id    BIGINT[] NOT NULL,
  points     BIGINT[] NOT NULL,

  rounds     BIGINT[]
);

create index if not exists users_login_idx on users(LOWER(login));
create index if not exists users_email_idx on users(LOWER(email));

create index if not exists solo_games_user_id_idx on solo_games(user_id);