package it.cnr.iit.epas.helpdesk.dto.v4;

import it.cnr.iit.epas.helpdesk.config.HelpdeskConfig.AdminEmail;
import it.cnr.iit.epas.helpdesk.config.HelpdeskConfig.Oil;
import lombok.Data;

@Data
public class HelpdeskConfigShowDto {

  //Configurazioni relative ad OIL.
  private Oil oil = new Oil();
  private AdminEmail adminEmail = new AdminEmail();

}
