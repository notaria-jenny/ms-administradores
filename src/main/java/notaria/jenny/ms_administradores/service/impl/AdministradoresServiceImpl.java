package notaria.jenny.ms_administradores.service.impl;

import notaria.jenny.ms_administradores.dto.AdministradoresRequestDTO;
import notaria.jenny.ms_administradores.dto.AdministradoresResponseDTO;
import notaria.jenny.ms_administradores.dto.AdministradoresUpdateDTO;
import notaria.jenny.ms_administradores.dto.PasswordUpdateDTO;
import notaria.jenny.ms_administradores.exception.RecursoDuplicadoException;
import notaria.jenny.ms_administradores.exception.RecursoNoEncontradoException;
import notaria.jenny.ms_administradores.model.Administradores;
import notaria.jenny.ms_administradores.model.Administradores.Rol;
import notaria.jenny.ms_administradores.repository.AdministradoresRepository;
import notaria.jenny.ms_administradores.service.AdministradoresService;
import lombok.RequiredArgsConstructor;
import notaria.jenny.ms_administradores.util.RutUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdministradoresServiceImpl implements AdministradoresService {

    private final AdministradoresRepository repository;
    private final PasswordEncoder passwordEncoder;

    // CRUD

    @Override
    public AdministradoresResponseDTO crear(AdministradoresRequestDTO request) {
        // Normaliza antes de comparar y guardar ("12345678-k" → "12345678-K")
        String rutNormalizado = RutUtils.normalizar(request.getRut());
        if (repository.existsByEmail(request.getEmail()))
            throw new RecursoDuplicadoException("El email ya está registrado");
        if (repository.existsByRut(request.getRut()))
            throw new RecursoDuplicadoException("El RUT ya está registrado");

        Administradores admin = new Administradores();
        admin.setNombreCompleto(request.getNombreCompleto());
        admin.setRut(request.getRut());
        admin.setEmail(request.getEmail());
        admin.setTelefono(request.getTelefono());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRol(request.getRol());
        admin.setActivo(true);
        admin.setFechaCreacion(LocalDate.now());

        return toResponse(repository.save(admin));
    }

    @Override
    public AdministradoresResponseDTO actualizar(Long id, AdministradoresUpdateDTO request) {
        Administradores admin = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Administrador con ID " + id + " no encontrado"));

        String rutNormalizado = RutUtils.normalizar(request.getRut());

        boolean emailCambiado = !admin.getEmail().equalsIgnoreCase(request.getEmail());
        if (emailCambiado && repository.existsByEmail(request.getEmail()))
            throw new RecursoDuplicadoException(
                    "El email '" + request.getEmail() + "' ya está en uso por otro administrador");

        boolean rutCambiado = !admin.getRut().equalsIgnoreCase(request.getRut());
        if (rutCambiado && repository.existsByRut(request.getRut()))
            throw new RecursoDuplicadoException(
                    "El RUT '" + request.getRut() + "' ya está en uso por otro administrador");

        admin.setNombreCompleto(request.getNombreCompleto());
        admin.setEmail(request.getEmail());
        admin.setTelefono(request.getTelefono());
        admin.setRut(rutNormalizado);
        admin.setRol(request.getRol());

        return toResponse(repository.save(admin));
    }

    @Override
    public void actualizarPassword(Long id, PasswordUpdateDTO request) {
        Administradores admin = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Administrador con ID " + id + " no encontrado"));
        admin.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
        repository.save(admin);
    }

    @Override
    public void toggleActivo(Long id) {
        Administradores admin = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Administrador con ID " + id + " no encontrado"));
        admin.setActivo(!admin.getActivo());
        repository.save(admin);
    }

    // BÚSQUEDAS

    @Override
    public AdministradoresResponseDTO buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Administrador con ID " + id + " no encontrado"));
    }

    @Override
    public AdministradoresResponseDTO buscarPorEmail(String email) {
        return repository.findByEmail(email)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Administrador con email '" + email + "' no encontrado"));
    }

    @Override
    public AdministradoresResponseDTO buscarPorRut(String rut) {
        String rutNormalizado = RutUtils.normalizar(rut);
        return repository.findByRut(rut)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Administrador con RUT '" + rut + "' no encontrado"));
    }

    // LISTADOS

    @Override
    public List<AdministradoresResponseDTO> listarTodos() {
        return repository.findAllByOrderByNombreCompletoAsc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public Page<AdministradoresResponseDTO> listarPaginado(Pageable pageable){
        return repository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public List<AdministradoresResponseDTO> listarPorNombre(String nombre) {
        return repository.findByNombreCompletoContainingIgnoreCase(nombre)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AdministradoresResponseDTO> listarPorRol(Rol rol) {
        return repository.findByRol(rol)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AdministradoresResponseDTO> listarActivos(Boolean activo) {
        return repository.findByActivo(activo)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<AdministradoresResponseDTO> listarPorFecha(LocalDate desde, LocalDate hasta) {
        if (hasta.isBefore(desde))
            throw new IllegalArgumentException(
                    "La fecha 'hasta' (" + hasta + ") no puede ser anterior a la fecha 'desde' (" + desde + ")");

        return repository.findByFechaCreacionBetween(desde, hasta)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // CONTADORES

    @Override
    public long contarPorRol(Rol rol) {
        return repository.countByRol(rol);
    }

    @Override
    public long contarPorActivo(Boolean activo) {
        return repository.countByActivo(activo);
    }

    // MAPPER

    private AdministradoresResponseDTO toResponse(Administradores admin) {
        AdministradoresResponseDTO dto = new AdministradoresResponseDTO();
        dto.setIdAdministrador(admin.getIdAdministrador());
        dto.setNombreCompleto(admin.getNombreCompleto());
        dto.setRut(admin.getRut());
        dto.setEmail(admin.getEmail());
        dto.setTelefono(admin.getTelefono());
        dto.setRol(admin.getRol());
        dto.setActivo(admin.getActivo());
        dto.setFechaCreacion(admin.getFechaCreacion());
        return dto;
    }
}
