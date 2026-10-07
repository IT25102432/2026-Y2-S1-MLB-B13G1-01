# Seating Layout & Hall Allocation Module

This folder contains only the **Cinema Hall Allocation and Seating Layout** part separated from the Movie Reservation System.

## Included backend files

- `HallController.java` — Hall CRUD, seat layout retrieval, seat updates.
- `HallService.java` — Hall validation, automatic capacity calculation, automatic seat generation, seat type/status updates, layout regeneration and hall deletion rules.
- `CinemaHall.java` — Hall entity.
- `Seat.java` — Seat entity.
- `CinemaHallRepository.java` — Hall database access.
- `SeatRepository.java` — Seat database access.

## Included frontend files

- `Halls.jsx` — Hall management UI and visual seating layout.
- `services/api.js` — Axios API configuration used by the page.
- `styles/halls.css` — Hall/seat-specific styles extracted from the main stylesheet.

## Main functions in this module

1. Create a cinema hall.
2. Edit hall name, row count, seats-per-row and active status.
3. Automatically calculate hall capacity.
4. Automatically generate seats such as `A1`, `A2`, `B1`, etc.
5. View the seating layout for each hall.
6. Change seat type: `STANDARD`, `PREMIUM`, `RECLINER`.
7. Change seat status: `AVAILABLE`, `HELD`, `BOOKED`, `OUT_OF_ORDER`.
8. Prevent duplicate hall names.
9. Validate maximum rows, columns and total capacity.
10. Regenerate seat layout safely when hall dimensions change.
11. Delete a hall only when deletion rules allow it.

## API endpoints

- `POST /api/halls`
- `GET /api/halls`
- `GET /api/halls/{id}`
- `PUT /api/halls/{id}`
- `DELETE /api/halls/{id}`
- `GET /api/halls/{id}/seats`
- `PUT /api/halls/seats/{seatId}`

## How to merge into the team repository

Copy the files into the matching locations in the main project. The package names and frontend paths are already kept in the same structure as the original Movie Reservation System.

The frontend page uses shared styles/classes from the main project as well as the included `styles/halls.css`, so it is intended to be merged into the existing React frontend rather than run as a separate standalone app.

## Suggested Git commit

```bash
git add .
git commit -m "Implement cinema hall allocation and seating layout management"
git push origin <your-branch-name>
```
