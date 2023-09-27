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

import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Map;
import javax.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
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
  @Slf4j
  @Data
  @ToString
  public static class Oil {

    private boolean enabled = false;
    private String url = "http://helpwildfly.si.cnr.it:8080";
    private String instance = "HDSiper";
    private String categories = "50:Problemi Tecnici - ePAS,51:Problemi Amministrativi - ePAS";
    private String fixedCategory;
    private String username;
    private String password;

    public Map<String, String> categoryMap() {
      String oilCategories = categories;
      Map<String, String> categoryMap = Maps.newLinkedHashMap();
      if (oilCategories != null && !oilCategories.isEmpty()) {
        for (String category : oilCategories.split(",")) {
          String[] categoryFields = category.split(":");
          if (categoryFields.length == 2) {
            categoryMap.put(categoryFields[0], categoryFields[1]);
          } else {
            log.warn("Categoria di segnalazione oil %s non specificata correttamente.", category);
          }
        }
      }
      return categoryMap;
    }

  }

  /**
   * Bean per le info relative alle email da inviare in seguito alle segnalazioni
   * effettuate dai responsabili delle presenze e/o responsabili tecnici di ePAS.
   */
  @Data
  @ToString
  public static class Email {

    /**
     * È possibile configurare l'email inserendo questi parametri nella configurazione del play.
     */
    private boolean enabled = false;
    private String to = "epas@iit.cnr.it";
    private String from = "segnalazioni@epas.tools.iit.cnr.it";
    private String subject = "Segnalazione ePAS";
  }

  //Configurazioni relative ad OIL.
  private Oil oil = new Oil();
  private Email email = new Email();
  @Valid
  private CorsSettings cors = new CorsSettings();

}