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

package it.cnr.iit.epas.helpdesk.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.cnr.iit.epas.helpdesk.config.HelpdeskConfig;
import it.cnr.iit.epas.helpdesk.config.OpenApiConfiguration;
import it.cnr.iit.epas.helpdesk.controller.v1.utils.ApiRoutes;
import it.cnr.iit.epas.helpdesk.dto.v4.HelpdeskConfigShowDto;
import it.cnr.iit.epas.helpdesk.dto.v4.ReportData;
import it.cnr.iit.epas.helpdesk.dto.v4.mapper.HelpdeskConfigShowMapper;
import it.cnr.iit.epas.helpdesk.service.ReportCenterService;
import java.io.IOException;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@SecurityRequirements(
    value = { 
        @SecurityRequirement(name = OpenApiConfiguration.BEARER_AUTHENTICATION), 
        @SecurityRequirement(name = OpenApiConfiguration.BASIC_AUTHENTICATION)})
@Tag(
    name = "Report center controller", 
    description = "Gestisce le segnalazioni inviate dagli utenti, smistandole via email al"
        + " personale della sede oppure via email oppure OIL agli esperti di ePAS.")
@RequestMapping(ApiRoutes.BASE_PATH + "/reportcenter")
@RestController
@RequiredArgsConstructor
public class ReportCenterController {

  private final HelpdeskConfig helpdeskConfig;
  private final HelpdeskConfigShowMapper configMapper;
  private final ReportCenterService reportCenterService;

  @GetMapping("/config")
  public ResponseEntity<HelpdeskConfigShowDto> config() {
    log.debug("Ricevuta richiesta visualizzazione della configurazione {}", helpdeskConfig);
    return ResponseEntity.ok().body(configMapper.convert(helpdeskConfig));
  }

  /**
   * Invia di una segnalazione agli utenti opportuni in funzione di chi ha la richiesta
   * e della presenza o meno della configurazione per OIL. 
   *
   * @throws MessagingException 
   * @throws IOException
   */
  @Operation(
      summary = "Invia una segnalazione al personale responsabile della sua gestione.",
      description = "L'invio della segnalazione segue un logica di escalation, per cui le "
          + "segnalazioni dei dipendenti vengono inviate ai responsabili amministrativi della"
          + "sede oppure ai responsabili tecnici (dipende dal tipo di segnalazione)."
          + "Le segnalazioni dei responsabili amministrativi e/o tecnici vengono inviate "
          + "ad un indirizzo email impostato in configurazione oppure ad OIL (se configurato).")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "segnalazione inviata correttamente"),
      @ApiResponse(responseCode = "400", description = "dati segnalazione non corretti"),
      @ApiResponse(responseCode = "403", 
        description = "autenticazione non presente o utente che ha effettuato"
            + " la richiesta non autorizzato ad effettuare segnalazioni"),
      @ApiResponse(responseCode = "500", 
        description = "problema nell'invio della segnalazione (invio email oppure OIL non"
            + " funzionanti oppure parametri mancanti (es. email.to)") 
  })
  @Transactional
  @PutMapping("/send")
  public ResponseEntity<Void> send(
      @NotNull @RequestBody @Valid ReportData reportData) throws MessagingException, IOException {
    log.info("Ricevuta richiesta di invio segnalazione {}", reportData);
    val sent = reportCenterService.sendFeedback(reportData);
    if (!sent) {
      return ResponseEntity.internalServerError().build();
    }
    return ResponseEntity.ok().build();
  }

}