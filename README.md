# Product List Parser Service

## Overview
This project is a Spring Boot application that implements a parser service designed to handle product lists in Excel format. The service facilitates the upload of `.xlsx` files containing product information, extracting relevant data and storing it in a database.

### Key Features
- **Data Parsing**: The service parses incoming Excel files to extract product details such as SKU, title, price, and quantity.
- **Database Integration**: Extracted product data is stored in a database, allowing for efficient data management and retrieval.
- **Change Detection**: When a `.xlsx` file is uploaded (whether with the same name or a different name), the system identifies:
  - **Unchanged Rows**: Rows that remain the same between uploads.
  - **Updated Rows**: Rows that have modified data.
  - **Newly Added Rows**: Rows that are present in the new upload but not in the database.
  
Only the updated and newly added rows are reflected in the database, ensuring efficient storage and minimizing unnecessary updates. This functionality allows users to manage product data effectively and keep the database synchronized with the latest information from uploaded files.

## Technologies Used
- **Java**: 17
- **Spring Boot**: 3.3.4
- **Apache POI**: For reading Excel files
- **Database**: MySQL
- **Docker**

## Project Structure
- **src/main/java**: Contains the main application code.
- **src/test/java**: Contains unit tests.
- **src/test/resources**: Contains test resources such as Excel files.
- **src/main/resources**: Contains application configuration files.
- **JavaProductParser.postman_collection.json**: A collection of API requests for testing with Postman.

## API Endpoints

### Upload File
- **Endpoint**: `POST /api/product/upload`
- **Description**: Uploads an Excel file containing product data and processes it to update the database. Returns a summary of changes made. 
  - For testing and development, you can use the following sample files:
    - [Product List (Initial)](https://github.com/Sakib58/JavaProductParser/blob/main/product_list.xlsx)
    - [Product List (Changed - 2 Rows Added, 3 Updated)](https://github.com/Sakib58/JavaProductParser/blob/main/product_list_changed_2_rows_added_3.xlsx)
    - [Product List (Changed - 3 Rows Updated)](https://github.com/Sakib58/JavaProductParser/blob/main/product_list_changed_3_rows.xlsx)
- **Request**: Form-data with the key `file` (must be an `.xlsx` file).
- **Response**: Returns a `ProductChangeSummaryDto` containing details about updated and newly added products.


### Get Change Histories
- **Endpoint**: `GET /api/product/change-histories`
- **Description**: Retrieves the history of changes made to the products from previously uploaded files.
- **Response**: Returns a list of `ProductChangeSummary` objects detailing changes.

### Get Product Info by SKU
- **Endpoint**: `GET /api/product/product-by-sku`
- **Description**: Fetches product information based on the provided SKU (Stock Keeping Unit).
- **Request**: Query parameter `sku` (the SKU of the product).
- **Response**: Returns a `ProductDto` with the details of the specified product or a 404 error if not found.

### Get All Products
- **Endpoint**: `GET /api/product/all`
- **Description**: Retrieves a list of all products stored in the database.
- **Response**: Returns a list of `ProductDto` objects representing all products.


## Setting Up the Project

### Prerequisites
- Java 17
- Maven
- Docker

# Running the Project

## Running Locally
1. Clone the repository:
   ```bash
   git clone https://github.com/Sakib58/JavaProductParser.git
   cd <project-directory>
   ```
2. Change the database configurations in the `src/main/resources/application.properties` file to connect to your local database. Update the following properties:
   
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```
4. Build the JAR File:
   ```bash
   mvn clean package
   ```
5. Run the Application:
   ```bash
   mvn spring-boot:run
   ```
6. Use the Postman collection for testing the API. You can find the collection [here](https://github.com/Sakib58/JavaProductParser/blob/main/JavaProductParser.postman_collection.json).

## Running with Docker
1. Build the JAR File (skipping tests if necessary):
   ```bash
   mvn clean package -DskipTests
   ```
2. Run the Docker Containers:
   ```bash
   docker-compose up --build
   ```
3. Use the Postman collection for testing the API. You can find the collection [here](https://github.com/Sakib58/JavaProductParser/blob/main/JavaProductParser.postman_collection.json).

4. To stop and remove the Docker containers, run:
    ```bash
    docker-compose down
    ```

