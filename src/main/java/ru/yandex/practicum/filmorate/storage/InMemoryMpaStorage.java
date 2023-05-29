package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public class InMemoryMpaStorage implements MpaStorage {
    @Override
    public Optional<Mpa> getMpa(int id) {
        return Optional.empty();
    }

    @Override
    public Collection<Mpa> getAllMpas() {
        return null;
    }
}
