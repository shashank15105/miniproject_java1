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
## ▶️ How to Run the Project

This project requires **two terminals**:
one for the database (MySQL) and one for the backend (Spring Boot).

---

### 🟢 Terminal 1 — MySQL (Database)

1. Start MySQL:

```bash
brew services start mysql
```

2. Open MySQL:

```bash
mysql -u root
```

3. Select the database:

```sql
USE coworking_space;
```

4. (Optional) Verify data:

```sql
SHOW TABLES;
SELECT * FROM workspaces;
SELECT * FROM bookings;
```

---

### 🟢 Terminal 2 — Backend (Spring Boot)

1. Navigate to project folder:

```bash
cd /Users/shashankbhata/Pgrms/SQL_mini_project
```

2. Run the application:

```bash
mvn spring-boot:run
```

3. Wait for:

```text
Tomcat started on port 8080
```

---

### 🌐 Open in Browser

```
http://localhost:8080
```

---

### 🔁 How It Works

```
Browser (UI)
   ↓
Spring Boot Backend
   ↓
MySQL Database
```

---

### 🧪 Test the Application

1. Open the website
2. Enter your name and booking details
3. Book a workspace

Then check database:

```sql
SELECT * FROM bookings;
```

---

### ❗ Troubleshooting

**Port 8080 already in use**

```bash
lsof -i :8080
kill -9 <PID>
```

**MySQL not running**

```bash
brew services start mysql
```

**Database not found**

```sql
CREATE DATABASE coworking_space;
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

