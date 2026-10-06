package com.cinema.movie_reservation_system.model;

/**
 * Model representing a Movie in the catalog.
 * Part of Movie Catalog & Details Management (IT25101311).
 */
public class Movie {

    private Long id;
    private String title;
    private String genre;
    private Integer durationMins;
    private String rating;
    private String posterUrl;
    private String description;
    private String status = "ACTIVE"; // ACTIVE or ARCHIVED

    public Movie() {
    }

    public Movie(String title, String genre, Integer durationMins, String rating, String posterUrl, String description, String status) {
        this.title = title;
        this.genre = genre;
        this.durationMins = durationMins;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.description = description;
        this.status = (status != null && !status.isBlank()) ? status.toUpperCase() : "ACTIVE";
    }

    public Movie(Long id, String title, String genre, Integer durationMins, String rating, String posterUrl, String description, String status) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.durationMins = durationMins;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.description = description;
        this.status = (status != null && !status.isBlank()) ? status.toUpperCase() : "ACTIVE";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getDurationMins() {
        return durationMins;
    }

    public void setDurationMins(Integer durationMins) {
        this.durationMins = durationMins;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = (status != null && !status.isBlank()) ? status.toUpperCase() : "ACTIVE";
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", durationMins=" + durationMins +
                ", rating='" + rating + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
