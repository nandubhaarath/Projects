-- Delete existing data from the Weights table if needed
DELETE FROM Weights;

-- Insert corrected data into the Weights table
INSERT INTO Weights (FromNode, ToNode, Weight) VALUES
(6, 1, 9),  -- Food Bank Central to Brasted Close
(6, 2, 4),  -- Food Bank Central to Steetley Court
(6, 3, 5),  -- Food Bank Central to Beresford Road
(6, 4, 6),  -- Food Bank Central to High Street
(6, 5, 7),  -- Food Bank Central to Castle Row
(6, 7, 11), -- Food Bank Central to Green Avenue
(6, 8, 15), -- Food Bank Central to Rosedrop
(1, 2, 2),  -- Brasted Close to Steetley Court
(1, 3, 4),  -- Brasted Close to Beresford Road
(1, 4, 8),  -- Brasted Close to High Street
(1, 5, 2),  -- Brasted Close to Castle Row
(1, 7, 4),  -- Brasted Close to Green Avenue
(1, 8, 9),  -- Brasted Close to Rosedrop
(2, 3, 4),  -- Steetley Court to Beresford Road
(2, 4, 5),  -- Steetley Court to High Street
(2, 5, 3),  -- Steetley Court to Castle Row
(2, 7, 7),  -- Steetley Court to Green Avenue
(2, 8, 8),  -- Steetley Court to Rosedrop
(3, 4, 3),  -- Beresford Road to High Street
(3, 5, 4),  -- Beresford Road to Castle Row
(3, 7, 11), -- Beresford Road to Green Avenue
(3, 8, 10), -- Beresford Road to Rosedrop
(4, 5, 2),  -- High Street to Castle Row
(4, 7, 6),  -- High Street to Green Avenue
(4, 8, 10), -- High Street to Rosedrop
(5, 7, 5),  -- Castle Row to Green Avenue
(5, 8, 4),  -- Castle Row to Rosedrop
(7, 8, 4);  -- Green Avenue to Rosedrop
