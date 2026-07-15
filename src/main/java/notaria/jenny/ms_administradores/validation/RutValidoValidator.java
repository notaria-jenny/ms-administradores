package notaria.jenny.ms_administradores.validation;

import notaria.jenny.ms_administradores.util.RutUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RutValidoValidator implements ConstraintValidator<RutValido, String> {

    @Override
    public boolean isValid(String rut, ConstraintValidatorContext context) {
        // null/vacío lo maneja @NotBlank; aquí solo validamos formato y DV
        if (rut == null || rut.isBlank()) {
            return true;
        }
        return RutUtils.esValido(rut);
    }
}
