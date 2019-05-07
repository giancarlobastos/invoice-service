## Wyre Invoice Coding Challenge

#### Goal
Create a basic invoicing service that allows users to create invoices and pay in digital currency

#### Requirements
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

#### Deliverables
* Link to repo with source code
* Link to a running instance of the code or instructions to run locally.




## Proposed solution

The solution consists of a micro-service that exposes a REST API to create and retrieve invoices. The service is composed of 5 components with single responsibilities:
* `BitcoinService` that handles the communication with the bitcoin blockchain;
* `InvoiceService` that implements the business logic;
* `PaymentListener` that observes the blockchain transactions to update the status of the payment of invoices;
* `InvoiceStorage` that persist the invoices, and
* `InvoiceController` that exposes the `InvoiceService` as a REST API.

For this solution I decided to implement the components without defined underlying _interfaces_ because of 2 reasons:
1. To keep the solution as simple as possible, and
2. For consider that in the future, if the `interfaces` are required, they can be added easily as it is the simplest refactoring scenario. 

#### Requirements
* Java 8
* Maven 3.5+

#### Technologies
* Spring Boot 2
* bitcoinJ
* Embedded Redis
* Angular 7

#### How to run

**Execute:**
```
mvn clean spring-boot:run
```

**And visit [http://localhost:8080](http://localhost:8080)** 

*The front-end is released together with the service.*

#### To test the invoice payment
You can use [Bitcoin Testnet Faucet](https://bitcoinfaucet.uo1.net/send.php) to send test bitcoins. 

#### Front-end source code
The front-end source code is available at [https://github.com/giancarlobastos/invoice-frontend](https://github.com/giancarlobastos/invoice-frontend). It is not necessary to run it locally.