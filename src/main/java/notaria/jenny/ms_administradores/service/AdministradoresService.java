package notaria.jenny.ms_administradores.service;

import notaria.jenny.ms_administradores.dto.AdministradoresRequestDTO;
import notaria.jenny.ms_administradores.dto.AdministradoresResponseDTO;
import notaria.jenny.ms_administradores.dto.AdministradoresUpdateDTO;
import notaria.jenny.ms_administradores.dto.PasswordUpdateDTO;
import notaria.jenny.ms_administradores.model.Administradores.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AdministradoresService {

    AdministradoresResponseDTO crear(AdministradoresRequestDTO request);
    AdministradoresResponseDTO actualizar(Long id, AdministradoresUpdateDTO request);
    void actualizarPassword(Long id, PasswordUpdateDTO request);
    void toggleActivo(Long id);

    AdministradoresResponseDTO buscarPorId(Long id);
    AdministradoresResponseDTO buscarPorEmail(String email);
    AdministradoresResponseDTO buscarPorRut(String rut);

    List<AdministradoresResponseDTO> listarTodos();
    Page<AdministradoresResponseDTO> listarPaginado(Pageable pageable);
    List<AdministradoresResponseDTO> listarPorNombre(String nombre);
    List<AdministradoresResponseDTO> listarPorRol(Rol rol);
    List<AdministradoresResponseDTO> listarActivos(Boolean activo);
    List<AdministradoresResponseDTO> listarPorFecha(LocalDate desde, LocalDate hasta);

    long contarPorRol(Rol rol);
    long contarPorActivo(Boolean activo);
}
