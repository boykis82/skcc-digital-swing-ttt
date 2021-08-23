package org.skcc.team1.tobe.customerserver.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.caltech.miniswing.util.AsyncHelper;
import org.caltech.miniswing.util.EnumMapper;
import org.skcc.team1.tobe.customerserver.dto.CustTypCd;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import reactor.core.scheduler.Scheduler;

import javax.persistence.EntityManager;
import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AppConfig {
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("강인수B");
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    @Bean
    public AsyncHelper asyncHelper(Scheduler scheduler) {
        return new AsyncHelper(scheduler);
    }

    @Bean
    public EnumMapper enumMapper() {
        EnumMapper enumMapper = new EnumMapper();
        enumMapper.put("custTypCd", CustTypCd.class);
        return enumMapper;
    }
}

