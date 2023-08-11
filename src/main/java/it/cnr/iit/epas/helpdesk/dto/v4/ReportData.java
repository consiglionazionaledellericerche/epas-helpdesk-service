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

package it.cnr.iit.epas.helpdesk.dto.v4;

import java.util.Map;
import lombok.Data;

/**
 * Dati prelevati via json dalle segnalazioni degli utenti.
 *
 * @author Marco Andreini
 * @author Cristian Lucchesi
 * @author Dario Tagliaferri
 */
@Data
public class ReportData {

  private BrowserData browser;
  private String html;
  private byte[] img;
  private String note;
  private String url;
  private String category;
  private Map<String, String> session;
  
  @Override
  public String toString() {
    return String.format("ReportData[BrowserData=%s, url=%s, category=%s, html.size()=%s,"
        + " img.size()=%s, note=%s]", browser, url, category, html != null ? html.length() : 0,
            img != null ? img.length : 0, note);
  }
}