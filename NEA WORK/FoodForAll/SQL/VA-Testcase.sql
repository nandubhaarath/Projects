SELECT  vda.AssignmentId, fpr.ParcelRequestId, r.FirstName, r.LastName,
        a.AddressLine1, a.AddressLine2, a.City, a.PostCode,
        sl.StatusName, vda.DeliveryDate
FROM VolunteerDeliveryAssignment vda
         JOIN FoodParcelRequest fpr ON vda.ParcelRequestId = fpr.ParcelRequestId
         JOIN Recipient r ON fpr.RecipientId = r.RecipientId
         JOIN Address a ON r.AddressId = a.AddressId
         JOIN StatusList sl ON fpr.StatusId = sl.StatusId
WHERE vda.VolunteerId = 1;





