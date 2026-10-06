package com.ceylonstar.showtime.controller;

import com.ceylonstar.showtime.model.Showtime;
import com.ceylonstar.showtime.service.ShowtimeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {

    /** Fixed set of halls at Ceylon Star Cinemas, per the requirements gathering (Hall 1, Hall 2, VIP Hall, etc.). */
    private static final List<String> HALLS = Arrays.asList("Hall 1", "Hall 2", "Hall 3", "VIP Hall");

    private final ShowtimeService service;

    public ShowtimeController(ShowtimeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Showtime> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Showtime getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/halls")
    public List<String> getHalls() {
        return HALLS;
    }

    @PostMapping
    public ResponseEntity<Showtime> create(@Valid @RequestBody Showtime showtime) {
        Showtime saved = service.create(showtime);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public Showtime update(@PathVariable Long id, @Valid @RequestBody Showtime showtime) {
        return service.update(id, showtime);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
