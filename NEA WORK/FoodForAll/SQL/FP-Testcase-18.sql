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
WHERE r.username = 'mortysmithwest'
  and fpr.PreferredDeliveryDate = '2024-12-24';