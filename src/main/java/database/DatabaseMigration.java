package database;

import org.flywaydb.core.Flyway;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseMigration {

    public static void rodarMigracao() {
        Properties props = new Properties();

        // Carrega as configurações do arquivo database.properties
        try (var fis = DatabaseMigration.class.getClassLoader().getResourceAsStream("database.properties")) {

            if (fis == null) {
                System.err.println("Não foi possível encontrar o arquivo database.properties no classpath!");
                return;
            }

            props.load(fis);
        } catch (IOException e) {
            System.err.println("Erro ao ler database.properties: " + e.getMessage());
            return;
        }

        // Configura e roda o Flyway
        Flyway flyway = Flyway.configure()
                .dataSource(
                        props.getProperty("db.url"),
                        props.getProperty("db.user"),
                        props.getProperty("db.password")
                )
                // Opcional: Se quiser que o Flyway limpe o banco se der erro de validação
                // .cleanDisabled(false)
                .load();

        // Executa as migrações
        flyway.migrate();
        System.out.println("Migrações do Flyway executadas com sucesso!");
    }
}
