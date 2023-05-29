package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {
    Optional<Mpa> getMpa(int id);

    Collection<Mpa> getAllMpas();
}
