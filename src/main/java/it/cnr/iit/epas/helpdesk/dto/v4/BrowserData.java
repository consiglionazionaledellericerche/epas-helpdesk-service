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

import java.util.List;
import lombok.Data;

/**
 * DTO che contiene i dati relativi al browser con cui viene
 * effettuata la segnalazione.
 *
 * @author Marco Andreini
 * @author Cristian Lucchesi
 *
 */
@Data
public class BrowserData {
  private String appCodeName;
  private String appName;
  private String appVersion;
  private boolean cookieEnabled;
  private boolean onLine;
  private String platform;
  private String userAgent;
  private List<String> plugins;
}
