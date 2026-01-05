select *
from recipient
         join address on address.AddressId = recipient.AddressId
where username = 'mortysmithwest'