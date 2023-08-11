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
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.auth.BasicAuthRequestInterceptor;
import feign.gson.GsonEncoder;
import it.cnr.iit.epas.helpdesk.config.HelpdeskConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class OilServiceClient {

  private final HelpdeskConfig config;

  /**
   * API per l'epas-helpdesk-service.
   */
  interface OilClient {

    @Headers("Content-Type: application/json")
    @RequestLine("PUT /rest/pest/{instance}")
    String send(OilCreateDto oilCreateDto, @Param("instance") String instance);

    @Headers("Content-Type: multipart/form-data")
    @RequestLine("POST /rest/pest/{instance}/{id}")
    void addAttachment(MultipartFile attachment,
        @Param("instance") String instance, @Param("id") String id);
  }

  private OilClient oilClient() {
    return Feign.builder()
        .requestInterceptor(
            new BasicAuthRequestInterceptor(
                config.getOil().getUsername(), config.getOil().getPassword()))
        .encoder(new GsonEncoder())
        .target(OilClient.class, config.getOil().getUrl());
  }
  
  public String send(OilCreateDto oilCreateDto) {
    return oilClient().send(oilCreateDto, config.getOil().getInstance());
  }
  
  public void addAttachment(String id, MultipartFile attachment) {
    oilClient().addAttachment(attachment, config.getOil().getInstance(), id);
  }
}