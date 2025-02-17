package org.example.retea.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Mesaj extends Entity<Long>{

    private Long dela;
    private Long catre;
    private String mesaj;
    private LocalDateTime data;
    private Long raspuns;

    public Mesaj(Long dela, Long catre, String mesaj, LocalDateTime data) {
        this.dela = dela;
        this.catre = catre;
        this.mesaj = mesaj;
        this.data = data;
        this.raspuns = null;
    }

    public Long getDela() {
        return dela;
    }
    public void setDela(Long dela) {
        this.dela = dela;
    }
    public Long getCatre() {
        return catre;
    }
    public void setCatre(Long catre) {
        this.catre = catre;
    }
    public String getMesaj() {
        return mesaj;
    }
    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }
    public LocalDateTime getData() {
        return data;
    }
    public void setData(LocalDateTime data) {
        this.data = data;
    }
    public Long getRaspuns() {
        return raspuns;
    }
    public void setRaspuns(Long raspuns) {
        this.raspuns = raspuns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mesaj mesaj_aux = (Mesaj) o;
        return Objects.equals(id, mesaj_aux.id) && Objects.equals(dela, mesaj_aux.dela) && Objects.equals(catre, mesaj_aux.catre) && Objects.equals(mesaj, mesaj_aux.mesaj) && Objects.equals(data, mesaj_aux.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dela, catre, mesaj, data);
    }

    @Override
    public String toString() { return "Mesaj{" + "id = " + id + ", from = " + dela + ", to = " + catre + ", message = '" + mesaj + '\'' + ", data = " + data + '}'; }
}
