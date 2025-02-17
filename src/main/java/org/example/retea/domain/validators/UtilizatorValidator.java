package org.example.retea.domain.validators;

import org.example.retea.domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException
    {
        if(entity.getPrenume().length()<3) throw new ValidationException("Prenumele trebuie sa contina cel putin 3 caractere.");
        if(entity.getNumeFam().length()<3) throw new ValidationException("Numele trebuie sa contina cel putin 3 caractere.");
    }
}

