# Banking-System

## Objective

Design a simple banking system that handles operations on bank accounts. The system should be capable of the following features:
- input banking transactions
- calculate interest
- printing account statement

Application should prompt the user for actions.

Upon given the actions user inputs should be validated.

Application should return the right output accordingly.

## Design Decisions

- Application is divided into multiple layers and modules such as services, repositories,
models, utils etc for separation of concerns and readability.

- It was decided to use a maps and lists as the datastore instead of a database (H2 etc) since the program is a small.
- Code is written in such a way that if a database needs to be introduced to the solution only minimal changes has to be made.
- Code is commented and Javadocs were added at important points to elaborate the logic used and for reader understanding.


## Assumptions

There were few unclear parts in the question. Few assumptions were made in cater those.
please refer below.

- When printing the final statement only account number and month is provided by the user but
not the year. However, if there were transactions from the same month for multiple years
we only consider records of the current year.
- Running sequence in transaction id, it is not mentioned what to do when transaction id exceeds 10 ( whether it should be 10 or 010).
so nevertheless of the id we add a preceding 0 like in the example given.
- When the interest is calculated for the initial balance for a period and compounding is
not done in the example. Therefore the current logic will follow what is given in the example.


## Further improvements

- More unit tests can be written to improve coverage
- Sonar properties file can be added with a running sonar server so the code quality reports can be generated and pushed.
- A database can be attached to the solution instead of an in memory solution.
- Spring boot can be used.
- Can add withdrawals (W), Deposits(D) and Interest(I) as enums since they are the only transaction types

## Test Data

Following test set was used for initial testing

Transactions
- 20230526|AC001|D|100.00
- 20230601|AC001|D|150.00
- 20230626|AC001|W|20.00
- 20230626|AC001|W|100.00

Interest rules
- 20230515|RULE02|1.90
- 20230615|RULE03|2.20

Account name and month
- AC001|06

Ideally given these inputs program should produce 0.39 as the interest amount.

## Setting up the application

This was developed in a macOS env with openJDK20

- Clone the project from the repo
- Run `mvn clean install` to install all dependencies

or else you can run the commands separately 

`mvn clean`  - to clean any existing build artifacts

`mvn compile` - to compile and create .class files

`mvn package`  - to create an executable JAR 

above command will create a JAR file within the target folder 

- Running the executable JAR via below command

`java -jar target/BankingSystem-1.0-SNAPSHOT.jar`

This will run the program in a terminal and prompt the user to enter inputs

Please find the build artifact (jar file) for this project [here](https://drive.google.com/drive/u/1/folders/1Cffc2qff2X9rohB9E2VJ6daqZeaD_ufm)


## About the author

My name is Sithija Thewa Hettige.
 Find more about me here ,

- [Personal Blog](https://fnotess.github.io/my-portfolio-web/#)
- [LinkedIn](https://www.linkedin.com/in/sithijathewahettige/)
- [Stack Overflow](https://stackoverflow.com/users/4859626/sithija-piyuman-thewa-hettige)


P.S : You may see a slight misalignment when comes to tables being printed
this I do not experience in the intellij IDEA terminal. But you may see it in others.
But the values are correct.




