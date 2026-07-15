package notaria.jenny.ms_administradores.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/** Valida que el campo sea un RUT chileno válido (formato sin puntos + dígito verificador). */
@Documented
@Constraint(validatedBy = RutValidoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RutValido {

    String message() default "RUT inválido. Formato esperado: 12345678-5 (sin puntos, con guion) y dígito verificador correcto";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
