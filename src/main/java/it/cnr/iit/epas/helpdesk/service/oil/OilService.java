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

package it.cnr.iit.epas.helpdesk.service.oil;

import feign.form.FormData;
import it.cnr.iit.epas.helpdesk.config.HelpdeskConfig;
import it.cnr.iit.epas.helpdesk.dto.v4.ReportData;
import it.cnr.iit.epas.helpdesk.models.Person;
import it.cnr.iit.epas.helpdesk.service.FileUtils;
import it.cnr.iit.epas.helpdesk.service.oil.OilCreateDto.ConfirmOption;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class OilService {

  private final HelpdeskConfig config;
  private final Oil oil;

  public void sendFeedback(ReportData data, Person currentPerson) {
    OilCreateDto oilData = buildOilCreateData(data, currentPerson);
    Long idSegnalazione = oil.newProblem(oilData, config.getOil().getInstance()); 
    log.info("Inviato ad OIL il feedback (id = {}) della persona {}, con i seguenti dati {}", 
        idSegnalazione, currentPerson.getFullname(), oilData);
    addAttachment(idSegnalazione, new FormData("image/png", "image.png", data.getImg()));
    try {
      addAttachment(idSegnalazione, 
          new FormData(
              "application/octet-stream", "page.html.gz", FileUtils.gzipText(data.getHtml())));
      addAttachment(idSegnalazione, 
          new FormData("application/octet-stream", "debug.txt.gz", FileUtils.debugFile(data)));
    } catch (IOException e) {
      log.error("Problema durante l'invio degli attachment per la segnalazione id = {}", idSegnalazione, e);
    }
  }

  public void addAttachment(Long idSegnalazione, FormData formData) {
    oil.addAttachment(idSegnalazione, formData, config.getOil().getInstance());
    log.info("Aggiunto su OIL file {} ({}) alla segnalazione id = {}", 
        formData.getFileName(), formData.getContentType(), idSegnalazione);
  }

  private OilCreateDto buildOilCreateData(ReportData data, Person currentPerson) {
    if (config.getOil().getFixedCategory() != null) {
      log.info("Sovrascritta la categoria {} con la categoria fissata in configurazione {}",
          data.getCategory(), config.getOil().getFixedCategory());
      data.setCategory(config.getOil().getFixedCategory());
    }

    return OilCreateDto.builder()
        .firstName(currentPerson.getName())
        .familyName(currentPerson.getSurname())
        .email(currentPerson.getEmail())
        .titolo("[ePAS] Segnalazione ePAS")
        .categoria(data.getCategory())
        .categoriaDescrizione(config.getOil().categoryMap().get(data.getCategory()))
        .descrizione(String.format("%s\n\nURL: %s\n%s", data.getNote(), 
            data.getUrl(), getPersonInfo(currentPerson)))
        .confirmRequested(ConfirmOption.n)
        .build();
  }

  private String getPersonInfo(Person person) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    String now = formatter.format(LocalDateTime.now());
    return String.format("Sede: %s\n\nUtente: %s %s. Matricola: %s. Email: %s. Data: %s", 
        person.getOffice().getName(), person.getName(), person.getSurname(), person.getNumber(), 
        person.getEmail(), now);
  }

}