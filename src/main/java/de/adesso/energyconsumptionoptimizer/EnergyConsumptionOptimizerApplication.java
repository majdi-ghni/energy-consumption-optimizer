package de.adesso.energyconsumptionoptimizer;

import de.adesso.energyconsumptionoptimizer.service.scheduling.SchedulingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan("de.adesso.energyconsumptionoptimizer.*")
@EntityScan("de.adesso.energyconsumptionoptimizer.*")
@EnableAsync
@RequiredArgsConstructor
public class EnergyConsumptionOptimizerApplication {

    private final SchedulingService schedulingService;

    public static void main(String[] args) {
        SpringApplication.run(EnergyConsumptionOptimizerApplication.class, args);
    }

    @PostConstruct
    @Async
    public void init() {
        schedulingService.init();
    }
}
