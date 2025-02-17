package org.example.retea.utils.events;

import org.example.retea.domain.Prietenie;

public class StatusPrietenieEvent implements Event{
    private StatusPrietenieType tip;
    private Prietenie prietenie;
    public StatusPrietenieEvent(StatusPrietenieType tip, Prietenie prietenie)
    {
        this.tip = tip;
        this.prietenie = prietenie;
    }

    public StatusPrietenieType getTip() {
        return tip;
    }
    public void setTip(StatusPrietenieType tip) {
        this.tip = tip;
    }
    public Prietenie getPrietenie() {
        return prietenie;
    }
    public void setPrietenie(Prietenie prietenie) {
        this.prietenie = prietenie;
    }
}
