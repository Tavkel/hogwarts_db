package ru.hogwarts.school;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.hogwarts.school.configs.DbContext;

import java.util.Scanner;


@SpringBootApplication
@OpenAPIDefinition
public class SchoolApplication {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String[] credentials;
        if (args.length != 2 || !checkCredentials(args[0], args[1])) {
            do {
                credentials = askForCredentials(input);
            } while (!checkCredentials(credentials[0], credentials[1]));

            DbContext.DbCredentials.setUser(credentials[0]);
            DbContext.DbCredentials.setPassword(credentials[1]);
            credentials = null;
        } else {
            DbContext.DbCredentials.setUser(args[0]);
            DbContext.DbCredentials.setPassword(args[1]);
            args[0] = "";
            args[1] = "";
        }

        SpringApplication.run(SchoolApplication.class, args);
    }

    private static String[] askForCredentials(Scanner input) {
        String[] result = new String[2];
        System.out.println("Enter login...");
        result[0] = input.nextLine();
        System.out.println("Enter password...");
        result[1] = input.nextLine();
        return result;
    }

    private static boolean checkCredentials(String login, String password) {
        return true;
    }
}
