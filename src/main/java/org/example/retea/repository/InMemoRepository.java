package org.example.retea.repository;

import org.example.retea.domain.Entity;
import org.example.retea.domain.validators.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class InMemoRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private Validator<E> validator;
    Map<ID,E> entitati;

    public InMemoRepository(Validator<E> validator)
    {
        this.validator = validator;
        entitati = new HashMap<ID,E>();
    }

    public Validator<E> getValidator()
    {
        return validator;
    }
    public void setValidator(Validator<E> validator)
    {
        this.validator = validator;
    }
    public Map<ID, E> getEntitati()
    {
        return entitati;
    }
    public void setEntitati(Map<ID, E> entitati)
    {
        this.entitati = entitati;
    }

    @Override
    public Optional<E> gasesteUnul(ID id)
    {
        Predicate<ID> idNotNull = idValue -> idValue != null;
        if (!idNotNull.test(id))
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        return Optional.ofNullable(entitati.get(id));
    }

    @Override
    public Iterable<E> gasesteToate() {
        return entitati.values();
    }

    @Override
    public Optional<E> salveaza(E entity)
    {
        Predicate<E> entityNotNull = e -> e != null;
        Predicate<ID> idNotNull = id -> id != null;

        if (!entityNotNull.test(entity)) {
            throw new IllegalArgumentException("Entity nu poate fi null.");
        }

        if (!idNotNull.test(entity.getId())) {
            throw new IllegalArgumentException("ID-ul nu poate fi null.");
        }

        validator.validate(entity);
        return Optional.ofNullable(entitati.putIfAbsent(entity.getId(), entity));
    }

    @Override
    public Optional<E> sterge(ID id)
    {
        Predicate<ID> idNotNull = idValue -> idValue != null;
        if (!idNotNull.test(id))
            throw new IllegalArgumentException("ID-ul nu poate fi null.");
        return Optional.ofNullable(entitati.remove(id));
    }

    @Override
    public Optional<E> actualizeaza(E entity)
    {
        Predicate<E> entityNotNull = e -> e != null;
        Predicate<ID> idNotNull = id -> id != null;

        if (!entityNotNull.test(entity)) {
            throw new IllegalArgumentException("Entity nu poate fi null.");
        }

        if (!idNotNull.test(entity.getId())) {
            throw new IllegalArgumentException("ID-ul nu poate fi null.");
        }

        validator.validate(entity);
        return Optional.ofNullable(entitati.compute(entity.getId(), (id, existingEntity) -> {
            if (existingEntity != null) {
                return entity;
            } else { return existingEntity; }
        }));
    }
}
