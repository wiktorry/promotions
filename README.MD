# Promotions

Algorithm that returns optimal payment methods for two json files
with orders and available payment methods.

## Idea

1. First, it evaluates two main strategies:
    - Assigning "PUNKTY" promotion to the largest possible orders.
    - Applying the 10% discount to orders that do not have any promotion equal to or greater than 10%.

2. After choosing the optimal strategy, the algorithm uses points by more optimal one.
   Then algorithm uses remaining payment methods, applying them to the largest possible orders.

3. If there are any remaining unassigned orders:
    - It attempts to use leftover points to apply 10% promotion.
    - Finally, it selects any available method that can pay the remaining amount.

## Stack

- Java 21
- Maven
- Lombok
- JUnit

## Usage

```bash
# Clone this repository
$ git clone https://github.com/wiktorry/promotions
# Open the project directory
$ cd promotions
# Build project with maven
$ mvn package
# Run project
$ java -jar target/app.jar pathToOrders.json pathToPaymentMethods.json
```

## Tests

To run tests use

```bash
$ mvn test
```