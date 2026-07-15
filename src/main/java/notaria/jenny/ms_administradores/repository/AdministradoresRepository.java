package notaria.jenny.ms_administradores.repository;

import notaria.jenny.ms_administradores.model.Administradores;
import notaria.jenny.ms_administradores.model.Administradores.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdministradoresRepository extends JpaRepository<Administradores, Long> {

    // Por ID (JpaRepository ya trae findById, pero lo dejamos documentado)
    Optional<Administradores> findByIdAdministrador(Long idAdministrador);

    // Por email
    Optional<Administradores> findByEmail(String email);
    boolean existsByEmail(String email);

    // Por RUT
    Optional<Administradores> findByRut(String rut);
    boolean existsByRut(String rut);

    // Por nombre (búsqueda parcial)
    List<Administradores> findByNombreCompletoContainingIgnoreCase(String nombre);

    // Por rol
    List<Administradores> findByRol(Rol rol);
    long countByRol(Rol rol);

    // Por activo
    List<Administradores> findByActivo(Boolean activo);
    long countByActivo(Boolean activo);

    // Por fecha
    List<Administradores> findByFechaCreacionBetween(LocalDate desde, LocalDate hasta);

    // Ordenado alfabéticamente
    List<Administradores> findAllByOrderByNombreCompletoAsc();
}