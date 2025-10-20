package database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexaoDB {
    private static Properties props = new Properties();
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    // Bloco estático, roda uma vez quando a classe é carregada
    static {
        try {
            // Carrega o driver do PostgreSQL
            Class.forName("org.postgresql.Driver");

            // Encontra o arquivo .properties dentro dos resources
            // O caminho "/" começa da raiz da pasta 'resources'
            InputStream input = ConexaoDB.class.getResourceAsStream("/resources/database.properties");

            if (input == null) {
                System.err.println("Erro: Arquivo database.properties não encontrado!");
                throw new RuntimeException("Arquivo de propriedades do banco não encontrado.");
            }

            // Carrega as propriedades (url, user, pass) do arquivo
            props.load(input);
            dbUrl = props.getProperty("db.url");
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao carregar configurações do banco.", e);
        }
    }

    /**
     * Obtém uma NOVA conexão com o banco de dados.
     * Quem chama este metodo é responsável por fechar a conexão.
     */
    public static Connection getConexao() throws SQLException {
        // Toda vez que getConexao() é chamado, ele cria e retorna uma nova conexão
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}
