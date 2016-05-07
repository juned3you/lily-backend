CREATE TABLE goal_configuration (
	id bigint  NOT NULL AUTO_INCREMENT,
	minimum_value int NOT NULL,
	maximum_value int NOT NULL,	
	value float NOT NULL,
	type VARCHAR(20) NOT NULL,	
	PRIMARY KEY (id)
);

//Monthly Growth Coefficient
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (0, 20, 1.05, 'MonthlyGrowth');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (20, 40, 1.1, 'MonthlyGrowth');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (40, 60, 1.15, 'MonthlyGrowth');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (60, 80, 1.20, 'MonthlyGrowth');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (80, 100, 1.25, 'MonthlyGrowth');

//Sleep Goal
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (0, 3, 10, 'Sleep');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (3, 4, 40, 'Sleep');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (4, 5, 60, 'Sleep');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (5, 6, 70, 'Sleep');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (6, 7, 80, 'Sleep');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (7, 9, 100, 'Sleep');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (9, 10, 80, 'Sleep');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (10, 12, 60, 'Sleep');

//Steps Goal
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (1000, 2000, 20, 'Steps');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (2000, 4000, 40, 'Steps');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (4000, 6000, 60, 'Steps');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (6000, 8000, 80, 'Steps');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (8000, 10000, 100, 'Steps');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (10000, 12000, 90, 'Steps');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (12000, 14000, 80, 'Steps');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (14000, -1, 60, 'Steps');

//BPM
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (20, 30, 195, 'BPM');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (30, 40, 185, 'BPM');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (40, 50, 175, 'BPM');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (50, 60, 165, 'BPM');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (60, -1, 155, 'BPM');

//Active minutes
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (0, 30, 0, 'ActiveMinutes');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (30, 50, 40, 'ActiveMinutes');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (50, 70, 60, 'ActiveMinutes');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (70, 90, 80, 'ActiveMinutes');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (90, 150, 100, 'ActiveMinutes');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (150, 200, 90, 'ActiveMinutes');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (200, 250, 70, 'ActiveMinutes');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (250, 350, 50, 'ActiveMinutes');
INSERT INTO goal_configuration (minimum_value, maximum_value, value, type) VALUES (350, 400, 20, 'ActiveMinutes');
