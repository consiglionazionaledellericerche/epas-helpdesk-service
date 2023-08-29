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

package it.cnr.iit.epas.helpdesk.service.email;

import it.cnr.iit.epas.helpdesk.config.HelpdeskConfig;
import it.cnr.iit.epas.helpdesk.dto.v4.ReportData;
import it.cnr.iit.epas.helpdesk.models.User;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

/**
 * Template delle email da inviare per le segnalazioni ricevute
 * dagli utenti.
 *
 * @author Cristian Lucchesi
 *
 */
@RequiredArgsConstructor
@Component
public class EmailTemplate {

  private final HelpdeskConfig config;

  /**
   * Template per le info di debug (user agent e sessione).
   */
  public String debugTemplate(ReportData data) {
    return String.format(
        """
           <p>User Agent:
             %s
           </p>
           %s
        """, data.getBrowser() != null ? data.getBrowser().getUserAgent() : "not available", 
        sessionTemplate(data.getSession()));
  }

  /**
   * Template per mostrare l'elenco delle proprietà in sessione.
   */
  public String sessionTemplate(Map<String, String> session) {
    if (session == null || session.isEmpty()) {
      return "";
    }
    StringBuffer template = new StringBuffer("<div>Session:<dl>");
    for (val item : session.entrySet()) {
      template.append("<dt>").append(item.getKey()).append("</dt>")
      .append("<dd>").append(item.getValue()).append("</dd>");
    }
    template.append("</dl></div>");
    return template.toString();
  }

  /**
   * Template per mostrare le informazioni dell'utente corrente se presente.
   */
  public String userTemplate(Optional<User> user) {
    if (!user.isPresent()) {
      return "";
    }
    String template = 
        """
        <p>Utente: %s</p>
        <p>Sede: %s</p>
        """;
    return String.format(template, user.get().getUsername(), 
        user.get().getPerson() != null 
        ? user.get().getPerson().getOffice().getName() 
            : user.get().getOwner());
    }

  /**
   * Template con le informazioni da inviare nel corpo html dell'email.
   */
  public String feedbackTemplate(ReportData data, Optional<User> user, boolean toPersonnelAdmin) {
    String template =
        """
        <html>
          <head><title>%s</title></head>
          <body>
            <p>
              Descrizione: <br/>
              %s
            </p>
            %s
            %s
            %s
          </body>
        </html>
        """;
    return String.format(template, config.getEmail().getSubject(), data.getNote(), getUrl(data), 
        userTemplate(user), toPersonnelAdmin ? "" : debugTemplate(data));
  }

  public String getUrl(ReportData reportData) {
    if (reportData.getUrl() == null) {
      return "";
    }
    return String.format("<p>URL: <a href=\"%s\">%s</a></p>", 
        reportData.getUrl(), reportData.getUrl());
  }
}
