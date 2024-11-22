# Event Scheduler Application  
A Spring Boot application to manage and schedule events without conflicts.

---

## **Prerequisites**  
- **Java**: Version 17 or later  
- **Maven**: Version 3.8.1 or later  
- **Development Environment**: IntelliJ IDEA or terminal with Java configured  

---

## **Instructions to Run the Application**  
1. Clone the repository and open the project in IntelliJ IDEA.  
2. Run the application using the IDE's **Run** button or a keyboard shortcut.  
3. Access the application at: [http://localhost:8080](http://localhost:8080)  

---

## API Documentation
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## **Database Migration**  
### Automatic Migration  
This application uses **Flyway** for database migrations.  
- Migration scripts are located in:  
  `src/main/resources/db/migration/`  
- These scripts are automatically applied when the application starts.  

---

## **Implementation Details**  
### **Time Handling**  
- All dates and times are managed using `LocalDateTime` in **UTC** for simplicity.  
- Timestamps must follow **ISO 8601** format.  

### **Event Scheduling Rules**  
- Events conflict if their times **overlap**, but not if one ends exactly when another starts.  
- Events with a **start time equal to the end time** are invalid.  

### **Validation and Error Handling**  
- Input is validated using Spring annotations, such as `@NotNull` and `@Size`.  
- Custom exception handling ensures consistent **JSON responses** for errors.  

### **API Design**  
- The API structure was defined using an **OpenAPI YAML file** (contract-first approach).  
- **GET Endpoint**: Combined filtering and retrieving of events with optional query parameters to align with RESTful principles.  

### **Database Design**  
- **Schema Migrations**: Managed with Flyway.  
- **Indexes**: Added on start and end times for improved query performance on large datasets.  

### **Testing Approach**  
- Followed a **test pyramid strategy**:  
  - **Service Tests**: Most coverage.  
  - **Controller Tests**: Limited.  
  - **Repository Tests**: None.  
- Tests include:  
  - Overlapping edge cases.  
  - Service methods and API responses.  

### **Implementation Choices**  
- **EventDto**: Used for data transfer to separate API concerns from database entities.  
- **Chained Method Structure**: Simplified controller responses for cleaner code.  
- Overlap detection logic is implemented in the **service layer** to keep controllers lightweight.  

---

## **Assumptions**  
- **Time Zones**: Handled in UTC to avoid complexity.  
- **Event Conflicts**: Defined by overlapping times, excluding exact start-to-end transitions.  
