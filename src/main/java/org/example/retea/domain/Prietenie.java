package org.example.retea.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Prietenie extends Entity<Tuplu<Long,Long>> {

    private LocalDateTime data;
    private Utilizator utilizator1;
    private Utilizator utilizator2;

    public Prietenie(Utilizator utilizator1, Utilizator utilizator2, LocalDateTime data)
    {
        this.utilizator1 = utilizator1;
        this.utilizator2 = utilizator2;
        this.data = data;
        Long uuid1 = utilizator1.getId();
        Long uuid2 = utilizator2.getId();
        Tuplu<Long,Long> UUIDComb = new Tuplu<>(uuid1,uuid2);
        this.setId(UUIDComb);
    }

    public LocalDateTime getData() {
        return data;
    }
    public void setData(LocalDateTime data) {
        this.data = data;
    }
    public Utilizator getUtilizator1() {
        return utilizator1;
    }
    public void setUtilizator1(Utilizator utilizator1) {
        this.utilizator1 = utilizator1;
    }
    public Utilizator getUtilizator2() {
        return utilizator2;
    }
    public void setUtilizator2(Utilizator utilizator2) {
        this.utilizator2 = utilizator2;
    }

    private String formatter(LocalDateTime time)
    {
        String time_printed;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        time_printed = time.format(formatter);
        return time_printed;
    }

    @Override
    public String toString() { return "Prietenie intre: " + utilizator1.getId() + " si " + utilizator2.getId() + ", data: " + formatter(getData()); }
}
