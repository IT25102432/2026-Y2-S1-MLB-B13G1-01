# MovieSphere – Showtime Management Service

A runnable Spring Boot application implementing the **Showtime Management** piece
(requirement 5.2) of the Web-Based Movie Reservation System: collision-free
scheduling so no hall is ever double-booked for overlapping times.

## Requirements

- JDK 17+
- Maven (IntelliJ bundles one — no separate install needed)

## Run it in IntelliJ

1. **File → Open** and select this project folder (the one with `pom.xml`).
2. Let IntelliJ index and download dependencies (bottom-right progress bar).
3. Open `MovieReservationSystemApplication.java` and click the green ▶ run icon,
   or right-click the file → **Run**.
4. Console should show `Tomcat started on port 8080`.

No database setup needed — it uses an in-memory H2 database that resets on
restart. Swap to MySQL later by following the comment block at the bottom of
`application.properties`.

## Demo frontend

Open **`http://localhost:8080`** in a browser once the app is running — that
serves `src/main/resources/static/index.html`, a schedule board for the
current date: one row per hall, showtimes drawn as blocks positioned by
start/end time. It talks to the REST API on the same origin, so there's no
CORS setup needed.

All four CRUD operations are reachable from the UI:

- **Create** — **+ New showtime** opens a form (movie, hall, date, start
  time, duration, price). Duration and a suggested price prefill from the
  picked movie/hall, both editable.
- **Read** — the board itself (`GET /api/showtimes`), plus clicking a block
  for its full detail popover.
- **Update** — click a block → **Edit** opens the same form pre-filled,
  submitting a `PUT` to `/api/showtimes/{id}` instead of a `POST`.
- **Delete** — click a block → **Cancel** → confirm inline. The block fades
  off the board immediately, and cancelled showtimes no longer come back on
  reload (the backend now excludes `CANCELLED` rows from every listing
  endpoint — they used to still return and redraw, which was the earlier bug).

Try scheduling (or editing) a showtime into a time that overlaps another one
in the same hall — the board surfaces the backend's `409 Conflict` message
inline in the form instead of saving, which is the actual demo of the
collision-free scheduling logic.

### Small UX touches worth noting when you demo

- Cancelling asks for confirmation inline in the popover (no native browser
  `confirm()` dialog) before it removes anything.
- The showtime you just created or edited gets a brief highlight pulse on
  the board, so it's obvious which block just changed.
- The Save/Schedule button disables and shows "Saving…" while a request is
  in flight, so a slow network can't produce a double-submit.
- A **Today** button next to the date field jumps back to the current date.
- Escape closes whichever modal or popover is open.
- `Movie` and `CinemaHall` aren't modeled in the backend yet, so the frontend
  keeps a small local catalog (6 fictional films, 4 halls incl. a VIP hall)
  just to give the ids a name and a color on the board.

## Try the API directly

- H2 console (to inspect the `showtimes` table): `http://localhost:8080/h2-console`
  — JDBC URL `jdbc:h2:mem:moviesphere`, user `sa`, blank password.
- Create a showtime:

  ```
  POST http://localhost:8080/api/showtimes
  Content-Type: application/json

  {
    "movieId": 1,
    "hallId": 1,
    "showDate": "2026-10-01",
    "startTime": "18:00:00",
    "durationMinutes": 120,
    "ticketPrice": 1200.00
  }
  ```

- Try creating a second showtime in the same hall/date that overlaps
  (e.g. `startTime: 19:00:00`) — you should get back `409 Conflict` with a
  message naming the clash.
- List showtimes: `GET http://localhost:8080/api/showtimes?date=2026-10-01`

You can test these with Postman, curl, or IntelliJ's built-in HTTP client
(create a `.http` file and paste the requests above).

## Project layout

```
src/main/java/com/moviesphere/showtime/
  MovieReservationSystemApplication.java   - entry point
  entity/Showtime.java                     - JPA entity
  dto/                                      - request/response payloads
  repository/ShowtimeRepository.java       - overlap-detection query
  service/ShowtimeService.java             - scheduling/collision logic
  controller/ShowtimeController.java       - REST endpoints
  exception/                               - custom exceptions + global handler
src/main/resources/application.properties  - DB + server config
src/main/resources/static/index.html       - demo frontend (schedule board)
src/test/                                  - smoke test
```

## Not included

`Movie` and `CinemaHall` entities aren't modeled yet — `Showtime` references
them by plain `Long` id so this module runs standalone. Add those entities
(and other stakeholders' modules — payments, notifications, auth) as your
group builds out the rest of the system.
