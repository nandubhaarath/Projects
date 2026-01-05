select addr1.AddressId    fromAddressId,
       addr1.Addressline1 fromAddressline1,
       addr1.Addressline2 fromAddressline2,
       addr1.PostCode     fromPostCode,

       addr2.AddressId    toAddressId,
       addr2.Addressline1 toAddressline1,
       addr2.Addressline2 toAddressline2,
       addr2.PostCode     toPostCode,
       Weight
from weights
         join address addr1 on weights.FromNode = addr1.addressid
         join address addr2 on weights.ToNode = addr2.AddressId