package codesquad.business.repository;

import java.nio.charset.StandardCharsets;
import java.sql.*;

import static codesquad.was.util.ResourceGetter.getResourceBytesByPath;

public class JdbcTemplate {
    public static JdbcTemplate jdbcTemplate = new JdbcTemplate();

//    private static final String H2_URL = "jdbc:h2:tcp://localhost/~/java-was";
    private static final String H2_IN_MEMORY_URL = "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1";
    private static final String H2_USERNAME = "sa";
    private static final String H2_PASSWORD = "";

    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/java-was?useSSL=false&allowMultiQueries=true&allowPublicKeyRetrieval=true";
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_PASSWORD = "";

    static {
        String initSQLString = new String(getResourceBytesByPath("/init.sql"), StandardCharsets.UTF_8);
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            // allowMultiQueries=true 옵션으로 멀티 스테이트먼트 실행
            statement.execute(initSQLString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
        return DriverManager.getConnection(H2_IN_MEMORY_URL, H2_USERNAME, H2_PASSWORD);
    }
}
