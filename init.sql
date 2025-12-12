DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS match_protocols CASCADE;
DROP TABLE IF EXISTS matches CASCADE;
DROP TABLE IF EXISTS tournament_registrations CASCADE;
DROP TABLE IF EXISTS weight_classes CASCADE;
DROP TABLE IF EXISTS tournaments CASCADE;
DROP TABLE IF EXISTS rankings CASCADE;
DROP TABLE IF EXISTS referees CASCADE;
DROP TABLE IF EXISTS athletes CASCADE;
DROP TABLE IF EXISTS users CASCADE;


DROP TYPE IF EXISTS hand_type CASCADE;
DROP TYPE IF EXISTS match_result_type CASCADE;
DROP TYPE IF EXISTS tournament_status_type CASCADE;
DROP TYPE IF EXISTS gender_type CASCADE;


CREATE TYPE hand_type AS ENUM ('LEFT', 'RIGHT');
CREATE TYPE match_result_type AS ENUM ('PIN', 'FOULS', 'RUNNING_FOULS', 'REFEREE_DECISION', 'INJURY');
CREATE TYPE tournament_status_type AS ENUM ('ANNOUNCED', 'REGISTRATION', 'ONGOING', 'FINISHED', 'CANCELED');
CREATE TYPE gender_type AS ENUM ('MALE', 'FEMALE');


CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER', 'REFEREE')),
    full_name VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE athletes (
    athlete_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    nickname VARCHAR(50),
    country_code VARCHAR(3) NOT NULL,
    city VARCHAR(100),
    birth_date DATE NOT NULL,
    gender gender_type NOT NULL,
    height_cm INT CHECK (height_cm > 100 AND height_cm < 250),
    weight_kg DECIMAL(5,2) CHECK (weight_kg > 30 AND weight_kg < 250),
    biceps_cm DECIMAL(4,1),
    forearm_cm DECIMAL(4,1),
    bio TEXT
);


CREATE TABLE referees (
    referee_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    certification_level VARCHAR(50) DEFAULT 'National',
    license_number VARCHAR(50) UNIQUE,
    years_experience INT DEFAULT 0 CHECK (years_experience >= 0),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE tournaments (
    tournament_id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    country VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL CHECK (end_date >= start_date),
    prize_pool DECIMAL(12, 2) DEFAULT 0.00 CHECK (prize_pool >= 0),
    status tournament_status_type DEFAULT 'ANNOUNCED',
    created_by INT REFERENCES users(user_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE weight_classes (
    class_id SERIAL PRIMARY KEY,
    tournament_id INT NOT NULL REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    class_name VARCHAR(50) NOT NULL,
    max_weight_kg DECIMAL(5,2) NOT NULL,
    gender gender_type NOT NULL,
    hand hand_type NOT NULL,
    entry_fee DECIMAL(10, 2) DEFAULT 0.00,
    UNIQUE(tournament_id, class_name, hand)
);


CREATE TABLE tournament_registrations (
    registration_id SERIAL PRIMARY KEY,
    tournament_id INT NOT NULL REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    athlete_id INT NOT NULL REFERENCES athletes(athlete_id) ON DELETE CASCADE,
    class_id INT REFERENCES weight_classes(class_id),
    weigh_in_weight DECIMAL(5,2),
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'DISQUALIFIED')),
    is_paid BOOLEAN DEFAULT FALSE,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE matches (
    match_id SERIAL PRIMARY KEY,
    tournament_id INT NOT NULL REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    class_id INT NOT NULL REFERENCES weight_classes(class_id),
    athlete_a_id INT NOT NULL REFERENCES athletes(athlete_id),
    athlete_b_id INT NOT NULL REFERENCES athletes(athlete_id),
    winner_id INT REFERENCES athletes(athlete_id),
    stage VARCHAR(50) NOT NULL,
    table_number INT CHECK (table_number > 0),
    match_number INT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    CONSTRAINT check_winner_participant CHECK (winner_id IS NULL OR winner_id = athlete_a_id OR winner_id = athlete_b_id)
);


CREATE TABLE match_protocols (
    protocol_id SERIAL PRIMARY KEY,
    match_id INT NOT NULL UNIQUE REFERENCES matches(match_id) ON DELETE CASCADE,
    referee_id INT REFERENCES referees(referee_id) ON DELETE SET NULL,
    winning_hand hand_type NOT NULL,
    result_type match_result_type NOT NULL,
    duration_seconds INT CHECK (duration_seconds >= 0),
    fouls_a INT DEFAULT 0 CHECK (fouls_a >= 0),
    fouls_b INT DEFAULT 0 CHECK (fouls_b >= 0),
    is_strap_match BOOLEAN DEFAULT FALSE,
    video_replay_url VARCHAR(255),
    notes TEXT
);


CREATE TABLE rankings (
    ranking_id SERIAL PRIMARY KEY,
    athlete_id INT NOT NULL REFERENCES athletes(athlete_id) ON DELETE CASCADE,
    hand hand_type NOT NULL,
    points INT DEFAULT 1000 CHECK (points >= 0),
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    tournaments_played INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(athlete_id, hand)
);


CREATE TABLE audit_log (
    log_id SERIAL PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    record_id INT NOT NULL,
    action_type VARCHAR(10) NOT NULL CHECK (action_type IN ('INSERT', 'UPDATE', 'DELETE')),
    old_value TEXT,
    new_value TEXT,
    changed_by INT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45)
);

CREATE OR REPLACE VIEW v_athlete_summary AS
SELECT
    a.athlete_id,
    u.full_name,
    a.country_code,
    r.hand,
    r.points,
    r.wins,
    r.losses,
    CASE
        WHEN (r.wins + r.losses) = 0 THEN 0
        ELSE ROUND((r.wins::DECIMAL / (r.wins + r.losses)) * 100, 2)
    END as win_rate
FROM athletes a
JOIN users u ON a.user_id = u.user_id
LEFT JOIN rankings r ON a.athlete_id = r.athlete_id;

CREATE OR REPLACE VIEW v_match_schedule AS
SELECT
    m.match_id,
    t.name as tournament_name,
    wc.class_name,
    m.stage,
    u1.full_name as athlete_a,
    u2.full_name as athlete_b,
    m.start_time,
    m.table_number
FROM matches m
JOIN tournaments t ON m.tournament_id = t.tournament_id
JOIN weight_classes wc ON m.class_id = wc.class_id
JOIN athletes a1 ON m.athlete_a_id = a1.athlete_id
JOIN users u1 ON a1.user_id = u1.user_id
JOIN athletes a2 ON m.athlete_b_id = a2.athlete_id
JOIN users u2 ON a2.user_id = u2.user_id
WHERE m.winner_id IS NULL
ORDER BY m.start_time;

CREATE OR REPLACE VIEW v_referee_workload AS
SELECT
    r.referee_id,
    u.full_name,
    COUNT(mp.protocol_id) as matches_judged,
    SUM(mp.fouls_a + mp.fouls_b) as total_fouls_called
FROM referees r
JOIN users u ON r.user_id = u.user_id
LEFT JOIN match_protocols mp ON r.referee_id = mp.referee_id
GROUP BY r.referee_id, u.full_name;


CREATE OR REPLACE FUNCTION calculate_bmi(weight_kg DECIMAL, height_cm INT)
RETURNS DECIMAL AS $$
BEGIN
    IF height_cm IS NULL OR height_cm = 0 THEN
        RETURN 0;
    END IF;
    RETURN ROUND(weight_kg / ((height_cm::DECIMAL / 100) ^ 2), 2);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION get_athlete_history(search_athlete_id INT)
RETURNS TABLE (
    match_date TIMESTAMP,
    opponent_name VARCHAR,
    result VARCHAR,
    stage VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        m.end_time,
        CASE
            WHEN m.athlete_a_id = search_athlete_id THEN u2.full_name
            ELSE u1.full_name
        END as opponent_name,
        CASE
            WHEN m.winner_id = search_athlete_id THEN 'WIN'
            ELSE 'LOSS'
        END as result,
        m.stage
    FROM matches m
    JOIN athletes a1 ON m.athlete_a_id = a1.athlete_id
    JOIN users u1 ON a1.user_id = u1.user_id
    JOIN athletes a2 ON m.athlete_b_id = a2.athlete_id
    JOIN users u2 ON a2.user_id = u2.user_id
    WHERE (m.athlete_a_id = search_athlete_id OR m.athlete_b_id = search_athlete_id)
      AND m.winner_id IS NOT NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION log_matches_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'UPDATE') THEN
        INSERT INTO audit_log (table_name, record_id, action_type, old_value, new_value, changed_by)
        VALUES (
            'matches',
            OLD.match_id,
            'UPDATE',
            'Winner: ' || COALESCE(OLD.winner_id::TEXT, 'None'),
            'Winner: ' || COALESCE(NEW.winner_id::TEXT, 'None'),
            NULL
        );
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_matches_audit
AFTER UPDATE ON matches
FOR EACH ROW
EXECUTE FUNCTION log_matches_changes();

CREATE OR REPLACE FUNCTION update_rankings_after_match()
RETURNS TRIGGER AS $$
DECLARE
    hand_val hand_type;
    loser_id INT;
BEGIN
    IF (OLD.winner_id IS NULL AND NEW.winner_id IS NOT NULL) THEN

        SELECT hand INTO hand_val FROM weight_classes WHERE class_id = NEW.class_id;

        IF (NEW.winner_id = NEW.athlete_a_id) THEN
            loser_id := NEW.athlete_b_id;
        ELSE
            loser_id := NEW.athlete_a_id;
        END IF;

        INSERT INTO rankings (athlete_id, hand, wins, losses, points)
        VALUES (NEW.winner_id, hand_val, 1, 0, 1010)
        ON CONFLICT (athlete_id, hand)
        DO UPDATE SET wins = rankings.wins + 1, points = rankings.points + 10, last_updated = NOW();

        INSERT INTO rankings (athlete_id, hand, wins, losses, points)
        VALUES (loser_id, hand_val, 0, 1, 990)
        ON CONFLICT (athlete_id, hand)
        DO UPDATE SET losses = rankings.losses + 1, points = rankings.points - 10, last_updated = NOW();

    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_stats
AFTER UPDATE ON matches
FOR EACH ROW
EXECUTE FUNCTION update_rankings_after_match();