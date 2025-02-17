package org.example.retea.repository.DataBaseRepository;

public class Pageable {

    private int numarPagina;
    private int dimensiunePagina;

    public Pageable(int numarPagina, int dimensiunePagina) {
        this.numarPagina = numarPagina;
        this.dimensiunePagina = dimensiunePagina;
    }

    public int getNumarPagina() {
        return numarPagina;
    }

    public int getDimensiunePagina() {
        return dimensiunePagina;
    }
}
