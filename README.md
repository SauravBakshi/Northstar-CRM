# Northstar CRM

A full-stack customer relationship management app built with React, Vite, Tailwind CSS, Spring Boot 3, Java 17, Spring Security, JWT, JPA, and MySQL.

## Requirements

- Java 17
- Maven 3.8+
- Node.js 20+
- MySQL 8+

## Run locally

1. Create a MySQL database: `CREATE DATABASE northstar_crm;`
2. In `backend`, copy `.env.example` values into your environment (or set the corresponding `SPRING_DATASOURCE_*` and `JWT_SECRET` variables).
3. Start the API: `cd backend && mvn spring-boot:run`
4. Start the UI in another terminal: `cd frontend && npm install && npm run dev`
5. Visit `http://localhost:5173`. Register a user; public signup always assigns EMPLOYEE. Promote the first user to ADMIN directly in the database if needed.

The frontend uses live API calls when authenticated and a polished sample workspace before login so the dashboard can be previewed immediately. Preview entries are local examples; signed-in records persist to MySQL. The dashboard and report charts use live workspace totals, deal creation history, lead sources, and task counts.

## First administrator

Public registration always creates an EMPLOYEE account. After registering the first account, promote it once using MySQL Workbench or the MySQL CLI:

```sql
UPDATE app_users SET role = 'ADMIN' WHERE email = 'you@example.com';
```

Sign out and back in. Admins can create team accounts, change team roles, and remove other members from the Team page; the API enforces those permissions and prevents an admin from removing their own access. Task assignment can be given to an existing team member.

## Security

Passwords are BCrypt hashed. JWTs expire after 24 hours by default. Signup cannot create an ADMIN. Role restrictions are enforced by Spring Security and service-level ownership checks. Configure `JWT_SECRET` with a random secret of at least 32 bytes in production. Use HTTPS and set the database credentials through environment variables.

## Modules

Dashboard, customers, leads, deals, tasks, follow-ups, users, and reports. Customer, lead, and deal records can be created, edited, and deleted within role permissions. Tasks can be created, assigned, completed, edited, and deleted. Dashboard charts use Recharts; forms use React Hook Form; navigation uses React Router.
