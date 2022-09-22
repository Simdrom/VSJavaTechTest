# VSJavaTechTest
Technical test for a Java Senior position

## Exercise 1
- [x] Created Springboot project using Java 8+.
- [x] Added to project an in-memory database that contains one table.
  - [x] Used H2
  - [x] Table's name **Users**
    - [x] Contains 3 columns
      - [x] Fullname
      - [x] Phone
      - [x] Address
- [x]  Users' table is initialised with:

| **Fullname**  | **Phone** |  **Address** |
| ------------- | ------------- |  ------------- |
| Thomson, Elias   | 555-8596  | Diamond St. 4G3 NY       |
| Simond, Ella     | 415-9687  | Fleet st. 45 B, 56 BR-NY |
| Clifford, Thomas | 416-69883 | Meet town, 45 - FL       |  

- [x] Project contains one end-point, ``` /api/users ```, as GET HTTP request without input parameters, and returns the data from Users' Table.
  - [x] In this case, I've used JSON as the format of the returned file, is called ```users.json``` and is generated in ``` resources ```´ folder in project ```src/main/resources```

## Exercise 2
- [x] Another end-point, ``` /api/copy ``` was created, as POST HTTP Request, and it receives as input a file and it copies it in local.
  - [x] The copy of the file has been made in the project local with relative paths.
  - [x] The copy of the file has been done asynchronously and with independent threads, reading and writing, taking into account that the endpoint returns a HTTP status 200 and while the copy is done on the other side.
