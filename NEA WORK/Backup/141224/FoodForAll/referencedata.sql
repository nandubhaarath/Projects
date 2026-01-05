INSERT INTO StatusList (StatusId, StatusName) VALUES
(1, 'Pending'),
(2, 'In Progress'),
(3, 'Delivered'),
(4, 'Collected'),
(5, 'Ready for Pickup');


INSERT INTO DietaryPreferenceList (PreferenceId, PreferenceName, Description) VALUES
(1, 'Vegetarian', 'No meat products'),
(2, 'Vegan', 'No animal products'),
(3, 'Gluten-Free', 'No gluten-containing products'),
(4, 'Lactose-Free', 'No dairy products');


INSERT INTO HealthRestrictionList (HealthRestrictionId, HealthRestrictionName, Description) VALUES
(1, 'Peanut Allergy', 'Severe allergic reaction to peanuts'),
(2, 'Gluten Allergy', 'Intolerance to gluten'),
(3, 'Lactose Intolerance', 'Intolerance to dairy products'),
(4, 'Shellfish Allergy', 'Allergic reaction to shellfish');


INSERT INTO FoodItemList (FoodItemId, FoodItemName, Description) VALUES
(1, 'Canned Beans', 'Canned black beans'),
(2, 'Pasta', 'Dry pasta'),
(3, 'Milk', '3-pint milk bottles'),
(4, 'Rice', 'Bags of rice'),
(5, 'Canned Soup', 'Canned vegetable soup');
