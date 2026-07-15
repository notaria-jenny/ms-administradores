package notaria.jenny.ms_administradores.exception;

/** Se lanza cuando el recurso no existe. El handler la traduce a HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
