# Energy Consumption Optimizer

---

## Introduction:

The Energy Consumption Optimizer is a web application developed as part of a bachelor's thesis project at TH Lübeck, in collaboration with adesso company. This tool is designed to empower consumers by optimizing the operation timing of household appliances based on dynamic electricity pricing. By leveraging real-time electricity price predictions from the energy spot market, the app provides both financial and ecological benefits to users.
---

## Features:

- **Real-time Price Analysis**: The app fetches current electricity pricing data and suggests the most cost-effective times for operating devices.
  
- **Environmental Impact**: Along with cost-saving recommendations, users get insights into their CO2 emissions and the proportion of renewable energy in their consumption.
---

## Technologies Used:

- **Backend**: Java 19, Spring Boot, Hibernate, JPA Repository
- **Database**: PostgreSQL
- **Authentication**: Spring Security with JWT
- **Data Mapping**: MapStruct, Lombok
- **External Data Sources**: Various external APIs
- **Task Scheduling**: ScheduledExecutorService

---

## Prerequisites
### mandatory: 
- Java version 19 
- Apache Maven  
- Docker  

### optional (recommended):
- DBeaver 
---

## Set-up
Ensure you have the Docker daemon or Docker Desktop running before proceeding.

1. **Clone the repository**: 
    `git clone https://github.com/majdi-ghni/energy-consumption-optimizer.git`

2. **Navigate to the project directory**:
    `cd energy-consumption-optimizer`
3. ** Create and start the docker container for the database by executing the following command:
   `docker-compose up --build`
   
### DBeaver setup & Database connection

Create a new connection of type `PostgreSQL`.
The database `energy_consumption_optimizer` will be accessible at `localhost` on port `5432`.
The password for the user `root` is `changeme`.

--------

## Getting Started:
1. **Navigate to the project directory**:
    `cd energy-consumption-optimizer`

2. **Install dependencies**:
    `mvn install`

3. **Run the application**:
    `mvn spring-boot:run`
