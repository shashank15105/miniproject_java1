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
