
select * from VolunteerDeliveryAssignment;

select * from foodparcelrequest;


select * from foodparcelrequest
join recipient on recipient.RecipientId=foodparcelrequest.RecipientId
join address on address.AddressId=recipient.AddressId
join statuslist on statuslist.StatusId=foodparcelrequest.StatusId
where foodparcelrequest.PreferredDeliveryDate='2024-12-08';

