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

import lombok.Data;

/**
 * Informazioni sulla configurazione dell'integrazione con OIL esportate via JSON.
 *
 * @author Cristian Lucchesi
 */
@Data
public class OIlShowDto {

  private boolean enabled = false;
  private String url;
  private String instance;
  private String categories;
  private String fixedCategory;

}