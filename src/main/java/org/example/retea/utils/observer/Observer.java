package org.example.retea.utils.observer;

import org.example.retea.service.Service;
import org.example.retea.utils.events.Event;

public interface Observer <E extends Event> {
    void update(E e, Service serviceActualizat);
}