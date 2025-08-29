INSERT INTO training_types (id, name)
SELECT 1, 'Cardio'
WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE name = 'Cardio');

INSERT INTO training_types (id, name)
SELECT 2, 'Strength'
WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE name = 'Strength');

INSERT INTO training_types (id, name)
SELECT 3, 'Yoga'
WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE name = 'Yoga');

INSERT INTO training_types (id, name)
SELECT 4, 'Crossfit'
WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE name = 'Crossfit');

INSERT INTO training_types (id, name)
SELECT 5, 'Pilates'
WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE name = 'Pilates');
