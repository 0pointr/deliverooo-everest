# Deliverooo

Deliverooo is a console app for estimating package delivery cost and time.


## Running


```bash
mvn spring-boot:run
```
### or

```bash
mvn clean package spring-boot:repackage
java -jar target/deliveroo-0.0.1-SNAPSHOT.jar
```
Add an empty newline  after input lines to start processing.
##

## Test


```bash
mvn test
```
##
## Notes
1. You can add more coupons in code like:
```java
// code, discount, minW, maxW, minD, maxD
couponService.registerCoupon( new Coupon("OFR001", 10, 70, 200, 0, 199) )
			.registerCoupon( new Coupon("OFR002", 7, 100, 250, 50, 150) )
			.registerCoupon( new Coupon("OFR003", 5, 10, 150, 50, 250) );
```
2. Tests are only written for cost and discount calculations.


