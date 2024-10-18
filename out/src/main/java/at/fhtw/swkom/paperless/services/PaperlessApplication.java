package at.fhtw.swkom.paperless.services;


import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"at.fhtw.swkom.paperless.services", "at.fhtw.swkom.paperless.controller"})
public class PaperlessApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaperlessApplication.class, args);
    }

    @Bean
    public JsonNullableModule jsonNullableModule() {
        return new JsonNullableModule();
    }
}
