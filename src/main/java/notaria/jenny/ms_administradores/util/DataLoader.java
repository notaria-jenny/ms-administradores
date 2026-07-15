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

    private final AdministradorRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) {
            log.info(">> ms-administrador: Base de datos ya contiene datos, omitiendo DataLoader.");
            return;
        }

        Faker faker = new Faker();
        Random random = new Random();
        Administrador.Rol[] roles = Administrador.Rol.values();

        for (int i = 0; i < 10; i++) {
            Administrador admin = new Administrador();
            admin.setNombreCompleto(faker.name().fullName());
            admin.setRut(generarRut(random));
            admin.setEmail(faker.internet().emailAddress());
            admin.setTelefono("+569" + faker.number().numberBetween(10000000, 99999999));
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRol(faker.options().option(roles));
            admin.setActivo(faker.bool().bool());
            admin.setFechaCreacion(LocalDate.now().minusDays(faker.number().numberBetween(1, 365)));
            repository.save(admin);
        }

        log.info(">> ms-administrador: ¡Base de datos poblada con DataFaker exitosamente!");
    }

    private String generarRut(Random random) {
        int numero = random.nextInt(20000000 - 5000000) + 5000000;
        int dv = calcularDv(numero);
        return numero + "-" + (dv == 10 ? "K" : dv);
    }

    private int calcularDv(int rut) {
        int suma = 0;
        int multiplicador = 2;
        while (rut != 0) {
            suma += (rut % 10) * multiplicador;
            rut /= 10;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }
        int resultado = 11 - (suma % 11);
        return resultado == 11 ? 0 : resultado;
    }
}
