package notaria.jenny.ms_administradores.dto;

import notaria.jenny.ms_administradores.model.Administradores.Rol;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdministradoresResponseDTO extends RepresentationModel<AdministradoresResponseDTO>{
    private Long idAdministrador;
    private String nombreCompleto;
    private String rut;
    private String email;
    private String telefono;
    private Rol rol;
    private Boolean activo;
    private LocalDate fechaCreacion;
}
