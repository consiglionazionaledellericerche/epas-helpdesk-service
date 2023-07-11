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

package it.cnr.iit.epas.helpdesk.dao;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.querydsl.core.BooleanBuilder;
import it.cnr.iit.epas.helpdesk.models.Office;
import it.cnr.iit.epas.helpdesk.models.QUser;
import it.cnr.iit.epas.helpdesk.models.Role;
import it.cnr.iit.epas.helpdesk.models.User;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Component;

/**
 * DAO per gli User.
 *
 * @author Cristian Lucchesi
 */
@Component
public class UserDao extends DaoBase<User> {

  @Inject
  UserDao(Provider<EntityManager> emp) {
    super(emp);
  }

  public static final Splitter TOKEN_SPLITTER = Splitter.on(' ').trimResults().omitEmptyStrings();

  /**
   * L'user identificato dall'id passato come parametro.
   */
  public User byId(Long id) {
    final QUser user = QUser.user;
    return getQueryFactory().selectFrom(user).where(user.id.eq(id)).fetchOne();
  }

  /**
   * L'user identificato dall'id passato come parametro.
   *
   * @param id l'identificativo dell'utente
   * @param password (opzionale) la password dell'utente
   * @return lo user  identificato dall'id passato come parametro.
   */
  public User getUserByIdAndPassword(Long id, Optional<String> password) {
    final QUser user = QUser.user;
    final BooleanBuilder condition = new BooleanBuilder();

    if (password.isPresent()) {
      condition.and(user.password.eq(password.get()));
    }
    return getQueryFactory().selectFrom(user)
        .where(condition.and(user.id.eq(id))).fetchOne();
  }

  /**
   * L'utente cui il token appartiene.
   *
   * @param recoveryToken la stringa con il token per ricreare la password
   * @return l'user corrispondente al recoveryToken inviato per il recovery della password.
   */
  public User getUserByRecoveryToken(String recoveryToken) {
    final QUser user = QUser.user;
    return getQueryFactory().selectFrom(user)
        .where(user.recoveryToken.eq(recoveryToken)).fetchOne();
  }

  /**
   * L'user corrispondente all'username e alla password (opzionale) passati.
   *
   * @param username l'username dell'utente
   * @param password (opzionale) la password dell'utente
   * @return l'user corrispondente a username e password passati come parametro.
   */
  public User getUserByUsernameAndPassword(String username, Optional<String> password) {
    final QUser user = QUser.user;
    final BooleanBuilder condition = new BooleanBuilder()
        // Solo gli utenti attivi
        .and(user.disabled.isFalse());

    if (password.isPresent()) {
      condition.and(user.password.eq(password.get()));
    }
    return getQueryFactory().selectFrom(user)
        .where(condition.and(user.username.equalsIgnoreCase(username))).fetchOne();
  }

  public User byUsername(String username) {
    return getUserByUsernameAndPassword(username, Optional.empty());
  }

  /**
   * Cerca un utente tramite il campo eppn della
   * persona associata all'utente (se presente).
   */
  public Optional<User> byEppn(String eppn) {
    final QUser user = QUser.user;
    return Optional.ofNullable(
        getQueryFactory()
          .selectFrom(user)
          .where(user.person.isNotNull(), user.person.eppn.equalsIgnoreCase(eppn))
          .fetchOne());
  }

  /**
   * Tutti gli username già presenti che contengono il pattern all'interno del proprio username.
   *
   * @param pattern pattern
   * @return list
   */
  public List<String> containsUsername(String pattern) {
    Preconditions.checkState(!Strings.isNullOrEmpty(pattern));
    final QUser user = QUser.user;

    return getQueryFactory().select(user.username).from(user)
        .where(user.username.containsIgnoreCase(pattern)).fetch();
  }

  /**
   * Ritorna true se l'user è di sistema oppure è amministatore: responsabile, personale, tecnico.
   *
   * @param user user
   * @return esito
   */
  public boolean hasAdminRoles(User user) {
    Preconditions.checkNotNull(user);

    return user.isSystemUser()
        || user.hasRoles(Role.SEAT_SUPERVISOR, Role.PERSONNEL_ADMIN,
        Role.PERSONNEL_ADMIN_MINI, Role.TECHNICAL_ADMIN);
  }
  
  /**
   * Gli utenti con almeno uno dei ruoli passati nella lista all'interno dell'office.
   *
   * @param office ufficio del quale restituire gli utenti
   * @param roles ruoli da considerare
   * @return La lista degli utenti con i ruoli specificati nell'ufficio passato come parametro
   */
  public List<User> getUsersWithRoles(final Office office, String... roles) {

    return office.getUsersRolesOffices().stream()
        .filter(uro -> Arrays.asList(roles).contains(uro.getRole().getName()))
        .map(uro -> uro.getUser()).distinct().collect(Collectors.toList());
  }

}