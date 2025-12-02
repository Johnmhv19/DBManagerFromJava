# MyDBMagic - PostgreSQL Database Management Tool

A simple, educational PostgreSQL database management utility designed for students and educators. MyDBMagic provides an easy-to-use interface for connecting to PostgreSQL databases and performing common operations without writing complex SQL.

## 🎯 Purpose

This project is designed to help students and teachers:
- Learn database concepts through hands-on practice
- Quickly set up and manage PostgreSQL databases
- Understand table structures and relationships
- Practice database operations in a safe, controlled environment

## 📋 Features

- **Easy Database Connection**: Connect to PostgreSQL with default or custom settings
- **Interactive Table Creation**: Step-by-step table creation with field definitions
- **Table Management**: View, create, and delete tables
- **Data Exploration**: View table structures and records
- **User Authentication**: Built-in login system support
- **Educational Focus**: Clear, understandable code structure for learning

## 🏗️ Project Structure

### 1. `MyDBMagic.java` - Core Database Management Class

The heart of the application that handles all database operations:

**Key Features:**
- Database connection management
- Table creation with interactive prompts
- CRUD operations (Create, Read, Update, Delete)
- Metadata retrieval for table structures
- User authentication system
- Comprehensive error handling and logging

**Main Methods:**
- `connectDB()` - Establishes PostgreSQL connection
- `createTable()` - Interactive table creation
- `showTables()` - Lists all tables in database
- `showTableFields()` - Displays table structure
- `showRecords()` - Shows all records in a table
- `deleteTable()` - Safely removes tables
- `login()` - User authentication

### 2. `MainDB.java` - User Interface and Application Controller

Provides a console-based menu system for interacting with the database:

**Features:**
- User-friendly menu interface
- Input validation and error handling
- Connection setup (default or custom)
- Safe table deletion with confirmation
- Comprehensive testing options

**Menu Options:**
1. Connect to database
2. Create table
3. Delete table
4. Show table structure
5. Show all tables
6. Show table records
7. Test login system
9. Exit

### 3. `Field.java` - Database Field Representation

Represents database table columns with their properties:

**Features:**
- Field name and data type management
- Constraint handling (PRIMARY KEY, NOT NULL)
- SQL generation for table creation
- Method chaining for fluent API
- Type validation and checking

**Usage Example:**
```java
Field idField = new Field("id", "INTEGER").primaryKey();
Field nameField = new Field("name", "VARCHAR").maxLength(100).notNull();
