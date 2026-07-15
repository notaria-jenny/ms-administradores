package notaria.jenny.ms_administradores.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import notaria.jenny.ms_administradores.model.Administradores.Rol;
import notaria.jenny.ms_administradores.validation.RutValido;

/**
 * DTO de actualización: NO incluye password.
 * Cambiar la contraseña tiene su propio endpoint (PATCH /{id}/password).
 */
@Data
public class AdministradoresUpdateDTO {

    @NotBlank
    @Size(max = 200)
    private String nombreCompleto;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(max = 20)
    private String telefono;

    @NotBlank
    @Size(max = 12)
    @RutValido
    private String rut;

    @NotNull
    private Rol rol;
}
