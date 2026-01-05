--food bank address details
select *
from foodbank
         join address on address.AddressId = foodbank.AddressId;

--volunteer address details
select *
from Volunteer
         join address on address.AddressId = volunteer.AddressId;


--list all food parcel requests
select *
from foodparcelrequest
         join recipient on recipient.RecipientId = foodparcelrequest.RecipientId
         join address on address.AddressId = recipient.AddressId
         join statuslist on statuslist.StatusId = foodparcelrequest.StatusId
--where foodparcelrequest.PreferredDeliveryDate='2024-12-06'
--and statuslist.StatusName='Pending'


