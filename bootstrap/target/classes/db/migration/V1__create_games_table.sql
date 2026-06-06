CREATE TABLE games (
    id                UUID            PRIMARY KEY,
    title             VARCHAR(255)    NOT NULL,
    designer          VARCHAR(255)    NOT NULL,
    min_players       INTEGER         NOT NULL,
    max_players       INTEGER         NOT NULL,
    min_age           INTEGER         NOT NULL,
    duration_minutes  INTEGER         NOT NULL,
    price_amount      NUMERIC(12, 2)  NOT NULL,
    price_currency    CHAR(3)         NOT NULL,
    created_at        TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT games_min_players_chk      CHECK (min_players >= 1),
    CONSTRAINT games_max_players_chk      CHECK (max_players >= min_players),
    CONSTRAINT games_min_age_chk          CHECK (min_age >= 0),
    CONSTRAINT games_duration_chk         CHECK (duration_minutes > 0),
    CONSTRAINT games_price_amount_chk     CHECK (price_amount >= 0)
);

CREATE INDEX games_title_idx ON games (LOWER(title));
