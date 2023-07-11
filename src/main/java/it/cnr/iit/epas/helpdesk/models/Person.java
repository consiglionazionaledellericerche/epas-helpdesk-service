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

import it.cnr.iit.epas.helpdesk.models.base.BaseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity per le persone.
 *
 * @author Cristian Lucchesi
 */
@Getter
@Setter
@Entity
@Table(name = "persons")
public class Person extends BaseEntity {

  /*
   * IMPORTANTE: relazione con user impostata a LAZY per non scaricare tutte le informazioni della
   * persona in fase di personDao.list. Necessaria comunque la join con le relazioni OneToOne.
   */

  private static final long serialVersionUID = -2293369685203872207L;

  private Long perseoId;

  @NotNull
  private String name;

  @NotNull
  private String surname;

  private String othersSurnames;

  private String fiscalCode;

  private LocalDate birthday;

  @Email
  @NotNull
  private String email;

  @OneToOne(optional = false, fetch = FetchType.LAZY)
  private User user;

  /**
   * Numero di matricola.
   */
  //@Unique
  private String number;

  /**
   * id che questa persona aveva nel vecchio database.
   */
  private Long oldId;

  /**
   * Campo da usarsi in caso di autenticazione via shibboleth.
   */
  private String eppn;

  private String telephone;

  private String fax;

  private String mobile;

  private boolean wantEmail;

  @ManyToOne
  @NotNull
  private Office office;  

  private LocalDateTime updatedAt;

  /**
   * Nome completo della persona.
   *
   * @return il nome completo.
   */
  public String getFullname() {
    return String.format("%s %s", surname, name);
  }


  public String fullName() {
    return getFullname();
  }

  @Override
  public String toString() {
    return getFullname();
  }

  /**
   * Comparatore di persone per fullname e poi id.
   *
   * @return un Comparator che compara per fullname poi id.
   */
  public static Comparator<Person> personComparator() {
    return Comparator
        .comparing(
            Person::getFullname,
            Comparator.nullsFirst(String::compareTo))
        .thenComparing(
            Person::getId,
            Comparator.nullsFirst(Long::compareTo));
  }

  @Transient
  public boolean isGroupManager() {
    return user.hasRoles(Role.GROUP_MANAGER);
  }
  
  @Transient
  public boolean isSeatSupervisor() {
    return user.hasRoles(Role.SEAT_SUPERVISOR);
  }

}