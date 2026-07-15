package notaria.jenny.ms_administradores.exception;

import notaria.jenny.ms_administradores.model.Administradores;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 — errores de @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos");
        body.put("errores", errores);
        return ResponseEntity.badRequest().body(body);
    }

    // 400 — enum inválido en el body (ej: rol inexistente)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBodyIlegible(HttpMessageNotReadableException ex) {
        String mensaje = ex.getMessage();
        String detalle = (mensaje != null && mensaje.contains("Administrador$Rol"))
                ? "Rol inválido. Los valores permitidos son: " + rolesPermitidos()
                : "El cuerpo de la solicitud contiene un valor inválido";
        return ResponseEntity.badRequest().body(baseBody(HttpStatus.BAD_REQUEST, detalle));
    }

    // 400 — enum/tipo inválido en la URL (ej: /rol/GERENTE)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        String detalle;
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(Administradores.Rol.class)) {
            detalle = "Rol inválido. Los valores permitidos son: " + rolesPermitidos();
        } else {
            detalle = "El parámetro '" + ex.getName() + "' tiene un formato inválido";
        }
        return ResponseEntity.badRequest().body(baseBody(HttpStatus.BAD_REQUEST, detalle));
    }

    // 400 — argumentos inválidos (ej: rango de fechas invertido)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(baseBody(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    // 404 — recurso no encontrado
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(baseBody(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // 409 — email/RUT duplicado
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicado(RecursoDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(baseBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    // 409 — respaldo: si dos requests pasan el existsBy... al mismo tiempo,
    // la constraint UNIQUE de la BD rechaza el segundo insert y cae aquí
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegridad(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(baseBody(HttpStatus.CONFLICT, "El email o RUT ya está registrado"));
    }

    // 500 — cualquier error no contemplado: stacktrace al log,
    // mensaje genérico al cliente (nunca exponer detalles internos)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleErrorInesperado(Exception ex) {
        log.error("Error inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(baseBody(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno. Intente nuevamente."));
    }

    private Map<String, Object> baseBody(HttpStatus status, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", mensaje);
        return body;
    }

    private String rolesPermitidos() {
        return Arrays.stream(Administradores.Rol.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
