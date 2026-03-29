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

Update if needed:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/coworking_space
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
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
