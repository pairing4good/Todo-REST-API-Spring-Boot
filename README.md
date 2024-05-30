# Todo REST API


The following is simple todo REST API using the [Spring Boot Framework](https://spring.io/projects/spring-boot). The API allow you to
add item to a to-do list, update, select and remove from the list.

### Set Up
* Make sure you have [Java 17 JDK](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html) installed

### Test
* Run the tests `./mvnw test`

### Usage
* Run the application `./mvnw spring-boot:run`
* Navigate to http://localhost:8080/swagger-ui.html

## Swagger UI:

### Add  Item
<img src="https://github.com/pairing4good/Todo-REST-API-Spring-Boot/blob/master/readme/additem.jpg?raw=true"  alt="Demo screen postman">

### Todo list :
<img src="https://github.com/pairing4good/Todo-REST-API-Spring-Boot/blob/master/readme/todolist.jpg?raw=true"  alt="Demo screen postman">

### Update item
<img src="https://github.com/pairing4good/Todo-REST-API-Spring-Boot/blob/master/readme/updateItem.jpg?raw=true"  alt="Demo screen postman">

### Delete item
<img src="https://github.com/pairing4good/Todo-REST-API-Spring-Boot/blob/master/readme/deleteItem.jpg?raw=true"  alt="Demo screen postman">

## Spring Testing

### Testing Pyramid
<img src="https://github.com/pairing4good/Todo-REST-API-Spring-Boot/blob/master/readme/test-pyramid.jpg?raw=true"  alt="Testing Pyramid">
https://martinfowler.com/bliki/TestPyramid.html

At the top of the testing pyramid tests are the slowest and cost the most to run and maintain.  At the bottom of the pyramid tests run the fastest and require very little maintenance.
As a result, we should write unit tests, which are at the bottom of the pyramid, for all production code.  As tests get slower and harder to maintain we should write less of them.  

- Unit Tests: test small units of code in complete isolation.  This is typically at the method level and mocks out any external dependencies.
  - [JUnit Tests](https://junit.org/junit5/)
  - Mock out all external dependencies with [Mockito](https://site.mockito.org/)
  - [Spring Unit Testing](https://docs.spring.io/spring-framework/reference/testing/unit.html)

- Service/Integration: integrates multiple components together to verify their interactions. 
  - [MockMvc](https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework.html)

- UI/ End-to-end: starts up the entire application and tests it as a black box.
    - [@SpringBootTest](https://spring.io/guides/gs/testing-web#_test_the_application)
    - [TestRestTemplate](https://spring.io/guides/gs/testing-web#_test_the_application)



