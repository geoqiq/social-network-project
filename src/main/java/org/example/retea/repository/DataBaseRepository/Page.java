package org.example.retea.repository.DataBaseRepository;

public class Page<E> {

    private Iterable<E> elementePePagina;
    private int elementeTotalCount;

    public Page(Iterable<E> elementePePagina, int elementeTotalCount) {
        this.elementePePagina = elementePePagina;
        this.elementeTotalCount = elementeTotalCount;
    }

    public Iterable<E> getElementeleDePePagina() {
        return elementePePagina;
    }

    public int getToateElementeleCount() {
        return elementeTotalCount;
    }
}
