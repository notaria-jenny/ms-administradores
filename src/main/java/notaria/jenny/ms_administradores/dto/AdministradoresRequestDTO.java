package notaria.jenny.ms_administradores.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import notaria.jenny.ms_administradores.model.Administradores;
import notaria.jenny.ms_administradores.validation.RutValido;

@Data
public class AdministradoresRequestDTO {

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
    @Size(min = 8)
    private String password;

    @NotBlank
    @Size(max = 12)
    @RutValido
    private String rut;

    @NotNull
    private Administradores.Rol rol;
}
