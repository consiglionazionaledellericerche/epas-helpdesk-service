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

package it.cnr.iit.epas.helpdesk.service.oil;

import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import it.cnr.iit.epas.helpdesk.config.HelpdeskConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builder per costruire un'istanza del client di OIL configurata.
 *
 * @author Cristian Lucchesi
 *
 */
@RequiredArgsConstructor
@Configuration
public class OilBulder {

  private final HelpdeskConfig config;

  /**
   * Costruisce l'istance del client OIL con impostati i parametri 
   * di configurazione e le dipendenze necessario per l'autenticazione
   * e per l'invio delle immagini com multipart/form-data.
   */
  @Bean
  public Oil oil() {
    return Feign.builder()
        .requestInterceptor(
            new BasicAuthRequestInterceptor(
                config.getOil().getUsername(), 
                config.getOil().getPassword()))
        .encoder(new FormEncoder(new GsonEncoder()))
        .decoder(new GsonDecoder())
        .target(Oil.class, config.getOil().getUrl());
  }
}