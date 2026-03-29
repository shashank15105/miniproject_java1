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
🧭 HOW TO RUN THE PROJECT (2 TERMINALS SETUP)

👉 You need 2 terminals running simultaneously

🟢 TERMINAL 1 — DATABASE (MySQL)
Step 1: Start MySQL
brew services start mysql
Step 2: Open MySQL
mysql -u root
Step 3: Use database
USE coworking_space;
Step 4: Check data (optional)
SHOW TABLES;

SELECT * FROM workspaces;
SELECT * FROM bookings;

👉 Keep this terminal open to monitor DB

🟢 TERMINAL 2 — RUN BACKEND (Spring Boot)
Step 1: Go to project
cd /Users/shashankbhata/Pgrms/SQL_mini_project
Step 2: Run app
mvn spring-boot:run

👉 You’ll see logs like:

Tomcat started on port 8080
🌐 STEP 3 — OPEN WEBSITE

Open browser:

http://localhost:8080
🔁 HOW BOTH TERMINALS WORK TOGETHER
Browser (UI)
   ↓
Spring Boot (Terminal 2)
   ↓
MySQL (Terminal 1)
🧪 LIVE TEST FLOW
👉 1. Book a workspace (in browser)
Enter name
select workspace
choose time
click book
👉 2. Check database (Terminal 1)
SELECT * FROM bookings;

👉 You’ll see new booking row 🔥

⚠️ COMMON ISSUES
❌ Port 8080 busy
lsof -i :8080
kill -9 <PID>
❌ MySQL not running
brew services start mysql

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

