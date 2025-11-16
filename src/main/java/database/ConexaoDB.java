package database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classe utilitária para gerenciar a conexão com o banco de dados PostgreSQL.
 * <p>
 * Esta classe utiliza um bloco de inicialização estático (<code>static { ... }</code>)
 * para carregar as credenciais (URL, usuário, senha) a partir de um arquivo
 * <code>database.properties</code> localizado na pasta <code>resources</code>.
 * <p>
 * A configuração é carregada <strong>uma única vez</strong> quando a classe é
 * utilizada pela primeira vez. O método principal para obter uma conexão é
 * {@link #getConexao()}.
 */
public class ConexaoDB {

    /** Armazena o conjunto de propriedades lidas do arquivo .properties. */
    private static Properties props = new Properties();

    /** A URL de conexão JDBC. */
    private static String dbUrl;

    /** O nome de usuário para o banco de dados. */
    private static String dbUser;

    /** A senha para o banco de dados. */
    private static String dbPassword;

    /**
     * Bloco de inicialização estático.
     * <p>
     * É executado automaticamente pela JVM apenas <strong>uma vez</strong>,
     * quando a classe {@code ConexaoDB} é carregada pela primeira vez.
     * <p>
     * Responsabilidades:
     * <ol>
     * <li>Carregar o driver JDBC do PostgreSQL.</li>
     * <li>Localizar e ler o arquivo <code>/resources/database.properties</code>.</li>
     * <li>Alimentar os campos estáticos {@link #dbUrl}, {@link #dbUser}, e {@link #dbPassword}.</li>
     * </ol>
     * Lança uma {@link RuntimeException} se o arquivo de propriedades não for
     * encontrado ou se houver um erro ao carregar as configurações,
     * "quebrando" a aplicação intencionalmente.
     */
    static {
        try {
            // Carrega o driver do PostgreSQL
            Class.forName("org.postgresql.Driver");

            // Encontra o arquivo .properties dentro dos resources
            InputStream input = ConexaoDB.class.getResourceAsStream("/database.properties");

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
            // Lança uma exceção de runtime para parar a aplicação se a conexão falhar
            throw new RuntimeException("Erro ao carregar configurações do banco.", e);
        }
    }

    /**
     * Obtém uma <strong>nova</strong> conexão com o banco de dados.
     * <p>
     * Este método utiliza as credenciais estáticas (url, usuário, senha)
     * carregadas durante a inicialização da classe.
     * <p>
     * <b>Importante:</b> Quem chama este método é o responsável por fechar
     * a conexão (preferencialmente usando um bloco <code>try-with-resources</code>)
     * para evitar vazamento de conexões.
     *
     * @return Uma nova instância de {@link Connection} com o banco.
     * @throws SQLException Se ocorrer um erro ao tentar estabelecer a conexão
     */
    public static Connection getConexao() throws SQLException {
        // Toda vez que getConexao() é chamado, ele cria e retorna uma nova conexão
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Método de teste (main) para verificar a conexão com o banco de dados.
     * <p>
     * Este método tenta carregar a classe (disparando o bloco estático) e,
     * em seguida, tenta obter uma conexão usando {@link #getConexao()}.
     * Fornece feedback detalhado no console em caso de sucesso ou falha,
     * ajudando a diagnosticar problemas comuns.
     */
    public static void main(String[] args) {
        System.out.println("--- Teste de Conexão ao Banco ---");
        System.out.println("Tentando carregar a classe ConexaoDB...");

        try (Connection conn = ConexaoDB.getConexao()) {
            // Se a linha acima funcionou, a classe carregou E a conexão foi feita

            if (conn != null) {
                System.out.println("SUCESSO! Conexão estabelecida.");
                System.out.println("URL: " + conn.getMetaData().getURL());
                System.out.println("Usuário: " + conn.getMetaData().getUserName());
            } else {
                System.err.println("FALHA! A conexão retornou nula.");
            }

        } catch (SQLException e) {
            System.err.println("FALHA! Erro de SQL ao conectar:");
            System.err.println("Verifique seu 'database.properties' (URL, usuário, senha) e se o Postgres está rodando.");
            e.printStackTrace();
        } catch (ExceptionInInitializerError e) {
            // Esta é a exceção lançada se o bloco 'static' falhar
            System.err.println("FALHA! Erro ao inicializar a classe ConexaoDB.");
            System.err.println("Isso quase sempre significa um de dois problemas:");
            System.err.println("  1. O .jar do driver (postgresql-xx.x.x.jar) não está no classpath.");
            System.err.println("  2. O 'database.properties' não foi encontrado (verifique o caminho '/resources/').");
            e.printStackTrace();
        }
    }
}