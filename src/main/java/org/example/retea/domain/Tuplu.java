package org.example.retea.domain;

import java.util.Objects;

/**
 * Define a Tuple o generic type entities
 * @param <E1> - tuple first entity type
 * @param <E2> - tuple second entity type
 */
public class Tuplu<E1,E2> {
    private E1 e1;
    private E2 e2;

    /**
     * creates tuple of two entities
     * @param e1  first entity of tuple
     * @param e2  second entity of tuple
     */
    public Tuplu(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * gets first entity
     * @return first entity
     */
    public E1 getLeft() {
        return e1;
    }

    /**
     * set first entity
     * @param e1 to set as first entity
     */
    public void setLeft(E1 e1) {
        this.e1 = e1;
    }

    /**
     * gets second entity
     * @return second entity
     */
    public E2 getRight() {
        return e2;
    }

    /**
     * sets second entity
     * @param e2 to set as second entity
     */
    public void setRight(E2 e2) {
        this.e2 = e2;
    }

    /**
     * to string for tuple
     * @return strings to be printed
     */
    @Override
    public String toString() {
        return e1 + ", " + e2;
    }

    /**
     * equality of tuples
     * @param o to check if object is equal
     * @return true if they are equal else false
     */
    @Override
    public boolean equals(Object o) {
        return this.e1.equals(((Tuplu) o).e1) && this.e2.equals(((Tuplu) o).e2);
    }

    /**
     * hashcode of tuple
     * @return hashcode of objects
     */
    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}