--main query
SELECT fpr.ParcelRequestId,
       fpr.RequestDate,
       fpr.PreferredDeliveryDate,
       fpr.StatusId,
       r.FirstName,
       r.LastName,
       a.AddressId,
       a.AddressLine1,
       a.AddressLine2,
       a.City,
       a.PostCode
FROM FoodParcelRequest fpr
         JOIN Recipient r ON fpr.RecipientId = r.RecipientId
         JOIN Address a ON r.AddressId = a.AddressId
WHERE r.username = 'mortysmithwest';

--sub query to select food parcel items by parcel id executed in a loop
SELECT fpi.FoodItemId, fi.FoodItemName, fi.Description, fpi.Quantity
FROM foodparcelitem fpi
         JOIN fooditemlist fi ON fpi.FoodItemId = fi.FoodItemId
WHERE fpi.ParcelRequestId = 1;

SELECT fpi.FoodItemId, fi.FoodItemName, fi.Description, fpi.Quantity
FROM foodparcelitem fpi
         JOIN fooditemlist fi ON fpi.FoodItemId = fi.FoodItemId
WHERE fpi.ParcelRequestId = 6;

SELECT fpi.FoodItemId, fi.FoodItemName, fi.Description, fpi.Quantity
FROM foodparcelitem fpi
         JOIN fooditemlist fi ON fpi.FoodItemId = fi.FoodItemId
WHERE fpi.ParcelRequestId = 11;

SELECT fpi.FoodItemId, fi.FoodItemName, fi.Description, fpi.Quantity
FROM foodparcelitem fpi
         JOIN fooditemlist fi ON fpi.FoodItemId = fi.FoodItemId
WHERE fpi.ParcelRequestId = 16;

SELECT fpi.FoodItemId, fi.FoodItemName, fi.Description, fpi.Quantity
FROM foodparcelitem fpi
         JOIN fooditemlist fi ON fpi.FoodItemId = fi.FoodItemId
WHERE fpi.ParcelRequestId = 21;


SELECT fpi.FoodItemId, fi.FoodItemName, fi.Description, fpi.Quantity
FROM foodparcelitem fpi
         JOIN fooditemlist fi ON fpi.FoodItemId = fi.FoodItemId
WHERE fpi.ParcelRequestId = 26;