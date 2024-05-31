# Instructor Notes

## End-to-End Test
Start by adding e2e, happy path tests in order to be able to refactor. These tests start the entire application 
which is slow and can be difficult to maintain over time.  As a consequence, the number of e2e tests should be limited 
to a small number of tests.  Edge cases should be tested farther down in the testing pyramid.
- Add the following tests to `TodoApiApplicationTests`
  - `shouldAddTodoSuccessfully`
  - `shouldListAtLeastOneTodo`
  - `shouldDeleteTodoSuccessfully`
  - `shouldUpdateTodo`
  - `shouldCountTodos`

## Refactoring
Now that e2e tests are in place, the application is positioned for refactoring.  Refactoring is defined as changing 
internal implementation without having any impact on external behavior.

### Inject Everything
All dependencies should be injected so that it can be easily mocked within tests
- Create a `Logger` within `LoggingConfiguration` using the `LoggerFactory`
- Switch to constructor injection in the following components to simplify mocking
  - `TodoController`
  - `TodoService`
- Inject `final` logger into the following components
  - `TodoController`
  - `TodoService`

Organize Tests in packages to differentiate the different types of tests.  This is helpful so developers can decide 
which tests they want to run locally.  It also helps team members understand where they should be adding tests 
during development.

- Create the following packages
  - e2e
  - integration
  - Unit tests will be placed in the same folder as the class under tests.  This allows tests to access package scope variables and method for testing.  It also helps organize tests for each class and simplify IDE test creation and navigation between the test and the class under test.