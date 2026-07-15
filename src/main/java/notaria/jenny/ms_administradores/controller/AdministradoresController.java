package notaria.jenny.ms_administradores.controller;

import notaria.jenny.ms_administradores.dto.AdministradoresRequestDTO;
import notaria.jenny.ms_administradores.dto.AdministradoresResponseDTO;
import notaria.jenny.ms_administradores.dto.AdministradoresUpdateDTO;
import notaria.jenny.ms_administradores.dto.PasswordUpdateDTO;
import notaria.jenny.ms_administradores.model.Administradores.Rol;
import notaria.jenny.ms_administradores.service.AdministradoresService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/administradores")
@RequiredArgsConstructor
@Tag(name = "Administradores", description = "Gestión de administradores de la Notaría")
public class AdministradoresController {

    private final AdministradoresService service;

    // ──────────────────────────────────────────────
    // CRUD
    // ──────────────────────────────────────────────

    @Operation(summary = "Crear un administrador")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Administrador creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Email o RUT ya registrado")
    })
    @PostMapping
    public ResponseEntity<AdministradoresResponseDTO> crear(@Valid @RequestBody AdministradoresRequestDTO request) {
        AdministradoresResponseDTO response = service.crear(request);
        agregarLinks(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar los datos de un administrador (sin contraseña)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Administrador actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado"),
            @ApiResponse(responseCode = "409", description = "Email o RUT ya en uso por otro administrador")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AdministradoresResponseDTO> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody AdministradoresUpdateDTO request) {
        AdministradoresResponseDTO response = service.actualizar(id, request);
        agregarLinks(response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar la contraseña de un administrador")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contraseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Contraseña inválida (mínimo 8 caracteres)"),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    })
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> actualizarPassword(@PathVariable Long id,
                                                   @Valid @RequestBody PasswordUpdateDTO request) {
        service.actualizarPassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activar o desactivar un administrador")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estado cambiado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    })
    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<Void> toggleActivo(@PathVariable Long id) {
        service.toggleActivo(id);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────
    // BÚSQUEDAS INDIVIDUALES
    // ──────────────────────────────────────────────

    @Operation(summary = "Buscar administrador por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Administrador encontrado"),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdministradoresResponseDTO> buscarPorId(@PathVariable Long id) {
        AdministradoresResponseDTO response = service.buscarPorId(id);
        agregarLinks(response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar administrador por email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Administrador encontrado"),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<AdministradoresResponseDTO> buscarPorEmail(@PathVariable String email) {
        AdministradoresResponseDTO response = service.buscarPorEmail(email);
        agregarLinks(response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar administrador por RUT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Administrador encontrado"),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    })
    @GetMapping("/rut/{rut}")
    public ResponseEntity<AdministradoresResponseDTO> buscarPorRut(@PathVariable String rut) {
        AdministradoresResponseDTO response = service.buscarPorRut(rut);
        agregarLinks(response);
        return ResponseEntity.ok(response);
    }

    // ──────────────────────────────────────────────
    // LISTADOS
    // ──────────────────────────────────────────────

    @Operation(summary = "Listar todos los administradores ordenados por nombre")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping
    public ResponseEntity<CollectionModel<AdministradoresResponseDTO>> listarTodos() {
        List<AdministradoresResponseDTO> lista = service.listarTodos();
        lista.forEach(this::agregarLinks);
        return ResponseEntity.ok(CollectionModel.of(lista,
                linkTo(methodOn(AdministradoresController.class).listarTodos()).withSelfRel()));
    }

    @Operation(summary = "Buscar administradores por nombre")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<AdministradoresResponseDTO>> listarPorNombre(@RequestParam String nombre) {
        List<AdministradoresResponseDTO> lista = service.listarPorNombre(nombre);
        lista.forEach(this::agregarLinks);
        return ResponseEntity.ok(CollectionModel.of(lista,
                linkTo(methodOn(AdministradoresController.class).listarPorNombre(nombre)).withSelfRel()));
    }

    @Operation(summary = "Listar administradores por rol")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/rol/{rol}")
    public ResponseEntity<CollectionModel<AdministradoresResponseDTO>> listarPorRol(@PathVariable Rol rol) {
        List<AdministradoresResponseDTO> lista = service.listarPorRol(rol);
        lista.forEach(this::agregarLinks);
        return ResponseEntity.ok(CollectionModel.of(lista,
                linkTo(methodOn(AdministradoresController.class).listarPorRol(rol)).withSelfRel()));
    }

    @Operation(summary = "Listar administradores por estado activo/inactivo")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/activos")
    public ResponseEntity<CollectionModel<AdministradoresResponseDTO>> listarActivos(
            @RequestParam @Schema(allowableValues = {"activo", "inactivo"}) String estado) {
        Boolean activo = estado.equalsIgnoreCase("activo");
        List<AdministradoresResponseDTO> lista = service.listarActivos(activo);
        lista.forEach(this::agregarLinks);
        return ResponseEntity.ok(CollectionModel.of(lista,
                linkTo(methodOn(AdministradoresController.class).listarActivos(estado)).withSelfRel()));
    }

    @Operation(summary = "Listar administradores por rango de fecha de creación")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @GetMapping("/fecha")
    public ResponseEntity<CollectionModel<AdministradoresResponseDTO>> listarPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        List<AdministradoresResponseDTO> lista = service.listarPorFecha(desde, hasta);
        lista.forEach(this::agregarLinks);
        return ResponseEntity.ok(CollectionModel.of(lista,
                linkTo(methodOn(AdministradoresController.class).listarPorFecha(desde, hasta)).withSelfRel()));
    }

    // ──────────────────────────────────────────────
    // CONTADORES
    // ──────────────────────────────────────────────

    @Operation(summary = "Contar administradores por rol")
    @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente")
    @GetMapping("/contar/rol/{rol}")
    public ResponseEntity<Long> contarPorRol(@PathVariable Rol rol) {
        return ResponseEntity.ok(service.contarPorRol(rol));
    }

    // ──────────────────────────────────────────────
    // LINKS HATEOAS
    // ──────────────────────────────────────────────

    private void agregarLinks(AdministradoresResponseDTO dto) {
        dto.add(linkTo(methodOn(AdministradoresController.class)
                .buscarPorId(dto.getIdAdministrador())).withSelfRel());
        dto.add(linkTo(methodOn(AdministradoresController.class)
                .toggleActivo(dto.getIdAdministrador())).withRel("toggle-activo"));
        dto.add(linkTo(methodOn(AdministradoresController.class)
                .listarTodos()).withRel("todos"));
    }
}
