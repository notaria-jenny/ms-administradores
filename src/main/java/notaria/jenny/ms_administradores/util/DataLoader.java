package notaria.jenny.ms_administradores.util;

import notaria.jenny.ms_administradores.model.Administradores;
import notaria.jenny.ms_administradores.repository.AdministradoresRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Profile({"dev", "test"})
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final AdministradoresRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) {
            log.info(">> ms-administrador: Base de datos ya contiene datos, omitiendo DataLoader.");
            return;
        }

        Faker faker = new Faker();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            Administradores admin = new Administradores();
            admin.setNombreCompleto(faker.name().fullName());
            admin.setRut(generarRut(random));
            admin.setEmail(faker.internet().emailAddress());
            admin.setTelefono("+569" + faker.number().numberBetween(10000000, 99999999));
            admin.setPassword(passwordEncoder.encode("Admin123!"));

            // La notaría tiene un solo notario titular: es el primero y siempre queda
            // activo, porque sin él no se pueden emitir declaraciones juradas
            boolean esNotario = (i == 0);
            admin.setRol(esNotario
                    ? Administradores.Rol.NOTARIO
                    : faker.options().option(
                    Administradores.Rol.ABOGADO,
                    Administradores.Rol.OFICIAL,
                    Administradores.Rol.FUNCIONARIA));
            admin.setActivo(esNotario || faker.bool().bool());

            admin.setFechaCreacion(LocalDate.now().minusDays(faker.number().numberBetween(1, 365)));
            repository.save(admin);
        }

        log.info(">> ms-administrador: ¡Base de datos poblada con DataFaker exitosamente!");
    }

    private String generarRut(Random random) {
        int numero = random.nextInt(20000000 - 5000000) + 5000000;
        return numero + "-" + RutUtils.calcularDv(numero);
    }

}
