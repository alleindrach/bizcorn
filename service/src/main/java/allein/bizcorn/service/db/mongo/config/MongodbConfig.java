package allein.bizcorn.service.db.mongo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MongodbConfig.Profile, ignoreUnknownFields = false)
public class MongodbConfig {
    //对应配置文件里的配置键
    public final static String Profile = "mongo";

    public static String getProfile() {
        return Profile;
    }

    private String database;

    private String host;

    private String port;

    private String username;

    private String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}