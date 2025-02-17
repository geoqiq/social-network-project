package org.example.retea.repository.DataBaseRepository;

import org.example.retea.domain.Entity;
import org.example.retea.repository.Repository;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> gasesteToatePage(Pageable pageable);
}
