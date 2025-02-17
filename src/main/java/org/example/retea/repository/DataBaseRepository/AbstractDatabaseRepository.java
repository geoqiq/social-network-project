package org.example.retea.repository.DataBaseRepository;

import org.example.retea.domain.Entity;
import org.example.retea.domain.validators.Validator;
import org.example.retea.repository.Repository;

import java.util.Optional;

public abstract class AbstractDatabaseRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    protected Validator validator;
    protected AccesDatabase data;
    protected String tabel;

    public AbstractDatabaseRepository(Validator validator, AccesDatabase data, String tabel)
    {
        this.validator = validator;
        this.data = data;
        this.tabel = tabel;
    }

    @Override
    public abstract Optional<E> salveaza(E entity);

    @Override
    public abstract Optional<E> actualizeaza(E entity);

    @Override
    public abstract Optional<E> sterge(ID id);

    @Override
    public abstract Iterable<E> gasesteToate();

    @Override
    public abstract Optional<E> gasesteUnul(ID id);
}
