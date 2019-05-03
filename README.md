##Wyre Invoice Coding Challenge

####Goal
Create a basic invoicing service that allows users to create invoices and pay in digital currency

####Requirements
* A User must be able to type in an amount and the service would generate an Invoice
* A new digital currency address must be created for each invoice
* The User can send funds to the address and have the amount deducted from the Invoice total
* If the User pays an amount into the wallet address specified, it would need to update the total amount owed on the Invoice.
* Invoices would have three states:
    * Expired 
    * Partially paid
    * Paid
* UI is not required, but is a *huge plus*. We are primarily looking for functionality
* You only need to support one digital currency. Your best bets would be BTC, ETH or DAI
* There are no restrictions on language or technologies used in this challenge

####Deliverables
* Link to repo with source code
* Link to a running instance of the code or instructions to run locally.




##Proposed solution

####Requirements
* Java 8
* Maven 3.5+

####Technologies
* Spring Boot 2
* bitcoinJ
* Embedded Redis

####How to run

Execute:
```
mvn clean spring-boot:run
```

And visit [http://localhost:8080](http://localhost:8080) 