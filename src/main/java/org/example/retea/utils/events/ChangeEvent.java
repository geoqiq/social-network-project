package org.example.retea.utils.events;

import org.example.retea.domain.Entity;

public class ChangeEvent<E extends Entity> implements Event {
    private ChangeEventType tip;
    private E data, dataVeche;

    public ChangeEvent(ChangeEventType tip, E data)
    {
        this.tip = tip;
        this.data = data;
    }

    public ChangeEvent(ChangeEventType tip, E data, E dataVeche)
    {
        this.tip = tip;
        this.data = data;
        this.dataVeche = dataVeche;
    }

    public ChangeEventType getTip() {
        return tip;
    }
    public E getData() {
        return data;
    }
    public E getDataVeche() {
        return dataVeche;
    }
}