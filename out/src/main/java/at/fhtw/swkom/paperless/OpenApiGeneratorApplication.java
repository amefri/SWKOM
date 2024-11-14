package at.fhtw.swkom.paperless;

import com.fasterxml.jackson.databind.Module;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
/*(
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)*/
@EnableJpaRepositories("at.fhtw.swkom.paperless.persistence.repository")
@EntityScan("at.fhtw.swkom.paperless.persistence.entity")
@ComponentScan(
    basePackages = {"at.fhtw.swkom.paperless", "at.fhtw.swkom.paperless.controller" , "at.fhtw.swkom.paperless.config", "at.fhtw.swkom.paperless.persistence"},
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class OpenApiGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenApiGeneratorApplication.class, args);
    }

    @Bean(name = "at.fhtw.swkom.paperless.OpenApiGeneratorApplication.jsonNullableModule")
    public Module jsonNullableModule() {
        return new JsonNullableModule();
    }

}