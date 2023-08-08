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

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.base.Verify;
import it.cnr.iit.epas.helpdesk.config.HelpdeskConfig;
import it.cnr.iit.epas.helpdesk.dao.UserDao;
import it.cnr.iit.epas.helpdesk.dto.v4.ReportData;
import it.cnr.iit.epas.helpdesk.models.Role;
import it.cnr.iit.epas.helpdesk.models.User;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import javax.mail.MessagingException;
import javax.mail.util.ByteArrayDataSource;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportMailerService {

  private static final Splitter COMMAS = Splitter.on(',').trimResults().omitEmptyStrings();

  private final UserDao userDao;
  private final HelpdeskConfig config;
  private final EmailService emailService;

  @Transactional
  public void sendFeedback(ReportData data, Optional<User> user) throws MessagingException, IOException {

    Verify.verifyNotNull(data);
    Verify.verifyNotNull(user);
    EmailData emailData = new EmailData();
    emailData.setFrom(config.getEmail().getFrom());
    emailData.setBody(Optional.ofNullable(data.getNote()).orElse("Segnalazione utente"));

    if (user.isPresent() && !userDao.hasAdminRoles(user.get())) {
      if (user.get().getPerson() != null) {
        emailData.setTo(userDao.getUsersWithRoles(user.get().getPerson().getOffice(), 
            Role.PERSONNEL_ADMIN).stream()
            .filter(u -> u.getPerson() != null).map(u -> u.getPerson().getEmail())
            .collect(Collectors.toList()));
      }
    } else {
      emailData.setTo(COMMAS.splitToList(config.getEmail().getTo()));
    }

    if (emailData.getTo().isEmpty()) {
      log.error("please correct {} in application.proporties", config.getEmail().getTo());
      return;
    }
    if (user.isPresent() && user.get().getPerson() != null
        && !Strings.isNullOrEmpty(user.get().getPerson().getEmail())) {
      emailData.setReplyTo(Optional.of(user.get().getPerson().getEmail()));
    }
    
    val username = user.isPresent() 
        ? user.get().getPerson() != null 
          ? user.get().getPerson().getFullname() 
              : user.get().getUsername() : "user unkwown";

    emailData.setSubject(String.format("%s: %s", config.getEmail().getSubject(), username));

    if (data.getHtml() != null) {
      ByteArrayOutputStream htmlGz = new ByteArrayOutputStream();
      GZIPOutputStream gz = new GZIPOutputStream(htmlGz);
      gz.write(data.getHtml().getBytes());
      gz.close();

      ByteArrayDataSource attachmentHtml = new ByteArrayDataSource(htmlGz.toByteArray(), "application/octet-stream");
      FileAttachment originalHtml = 
          new FileAttachment("page.html.gz", attachmentHtml);

      emailData.getAttachments().add(originalHtml);
    }

    if (data.getImg() != null) {
      ByteArrayDataSource attachmentImg = new ByteArrayDataSource(data.getImg(), "image/png");
      FileAttachment img = new FileAttachment("image.png", attachmentImg);
      emailData.getAttachments().add(img);
      
    }

    emailService.sendEmail(emailData);

  }
}