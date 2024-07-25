meCash Design
==================

# Table of Content

- [Functional Requirements](#functional-requirements)
- [Non Functional Requirements](#non-functional-requirements)
- [Use-case Diagrams](#use-case-diagram)
- [Capacity Estimation](#capacity-estimation)
- [Low level Design](#low-level-design)
- High level Design
- Database Design
- REST Endpoint
- Scalability

# Functional Requirements
- Customer Registration (Individual and Corporate)
- Customer Login (Individual and Corporate)
- Customer Account Creation (multi-currency)
- Deposit (multi-currency)
- Funds Transfer (multi-currency)
- View Account Balance (multi-currency)
- View Transaction History (multi-currency)

# Non-Functional Requirements
- Security:
  - Password Security:
    - password must contain at least one uppercase,lowercase, number, and a special character. Proposed regex will be ```/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\W)(?!.* ).{8,16}$/```
    - JWT token will be used for authentication and authorization of requests
- Audit Trail:
  - An audit trail of requests and responses 
  
- Availability: The system should always be available.
- Policies in Nigeria:
  - Individual Account:
    - An Individual account must have a valid BVN & NIN
  - Corporate Account:
    - A Corporate entity must have:
      - A valid registration number (Business Number or RC Number)
      - Corporate entities registered as a business(BN) will have at lease one signatory
      - Corporate entities registered as LLC(RC) must have at least two signatories
      - All signatories must have valid BVN and NIN

# Use-case Diagram
![meCash use-case](../docs/assets/meCash-usecase.drawio.png)

As seen in the image above the customer will be able to perform the following functions
- Register: The customer will register if they do not previously have a profile. Registration requires the  following:
  - BVN number
  - NIN number
  - Name (surname, firstname, middle name)
  - email
  - phone number (username)
  - DOB
  - password

- Login: Upon successful registration and login, the customer may log in with their username and password


- Create Account: The customer will log in with their username(phone number) and password 
  - A logged in customer may create one individual account per currency
  - A logged in customer may create one or many corporate accounts per currency. Each corporate account per currency must be created with one company or business registration number
  - Corporate entities(LLC - Limited Liability company)  must have at least two signatories
  - Individual or Corporate entities(Business Name) may have one or many signatories


- View account balance:
  - A logged-in user can view the balance of all the accounts he is a signatory of.


- View transaction:
  - A logged-in user can view a history of transactions on accounts he is a signatory of
  - A logged-in user must provide an account number as a query param
  - A logged-in user may provide a date range as a query param
  

- Transaction: A logged-in user may perform the following transactions
  - A transaction may either be of type `CREDIT` or `DEBIT`
  - A transaction may be one of the following categories:
    - Deposit: `DEBIT` the till account, `CREDIT` the customer account
    - Withdrawal: `CREDIT` the till account, `DEBIT` customer account
    - Transfer: `DEBIT` the initiator `CREDIT` the beneficiary

# Capacity Estimation
Assumption is 100 million users per month

``` 
Traffic per second: 100,000,000/30*24*60*60= 38.50 
Assumption: 30% traffic for new registration, 40% perform transactions, 30% view account balance and transaction history

TPS = 30+40 =70
Storage per second(100 KB/transaction)= 70*100 = 7000 KB/S = 7 MB/S
Storage required per year = 7*60*60*24*365 = 221 TB
```

# Low Level Design



