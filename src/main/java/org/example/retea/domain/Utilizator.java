package org.example.retea.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long> {

    private String prenume;
    private String numeFam;
    private String numeUtil;
    private List<Utilizator> prieteni;

    public Utilizator(String prenume, String numeFam, String numeUtil) {
        this.prenume = prenume;
        this.numeFam = numeFam;
        this.numeUtil = numeUtil;
        prieteni = new ArrayList<Utilizator>();
    }

    public String getNumeUtil() {
        return numeUtil;
    }
    public void setNumeUtil(String numeUtil) {
        this.numeUtil = numeUtil;
    }
    public String getPrenume() {
        return prenume;
    }
    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }
    public String getNumeFam() {
        return numeFam;
    }
    public void setNumeFam(String numeFam) {
        this.numeFam = numeFam;
    }
    public String getFullName() {
        return prenume + " " + numeFam;
    }
    public List<Utilizator> getPrieteni() {
        return prieteni;
    }

    @Override
    public String toString() {
        String friendList = prieteni != null ? prieteni.stream()
                .map(friend -> friend.getPrenume() + " " + friend.getNumeFam())
                .reduce((friend1, friend2) -> friend1 + ", " + friend2)
                .orElse("-") : "-";
        return "ID: " + id + "; username:" + numeUtil + "; prieteni: " + friendList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return Objects.equals(prenume, that.prenume) && Objects.equals(numeFam, that.numeFam) && Objects.equals(numeUtil, that.numeUtil) && Objects.equals(prieteni, that.prieteni);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrenume(), getNumeFam(), getPrieteni());
    }
}