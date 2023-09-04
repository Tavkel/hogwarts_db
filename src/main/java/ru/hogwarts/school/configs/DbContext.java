package ru.hogwarts.school.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DbContext {
    @Bean
    @ConfigurationProperties(prefix = "app.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .url("jdbc:postgresql://89.179.242.75:5432/SkyproLearning")
                .driverClassName("org.postgresql.Driver")
                .username(DbCredentials.getUser())
                .password(DbCredentials.getPassword())
                .build();
    }


    public static class DbCredentials{
        private static String user;
        private static String password;

        public static String getUser() {
            return user;
        }
        public static void setUser(String user) {
            DbCredentials.user = user;
        }
        public static String getPassword() {
            return password;
        }
        public static void setPassword(String password) {
            DbCredentials.password = password;
        }
    }
}
