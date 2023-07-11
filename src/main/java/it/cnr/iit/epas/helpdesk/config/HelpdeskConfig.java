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

package it.cnr.iit.epas.helpdesk.config;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Contenitore dei parametri di configurazione del servizio.
 *
 * @author Cristian Lucchesi
 *
 */
@Data
@EqualsAndHashCode
@ToString
@Configuration
@ConfigurationProperties(prefix = "epas.helpdesk")
public class HelpdeskConfig implements Serializable {

  private static final long serialVersionUID = -3011463006965805666L;

  /**
   * Bean per le info relative ad OIL.
   */
  @Data
  @ToString
  public static class Oil {

    private boolean enabled = false;
    private String baseUrl = "http://epas-helpdesk-service:8080/rest/v1";
    private String createPath = "/";
    
    public URL getOilCreateUrl() throws MalformedURLException {
      return new URL(new URL(baseUrl), createPath);
    }
  }

  /**
   * Bean per le info relative alle email da inviare in seguito alle segnalazioni
   * effettuate dai responsabili delle presenze e/o responsabili tecnici di ePAS.
   */
  @Data
  @ToString
  public static class AdminEmail {

    /**
     * È possibile configurare l'email inserendo questi parametri nella configurazione del play.
     */
    private String to = "epas@iit.cnr.it";
    private String from = "segnalazioni@epas.tools.iit.cnr.it";
    private String subject = "Segnalazione ePAS";
  }

  //Configurazioni relative ad OIL.
  private Oil oil = new Oil();
  private AdminEmail adminEmail = new AdminEmail();

}