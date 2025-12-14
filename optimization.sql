CREATE INDEX idx_matches_tourn ON matches(tournament_id);
CREATE INDEX idx_matches_athlete_a ON matches(athlete_a_id);
CREATE INDEX idx_matches_athlete_b ON matches(athlete_b_id);
CREATE INDEX idx_matches_winner ON matches(winner_id);

CREATE INDEX idx_registrations_athlete ON tournament_registrations(athlete_id);
CREATE INDEX idx_registrations_tourn ON tournament_registrations(tournament_id);

CREATE INDEX idx_athletes_country ON athletes(country_code);

CREATE INDEX idx_athletes_gender ON athletes(gender);

CREATE INDEX idx_rankings_points ON rankings(points DESC);

ANALYZE;