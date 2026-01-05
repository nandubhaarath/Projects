CREATE TABLE Address (
    AddressId INTEGER NOT NULL,
    AddressLine1 VARCHAR(50) NOT NULL,
    AddressLine2 VARCHAR(50),
    AddressLine3 VARCHAR(50),
    City 	VARCHAR(50),
    PostCode VARCHAR(20) NOT NULL,
    CONSTRAINT Address_PK PRIMARY KEY (AddressId)
);

CREATE TABLE Recipient (
    RecipientId  INT     NOT NULL,
    Title        VARCHAR(10) NULL,
    FirstName    VARCHAR(50) NOT NULL,
    LastName     VARCHAR(50) NOT NULL,
    DOB          DATE  NULL,
    Email        VARCHAR(50),
    TelNumber    VARCHAR(14),
    MobileNumber VARCHAR(14),
    UserName     VARCHAR(100) NOT NULL,
    Password     VARCHAR(50),
    AddressId INTEGER NOT NULL,
    CONSTRAINT RECIPIENT_PK PRIMARY KEY (RecipientId),
    CONSTRAINT Recipient_Address_FK FOREIGN KEY (AddressId) REFERENCES Address(AddressId)
);

CREATE TABLE Donor (
    DonorId INT NOT NULL,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Email VARCHAR(50),
    TelNumber VARCHAR(14),
    MobileNumber VARCHAR(14),
    AddressId INTEGER NOT NULL,
    CONSTRAINT Donor_PK PRIMARY KEY (DonorId),
    CONSTRAINT Donor_Address_FK FOREIGN KEY (AddressId) REFERENCES Address(AddressId)
);

CREATE TABLE FoodBank (
    FoodBankId INT NOT NULL,
    FoodBankName VARCHAR(100) NOT NULL,
    AddressId INTEGER NOT NULL,
    CONSTRAINT FoodBank_PK PRIMARY KEY (FoodBankId),
    CONSTRAINT FoodBank_Address_FK FOREIGN KEY (AddressId) REFERENCES Address(AddressId)
);

CREATE TABLE Volunteer (
    VolunteerId INT NOT NULL,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Email VARCHAR(50),
    TelNumber VARCHAR(14),
    MobileNumber VARCHAR(14),
    AddressId INTEGER NOT NULL,
    CONSTRAINT Volunteer_PK PRIMARY KEY (VolunteerId),
    CONSTRAINT Volunteer_Address_FK FOREIGN KEY (AddressId) REFERENCES Address(AddressId)
);

CREATE TABLE DietaryPreferenceList (
    PreferenceId INT NOT NULL,
    PreferenceName VARCHAR(50) NOT NULL,
    Description VARCHAR(255),
    CONSTRAINT DietaryPreferenceList_PK PRIMARY KEY (PreferenceId)
);

CREATE TABLE DietaryPreference (
    RecipientId INT NOT NULL,
    PreferenceId INT NOT NULL,
    Description VARCHAR(255),
    CONSTRAINT DietaryPreference_PK PRIMARY KEY (RecipientId, PreferenceId),
    CONSTRAINT DietaryPreference_Recipient_FK FOREIGN KEY (RecipientId) REFERENCES Recipient(RecipientId),
    CONSTRAINT DietaryPreference_Preference_FK FOREIGN KEY (PreferenceId) REFERENCES DietaryPreferenceList(PreferenceId)
);


CREATE TABLE FoodParcelRequest (
    ParcelRequestId INT NOT NULL,
    RecipientId INT NOT NULL,
    FoodBankId INT NOT NULL,
    RequestDate DATE NOT NULL,
    StatusId INT NOT NULL,
    CONSTRAINT FoodParcelRequest_PK PRIMARY KEY (ParcelRequestId),
    CONSTRAINT FoodParcelRequest_Recipient_FK FOREIGN KEY (RecipientId) REFERENCES Recipient(RecipientId),
    CONSTRAINT FoodParcelRequest_FoodBank_FK FOREIGN KEY (FoodBankId) REFERENCES FoodBank(FoodBankId),
    CONSTRAINT FoodParcelRequest_Status_FK FOREIGN KEY (StatusId) REFERENCES StatusList(StatusId)
);


CREATE TABLE StatusList (
    StatusId INT NOT NULL,
    StatusName VARCHAR(50) NOT NULL,
    CONSTRAINT StatusList_PK PRIMARY KEY (StatusId)
);

CREATE TABLE FoodItemList (
    FoodItemId INT NOT NULL,
    FoodItemName VARCHAR(100) NOT NULL,
    Description VARCHAR(255),
    CONSTRAINT FoodItemList_PK PRIMARY KEY (FoodItemId)
);


CREATE TABLE FoodParcelItem (
    ParcelRequestId INT NOT NULL,
    FoodItemId INT NOT NULL,
    Quantity INT NOT NULL,
    CONSTRAINT FoodParcelItem_PK PRIMARY KEY (ParcelRequestId, FoodItemId),
    CONSTRAINT FoodParcelItem_FoodParcelRequest_FK FOREIGN KEY (ParcelRequestId) REFERENCES FoodParcelRequest(ParcelRequestId),
    CONSTRAINT FoodParcelItem_FoodItem_FK FOREIGN KEY (FoodItemId) REFERENCES FoodItemList(FoodItemId)
);

CREATE TABLE FoodStock (
    StockId INT NOT NULL,
    FoodItemId INT NOT NULL,
    FoodBankId INT NOT NULL,
    Quantity INT NOT NULL,
    CONSTRAINT FoodStock_PK PRIMARY KEY (StockId),
    CONSTRAINT FoodStock_FoodItem_FK FOREIGN KEY (FoodItemId) REFERENCES FoodItemList(FoodItemId),
    CONSTRAINT FoodStock_FoodBank_FK FOREIGN KEY (FoodBankId) REFERENCES FoodBank(FoodBankId)
);

CREATE TABLE FoodStockTransaction (
    TransactionId INT NOT NULL,
    StockId INT NOT NULL,
    TransactionType VARCHAR(50) NOT NULL,  -- Donation or Request
    Quantity INT NOT NULL,
    TransactionDate DATE NOT NULL,
    CONSTRAINT FoodStockTransaction_PK PRIMARY KEY (TransactionId),
    CONSTRAINT FoodStockTransaction_Stock_FK FOREIGN KEY (StockId) REFERENCES FoodStock(StockId)
);

CREATE TABLE Donation (
    DonationId INT NOT NULL,
    DonorId INT NOT NULL,
    FoodItemId INT NOT NULL,
    Quantity INT NOT NULL,
    DonationDate DATE NOT NULL,
    CONSTRAINT Donation_PK PRIMARY KEY (DonationId),
    CONSTRAINT Donation_Donor_FK FOREIGN KEY (DonorId) REFERENCES Donor(DonorId),
    CONSTRAINT Donation_FoodItem_FK FOREIGN KEY (FoodItemId) REFERENCES FoodItemList(FoodItemId)
);

CREATE TABLE VolunteerDeliveryAssignment (
    AssignmentId INT NOT NULL,
    VolunteerId INT NOT NULL,
    ParcelRequestId INT NOT NULL,
    DeliveryDate DATE NOT NULL,
    StatusId INT NOT NULL,
    CONSTRAINT VolunteerDeliveryAssignment_PK PRIMARY KEY (AssignmentId),
    CONSTRAINT VolunteerDeliveryAssignment_Volunteer_FK FOREIGN KEY (VolunteerId) REFERENCES Volunteer(VolunteerId),
    CONSTRAINT VolunteerDeliveryAssignment_FoodParcelRequest_FK FOREIGN KEY (ParcelRequestId) REFERENCES FoodParcelRequest(ParcelRequestId),
    CONSTRAINT VolunteerDeliveryAssignment_Status_FK FOREIGN KEY (StatusId) REFERENCES StatusList(StatusId)
);

CREATE TABLE VolunteerCollectionAssignment (
    AssignmentId INT NOT NULL,
    VolunteerId INT NOT NULL,
    DonorId INT NOT NULL,
    CollectionDate DATE NOT NULL,
    StatusId INT NOT NULL,
    ItemsReady VARCHAR(20), -- Optional: "Ready", "Not Ready"
    CONSTRAINT VolunteerCollectionAssignment_PK PRIMARY KEY (AssignmentId),
    CONSTRAINT VolunteerCollectionAssignment_Volunteer_FK FOREIGN KEY (VolunteerId) REFERENCES Volunteer(VolunteerId),
    CONSTRAINT VolunteerCollectionAssignment_Donor_FK FOREIGN KEY (DonorId) REFERENCES Donor(DonorId),
    CONSTRAINT VolunteerCollectionAssignment_Status_FK FOREIGN KEY (StatusId) REFERENCES StatusList(StatusId)
);


CREATE TABLE HealthRestrictionList (
    HealthRestrictionId INT NOT NULL,
    HealthRestrictionName VARCHAR(50) NOT NULL,
    Description VARCHAR(255),
    CONSTRAINT HealthRestrictionList_PK PRIMARY KEY (HealthRestrictionId)
);


CREATE TABLE RecipientHealthRestriction (
    RecipientId INT NOT NULL,
    HealthRestrictionId INT NOT NULL,
    CONSTRAINT RecipientHealthRestriction_PK PRIMARY KEY (RecipientId, HealthRestrictionId),
    CONSTRAINT RecipientHealthRestriction_Recipient_FK FOREIGN KEY (RecipientId) REFERENCES Recipient(RecipientId),
    CONSTRAINT RecipientHealthRestriction_HealthRestriction_FK FOREIGN KEY (HealthRestrictionId) REFERENCES HealthRestrictionList(HealthRestrictionId)
);
