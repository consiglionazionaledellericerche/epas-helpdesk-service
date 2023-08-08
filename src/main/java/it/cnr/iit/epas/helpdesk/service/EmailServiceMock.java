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

import com.google.common.base.Verify;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Mock del servizio per l'invio delle email contenenti le segnalazioni 
 * degli utenti.
 *
 * @author Cristian Lucchesi
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "epas.helpdesk.email", name = "enabled", havingValue = "false")
public class EmailServiceMock implements EmailService {

  @Override
  public void sendEmail(EmailData emailData) throws MessagingException {
    sendEmail(emailData.getTo(), emailData.getFrom(), emailData.getCc(), emailData.getReplyTo(),
        emailData.getSubject(), emailData.getBody(), emailData.getAttachments());
  }

  @Override
  public void sendEmail(String to, String from, String subject, String body, 
      List<FileAttachment> attachments) throws MessagingException {
    sendEmail(Lists.newArrayList(to), from, Lists.newArrayList(), Optional.empty(), subject, body, attachments);
  }

  @Override
  public void sendEmail(List<String> to, String from, List<String> cc, Optional<String> replyTo, 
      String subject, String body, List<FileAttachment> attachments) throws MessagingException {
    Verify.verifyNotNull(to);
    Verify.verifyNotNull(subject);

    log.info("Mock Email Service: email a {}, from = {}, cc = {}, subject = {}, body = {}, "
        + "attachments are present = {}", to, from, cc, subject, body, !attachments.isEmpty());
  }
}