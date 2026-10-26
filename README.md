# Employee Payroll System (Java)

A beginner-friendly Java Swing desktop project with MySQL storage and JDBC.

## Features
- Add, view, update, and delete employee records
- Calculate net salary = basic salary + allowances - deductions
- Generate a payslip preview and print it
- Validate required fields and numeric salary inputs

## Requirements
- JDK 17+
- MySQL Server
- Maven
- NetBeans, IntelliJ IDEA, or another Java IDE

## Setup
1. Install Java JDK 17 or later, MySQL Server, and Maven.
2. Open MySQL Workbench or a MySQL command-line client.
3. Run the SQL script in `database.sql` to create the database and table.
4. Open `src/main/java/com/college/payroll/DBConnection.java`.
5. Change `USER` and `PASSWORD` to match your local MySQL account. Do not use your real password in a public repository.
6. Open a terminal in the folder containing `pom.xml` and run:
   `mvn clean compile`
7. Run the main class `com.college.payroll.PayrollApp` from your IDE, or run:
   `mvn exec:java -Dexec.mainClass=com.college.payroll.PayrollApp`
   If Maven says the exec plugin is unavailable, run the main class directly from your IDE.

## How to use
- Enter name, department, designation, basic salary, allowances, and deductions.
- Click **Add Employee** to save a record.
- Select a row to load its details into the form.
- Click **Update Selected ID** to save edits, or **Delete ID** to remove that employee.
- Select/load an employee ID and click **Generate Payslip**.
- Click **Print Payslip** to open the system print dialog.

## Salary example
Basic = ₹25,000; allowances = ₹3,000; deductions = ₹1,000.
Net salary = ₹25,000 + ₹3,000 - ₹1,000 = ₹27,000.

## Notes
- This is an educational demo. It does not include tax, provident fund, overtime, attendance-linked pay, or statutory payroll compliance.
- Database credentials are local configuration. Keep them private.
