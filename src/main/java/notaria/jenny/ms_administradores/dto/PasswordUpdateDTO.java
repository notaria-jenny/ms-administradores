package notaria.jenny.ms_administradores.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordUpdateDTO {

    @NotBlank
    @Size(min = 8, max = 100)
    private String nuevaPassword;
}
