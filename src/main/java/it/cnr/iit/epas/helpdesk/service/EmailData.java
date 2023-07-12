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

package it.cnr.iit.epas.helpdesk.service;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import lombok.Data;

@Data
public class EmailData {

  private String from = null;
  private List<String> to = Lists.newArrayList();
  private List<String> cc = Lists.newArrayList();
  private Optional<String> replyTo = Optional.empty();
  private String subject = null;
  private String body;
  private List<FileAttachment> attachments = Lists.newArrayList();

}