package org.example.retea.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrietenDTO {

    Utilizator prieten;
    LocalDateTime inceputDePrietenie;

    public PrietenDTO(Utilizator prieten, LocalDateTime inceputDePrietenie) {
        this.prieten = prieten;
        this.inceputDePrietenie = inceputDePrietenie;
    }

    @Override
    public String toString() {
        String timePrinted;
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        timePrinted= inceputDePrietenie.format(format);
        return prieten.getPrenume()+"| "+
                prieten.getNumeFam()+"| "+
                timePrinted;
    }
}