CREATE TABLE exercise
(
    id               UUID PRIMARY KEY,
    name             VARCHAR(255) NOT NULL UNIQUE,
    movement_pattern VARCHAR(50)  NOT NULL,
    category         VARCHAR(50)  NOT NULL,
    difficulty       VARCHAR(50)  NOT NULL
);

CREATE TABLE exercise_target_muscle_groups
(
    exercise_id  UUID        NOT NULL REFERENCES exercise (id),
    muscle_group VARCHAR(50) NOT NULL,
    PRIMARY KEY (exercise_id, muscle_group)
);

CREATE TABLE exercise_secondary_muscle_groups
(
    exercise_id  UUID        NOT NULL REFERENCES exercise (id),
    muscle_group VARCHAR(50) NOT NULL,
    PRIMARY KEY (exercise_id, muscle_group)
);
