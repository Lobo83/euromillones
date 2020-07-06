package org.lobo.euromillones.configuration;

import org.lobo.euromillones.service.EstadisticaService;
import org.lobo.euromillones.service.JugadaFeederService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Configuration
@EnableJpaRepositories("org.lobo.euromillones.persistence.repository")
@EnableTransactionManagement
@EntityScan("org.lobo.euromillones.persistence.model")
@EnableSwagger2
@EnableAsync
public class EuromillonesConfiguration {
    @Autowired
    private EstadisticaService estadisticaService;
    @Autowired
    private JugadaFeederService jugadaFeederService;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("org.lobo.euromillones")).paths(PathSelectors.any()).build();
    }

    @PostConstruct
    private void initBaseDatos() {

        jugadaFeederService.crearJugadasDesdeOrigen(LocalDate.of(2017, 12, 05));
        estadisticaService.crearFrecuencias();

    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("Async-");
        return executor;
    }

}
