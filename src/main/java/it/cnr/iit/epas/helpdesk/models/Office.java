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

package it.cnr.iit.epas.helpdesk.models;

import com.google.common.collect.Lists;
import it.cnr.iit.epas.helpdesk.models.base.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


/**
 * Un ufficio.
 */
@Getter
@Setter
@Entity
@Audited
public class Office extends BaseEntity {

  private static final long serialVersionUID = 2757179343678369768L;

  private Long perseoId;

  @NotNull
  @Column(nullable = false)
  private String name;

  //Codice della sede, per esempio per la sede di Pisa è "044000"
  //@As(binder = NullStringBinder.class)
  private String code;

  //sedeId, serve per l'invio degli attestati, per esempio per la sede di Pisa è "223400"
  //@Unique
  @NotNull
  private String codeId;

  private String address;

  private LocalDate joiningDate;

  private boolean headQuarter = false;

  @OneToMany(mappedBy = "owner", cascade = {CascadeType.REMOVE})
  private List<User> users = Lists.newArrayList();

  @OneToMany(mappedBy = "office", cascade = {CascadeType.REMOVE})
  private List<UsersRolesOffices> usersRolesOffices = Lists.newArrayList();

  private LocalDateTime updatedAt;

  @Transient
  private Boolean isEditable = null;

  public String getName() {
    return this.name;
  }

  @Override
  public String getLabel() {
    return this.name;
  }

  @Override
  public String toString() {
    return getLabel();
  }

}