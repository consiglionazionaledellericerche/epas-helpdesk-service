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

import it.cnr.iit.epas.helpdesk.config.HelpdeskConfig;
import it.cnr.iit.epas.helpdesk.dao.UserDao;
import it.cnr.iit.epas.helpdesk.dto.v4.ReportData;
import it.cnr.iit.epas.helpdesk.security.SecureUtils;
import it.cnr.iit.epas.helpdesk.service.email.ReportMailerService;
import it.cnr.iit.epas.helpdesk.service.oil.OilService;
import java.io.IOException;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportCenterService {

  private final ReportMailerService reportMailerService;
  private final HelpdeskConfig config;
  private final UserDao userDao;
  private final OilService oilService;
  private final SecureUtils secureUtils;
  
  public void sendFeedback(ReportData data) throws MessagingException, IOException {
    val currentUser = secureUtils.getCurrentUser();

    if (config.getOil().isEnabled() && currentUser.isPresent()) {
      if (userDao.hasAdminRoles(currentUser.get()) && currentUser.get().getPerson() != null) {
        log.info("Invio ad OIL la segnalazione. Utente {}. Categoria: '{} (id={})'. Url: {}. Note: {}", 
            currentUser.get().getUsername(),
            config.getOil().categoryMap().get(data.getCategory()), data.getCategory(),
            data.getUrl(), data.getNote());
        oilService.sendFeedback(data, currentUser.get().getPerson());
      } else {
        reportMailerService.sendFeedback(data, currentUser);
      }
    } else {
      reportMailerService.sendFeedback(data, currentUser);
    }
  }
}