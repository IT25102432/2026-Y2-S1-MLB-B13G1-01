# Showtime Desk — Ceylon Star Cinemas

A Spring Boot module for **Movie & Showtime Management**, built for the SE2030
MovieSphere Cinema Reservation Portal (Lab Sheet 01). It demonstrates full
CRUD (Create, Read, Update, Delete) for cinema showtimes, with server-side
validation that prevents double-booking a hall ("collision-free scheduling").

## Run it in IntelliJ

1. **File → Open…** and select the `showtime-management` folder (the one
   containing `pom.xml`). IntelliJ will detect it as a Maven project and
   download the dependencies automatically — this needs an internet
   connection the first time.
2. Open `ShowtimeManagementApplication.java`
   (`src/main/java/com/ceylonstar/showtime/`) and click the green ▶ run
   arrow next to the class, or right-click it → **Run**.
3. Once the console shows `Started ShowtimeManagementApplication`, open
   **http://localhost:8080** in your browser.

No database installation is needed — it uses an in-memory H2 database that
resets each time you restart the app, pre-loaded with a few sample
showtimes so the screen isn't empty for a demo.

### Alternative: command line

```bash
mvn spring-boot:run
```

## What to demo

| CRUD operation | Where in the UI |
|---|---|
| **Create** | "Schedule a showtime" button (top right) |
| **Read** | The ticket grid, plus the search box and Hall / Status / Date filters |
| **Update** | "Edit" button on any ticket card |
| **Delete** | "Delete" button on any ticket card → confirmation dialog |

**To demonstrate the collision check:** try scheduling two showtimes in the
same hall on the same date with overlapping times — the form will reject it
with a clear message naming the conflicting movie and time (a 15-minute
cleaning/entry turnaround is enforced between back-to-back screenings in the
same hall).

## Project structure

```
src/main/java/com/ceylonstar/showtime/
  model/        Showtime entity, ShowStatus enum
  repository/   Spring Data JPA repository
  service/      Business logic + hall/time collision validation
  controller/   REST API (/api/showtimes)
  exception/    Custom exceptions + a global @RestControllerAdvice handler
  config/       Startup data seeder (sample showtimes)

src/main/resources/
  application.properties   H2 datasource config
  static/index.html        Single-page UI
  static/css/style.css     Styling
  static/js/app.js         Fetch-based CRUD calls, filtering, modal, toasts
```

## API reference

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/showtimes` | List all showtimes |
| GET | `/api/showtimes/{id}` | Get one showtime |
| GET | `/api/showtimes/halls` | List available halls |
| POST | `/api/showtimes` | Create a showtime |
| PUT | `/api/showtimes/{id}` | Update a showtime |
| DELETE | `/api/showtimes/{id}` | Delete a showtime |

You can also browse the raw data at **http://localhost:8080/h2-console**
(JDBC URL: `jdbc:h2:mem:moviesphere`, user `sa`, no password) while the app
is running.
