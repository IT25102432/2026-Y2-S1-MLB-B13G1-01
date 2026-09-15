package com.cinema.movie_reservation_system.repository;

import com.cinema.movie.entity.Movie;
import com.cinema.movie.entity.MovieStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a single combined Specification<Movie> from optional search
 * criteria. Any parameter left null is simply skipped, so this one
 * specification powers /movies (no filters), /movies?title=..,
 * /movies?genre=.., and any combination thereof.
 */
public final class MovieSpecification {

    private MovieSpecification() {
    }

    public static Specification<Movie> filterBy(String title, String genre, String language, MovieStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (genre != null && !genre.isBlank()) {
                predicates.add(cb.isMember(genre.toUpperCase(), root.get("genres")));
            }
            if (language != null && !language.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("language")), language.toLowerCase()));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Avoid duplicate rows caused by the genres element-collection join
            if (query != null) {
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
