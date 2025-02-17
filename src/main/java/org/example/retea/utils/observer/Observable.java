package org.example.retea.utils.observer;
import org.example.retea.utils.events.Event;

public interface Observable <E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
