SELECT fpi.FoodItemId, fi.FoodItemName, fi.Description, fpi.Quantity
FROM foodparcelitem fpi
         JOIN fooditemlist fi ON fpi.FoodItemId = fi.FoodItemId
WHERE fpi.ParcelRequestId = 31;