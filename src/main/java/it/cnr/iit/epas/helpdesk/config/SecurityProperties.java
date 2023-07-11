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
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Configuration
@ConfigurationProperties(prefix = "epas.security")
public class SecurityProperties implements Serializable {

  private static final long serialVersionUID = -1250232668456383929L;

  private Oauth2 oauth2 = new Oauth2();

  /**
   * Bean per le info relative all'oauth2.
   */
  @Data
  public static class Oauth2 {
    boolean resourceserverEnabled = false;
    
    String jwtField = "preferred_username";

    UserAuthIdentifier userAuthIdentifier = UserAuthIdentifier.subjectId;
  }

  /**
   * Identificatore interno ad ePAS da utilizzare per 
   * fare il match del claim oauth2 (oauth2TokenClaim)
   * con l'utente interno ad ePAS.
   */
  public enum UserAuthIdentifier {
    username,
    eppn,
    subjectId
  } 

}