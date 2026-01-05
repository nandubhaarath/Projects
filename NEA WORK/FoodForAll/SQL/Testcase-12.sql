select * from foodparcelrequest
                  join recipient on recipient.RecipientId=foodparcelrequest.RecipientId
                  join address on address.AddressId=recipient.AddressId
                  join statuslist on statuslist.StatusId=foodparcelrequest.StatusId
where foodparcelrequest.PreferredDeliveryDate='2024-12-06'
and statuslist.StatusName='Pending'