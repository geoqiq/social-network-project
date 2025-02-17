package org.example.retea.domain;

import org.example.retea.utils.events.StatusPrietenieType;

import java.util.Objects;

public class Invitatie extends Entity<Tuplu<Long,Long>>{

    private Long id1;
    private Long id2;
    private StatusPrietenieType status;

    public Invitatie(Long id1, Long id2, StatusPrietenieType status) {
        this.id1 = id1;
        this.id2 = id2;
        this.status = status;
        Tuplu<Long,Long> IDComb=new Tuplu<>(id1,id2);
        this.setId(IDComb);
    }

    public Long getId1() {
        return id1;
    }
    public void setId1(Long id1) {
        this.id1 = id1;
    }
    public Long getId2() {
        return id2;
    }
    public void setId2(Long id2) {
        this.id2 = id2;
    }
    public String getStatus() {
        return status.toString();
    }
    public void setStatus(StatusPrietenieType status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Invitatie invitatie_aux = (Invitatie) o;
        return Objects.equals(id1, invitatie_aux.id1) && Objects.equals(id2, invitatie_aux.id2) && Objects.equals(status, invitatie_aux.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id1, id2, status);
    }

    @Override
    public String toString() { return "Invitatie{" + "id1 = " + id1 + ", id2 = " + id2 + ", status = '" + status + '\'' + '}'; }
}
