package Util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;

public class DatabaseConfig {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        Dotenv dotenv = Dotenv.load();

        String host = dotenv.get("HOST");
        String port = dotenv.get("PORT");
        String database = dotenv.get("DATABASE");

        config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
        config.setUsername(dotenv.get("USER"));
        config.setPassword(dotenv.get("PASSWORD"));

        ds = new HikariDataSource(config);
    }

    private DatabaseConfig() {}

    public static DataSource getDataSource() {
        return ds;
    }
}
