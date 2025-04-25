# JavaDBImpl

A lightweight, in-memory relational algebra engine in Java, built as part of a Database Management class.

## Features  
- **Data Loading** from CSV files  
- **Relational Algebra Operators** in `RAimpl`:
  - **Selection** (`select`) with custom predicates  
  - **Projection** (`project`) eliminating duplicates  
  - **Union** (`union`) and **Difference** (`diff`)  
  - **Rename** (`rename`) of attributes  
  - **Cartesian Product** (`cartesianProduct`)  
  - **Natural Join** and **Theta Join**  
- **Predicate Parsing** in `PredicateImpl` supporting `==, !=, >, <, >=, <=` across `INTEGER`, `DOUBLE`, and `STRING`

## Usage Example
```java
Relation instructors = new RelationBuilder()
    .attributeNames(List.of("ID", "Name", "Dept", "Salary"))
    .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE))
    .build();
instructors.loadData("/path/to/instructor_export.csv");

Relation teaches = new RelationBuilder()
    .attributeNames(List.of("ID", "Course_ID", "Semester", "Year"))
    .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.INTEGER))
    .build();
teaches.loadData("/path/to/teaches_export.csv");

RAimpl db = new RAimpl();
Relation joined = db.join(instructors, teaches);
Relation result = db.project(joined, List.of("Name", "Course_ID", "Year"));
result.print();
