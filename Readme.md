# Deliverooo

Deliverooo is a console app for for estimating package delivery cost and time.


## Running


```bash
mvn spring-boot:run
```
### or

```bash
mvn clean package spring-boot:repackage
java -jar target/deliveroo-0.0.2-SNAPSHOT.jar
```

### Input Examples
Remember to add an empty newline  after input lines to start processing.

Input 1
```bash
100 3
PKG1 5 5 OFR001
PKG2 15 5 OFR002
PKG3 10 100 OFR003
2 70 200
```
Output 1
```bash
PKG1 0.0 175.00 0.07
PKG2 0.0 275.00 0.07
PKG3 35.0 665.00 1.43
```
Input 2
```bash
100 5
PKG1 50 30 OFR001
PKG2 75 125 OFR002
PKG3 175 100 OFR003
PKG4 110 60 OFR003
PKG5 155 95 NA
2 70 200
```
Output 2
```bash
PKG1 0.0 750.00 4.00
PKG2 0.0 1475.00 1.79
PKG3 0.0 2350.00 1.43
PKG4 75.0 1425.00 0.86
PKG5 0.0 2125.00 4.21
```

##

## Test


```bash
mvn test
```
##
## Notes
1. 

#### UPDATE:
You can now add coupons directly in /src/main/resources/coupons.json like following:

```json
{
	"code":"OFR001",
	"discount":10,
	"minWeight":70,
	"maxWeight":200,
	"minDistance":0,
	"maxDistance":199
},
```

~~You can add more coupons in Deliveroo.setup like:~~
```java
// code, discount, minW, maxW, minD, maxD
couponService.registerCoupon( new Coupon("OFR001", 10, 70, 200, 0, 199) )
			.registerCoupon( new Coupon("OFR002", 7, 100, 250, 50, 150) )
			.registerCoupon( new Coupon("OFR003", 5, 10, 150, 50, 250) );
```

2. Tests are only written for cost, discount and ETA calculations and input validations.
