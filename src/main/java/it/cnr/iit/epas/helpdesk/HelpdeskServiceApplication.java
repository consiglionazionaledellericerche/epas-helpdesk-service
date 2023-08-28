/*
 * Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.cnr.iit.epas.helpdesk;

import java.util.Arrays;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableAsync
@EntityScan(basePackages = "it.cnr.iit.epas.helpdesk.models")
@EnableJpaRepositories
@EnableCaching
@EnableTransactionManagement
@EnableFeignClients
@SpringBootApplication
public class HelpdeskServiceApplication {

  @EventListener
  public void handleContextRefresh(ContextRefreshedEvent event) {
      final Environment env = event.getApplicationContext().getEnvironment();

      log.info("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));

      final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();

      StreamSupport.stream(sources.spliterator(), false)
                   .filter(ps -> ps instanceof EnumerablePropertySource<?>)
                   .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                   .flatMap(Arrays::stream)
                   .distinct()
                   .filter(prop -> !(prop.contains("credentials") || prop.contains("password") ||
                       prop.startsWith("java.") || prop.startsWith("sun.") || prop.startsWith("user.")))
                   .forEach(prop -> log.info("{}: {}", prop, env.getProperty(prop)));
  }

  public static void main(String[] args) {
    SpringApplication.run(HelpdeskServiceApplication.class, args);
  }

}