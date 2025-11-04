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

    /**
     * METODO DE TESTE SIMPLES
     * Para rodar: clique com o botão direito DENTRO deste metodo e
     * selecione "Run 'ConexaoDB.main()'"
     */
    public static void main(String[] args) {
        System.out.println("--- Teste de Conexão ao Banco ---");
        System.out.println("Tentando carregar a classe ConexaoDB...");

        try (Connection conn = ConexaoDB.getConexao()) {
            // Se a linha acima funcionou, a classe carregou E a conexão foi feita

            if (conn != null) {
                System.out.println("✅ SUCESSO! Conexão estabelecida.");
                System.out.println("URL: " + conn.getMetaData().getURL());
                System.out.println("Usuário: " + conn.getMetaData().getUserName());
            } else {
                System.err.println("❌ FALHA! A conexão retornou nula.");
            }

        } catch (SQLException e) {
            System.err.println("❌ FALHA! Erro de SQL ao conectar:");
            System.err.println("Verifique seu 'database.properties' (URL, usuário, senha) e se o Postgres está rodando.");
            e.printStackTrace();
        } catch (ExceptionInInitializerError e) {
            System.err.println("❌ FALHA! Erro ao inicializar a classe ConexaoDB.");
            System.err.println("Isso quase sempre significa um de dois problemas:");
            System.err.println("  1. (O que aconteceu com você) O .jar do driver não foi encontrado.");
            System.err.println("  2. O arquivo 'database.properties' não foi encontrado no caminho (ex: /resources/database.properties).");
            e.printStackTrace();
        }
    }
}
