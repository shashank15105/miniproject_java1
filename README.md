# Co-Working Space Management System

Spring Boot + JDBC + MySQL + HTML/CSS/JavaScript application for booking workspaces locally on `http://localhost:8080`.

## Main Features

- Auto-creates the `coworking_space` database on startup
- Auto-creates `users`, `workspaces`, and `bookings` tables
- Auto-inserts sample rows for `U1`, `U2`, `W1`, and `W2`
- Lists available workspaces
- Books a workspace with time-based pricing
- Shows booking history for a user

## Run

```bash
cd /Users/shashankbhata/Pgrms/SQL_mini_project
mvn spring-boot:run
```

## Database Defaults

- Database: `coworking_space`
- Username: `root`
- Password: empty

Override if needed with:

```bash
export COWORKING_DB_URL="jdbc:mysql://localhost:3306/coworking_space?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"
export COWORKING_DB_USERNAME="root"
export COWORKING_DB_PASSWORD=""
```

## API Endpoints

- `GET /workspaces`
- `POST /book`
- `GET /bookings?userId=U1`

## ⚙️ Prerequisites

Before running this project, make sure you have the following installed:

* **Java JDK 17 or above**
* **Maven 3.8+**
* **MySQL Server 8+**
* **Git**
* A modern browser (Chrome, Edge, etc.)

---

## 🛠 Installation Steps

### 1. Clone the repository

```bash
git clone https://github.com/shashank15105/miniproject_java1.git
cd miniproject_java1
```

---

### 2. Start MySQL

Make sure MySQL server is running.

```bash
brew services start mysql
```

---

### 3. Create Database

Open MySQL:

```bash
mysql -u root
```

Run:

```sql
CREATE DATABASE coworking_space;
```

---

### 4. Configure Database Connection

Open:

```text
src/main/resources/application.properties
```

---

### 5. Run the Application

```bash
mvn spring-boot:run
```

---

### 6. Open in Browser

```
http://localhost:8080
```

---

## 🧪 Sample Data

The app auto-creates sample data on startup:

* Users (auto-created when booking)
* Workspaces (pre-seeded)

---

## ❗ Troubleshooting

### Port already in use

```bash
lsof -i :8080
kill -9 <PID>
```

### MySQL connection error

* Check if MySQL is running
* Verify username/password

### Maven not found

```bash
brew install maven
```

