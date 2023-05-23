package ru.yandex.practicum.filmorate.model;

public enum Rating {
    G("G"),
    PG("PG"),
    PG13("PG-13"),
    R("R"),
    NC17("NC-17");

    private final String rating;

    Rating(String rating) {
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public static Rating fromString(String ratingString) {
        for (Rating rating : Rating.values()) {
            if (rating.getRating().equalsIgnoreCase(ratingString)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Invalid rating string: " + ratingString);
    }
}
