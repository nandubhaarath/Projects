-- Clear existing data
DELETE FROM foodparcelitem;

-- Insert multiple food items per parcel with varying quantities
INSERT INTO foodparcelitem (ParcelRequestId, FoodItemId, Quantity) VALUES
(1, 1, 3), (1, 2, 1), (1, 3, 2),     -- Parcel 1 with 3 items
(2, 2, 2), (2, 4, 3),                -- Parcel 2 with 2 items
(3, 3, 1), (3, 5, 2), (3, 1, 1),     -- Parcel 3 with 3 items
(4, 4, 2), (4, 5, 4),                -- Parcel 4 with 2 items
(5, 5, 4), (5, 2, 2),                -- Parcel 5 with 2 items
(6, 1, 1), (6, 3, 3), (6, 4, 1),     -- Parcel 6 with 3 items
(7, 2, 3), (7, 5, 1),                -- Parcel 7 with 2 items
(8, 3, 1), (8, 4, 2),                -- Parcel 8 with 2 items
(9, 4, 1), (9, 1, 2), (9, 5, 2),     -- Parcel 9 with 3 items
(10, 5, 3), (10, 2, 2),              -- Parcel 10 with 2 items
(11, 1, 2), (11, 4, 1), (11, 3, 2),  -- Parcel 11 with 3 items
(12, 2, 2), (12, 5, 2),              -- Parcel 12 with 2 items
(13, 3, 2), (13, 1, 3),              -- Parcel 13 with 2 items
(14, 4, 4), (14, 2, 1),              -- Parcel 14 with 2 items
(15, 5, 1), (15, 3, 1), (15, 4, 2),  -- Parcel 15 with 3 items
(16, 1, 3), (16, 5, 2),              -- Parcel 16 with 2 items
(17, 2, 2), (17, 3, 1),              -- Parcel 17 with 2 items
(18, 3, 2), (18, 4, 3),              -- Parcel 18 with 2 items
(19, 4, 3), (19, 1, 1),              -- Parcel 19 with 2 items
(20, 5, 4), (20, 2, 3),              -- Parcel 20 with 2 items
(21, 1, 1), (21, 3, 2),              -- Parcel 21 with 2 items
(22, 2, 1), (22, 4, 3),              -- Parcel 22 with 2 items
(23, 3, 2), (23, 5, 1),              -- Parcel 23 with 2 items
(24, 4, 3), (24, 1, 2),              -- Parcel 24 with 2 items
(25, 5, 1), (25, 2, 2), (25, 3, 1);  -- Parcel 25 with 3 items



--update data
update foodparcelrequest set preferreddeliverydate='2024-12-05' where parcelrequestid=26;


update fooditemlist
set description='Baked Beans',fooditemname='Baked Beans'
where description='Baked Beans';


update fooditemlist
set description='Pasta',fooditemname='Pasta'
where description='Dry pasta';


update fooditemlist
set description='4 pint whole milk'
where description='3-pint milk bottles';


update fooditemlist
set description='1kg rice'
where description='Bags of rice';
