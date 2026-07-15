package notaria.jenny.ms_administradores.exception;

/** Se lanza cuando email o RUT ya existen. El handler la traduce a HTTP 409. */
public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
