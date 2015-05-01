/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.useradministration.domain;

import org.mifosplatform.infrastructure.security.domain.PlatformUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AppUserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser>, PlatformUserRepository {

    @Query("update AppUser au set au.loginFailures = au.loginFailures+1 where au.username=?1")
    @Modifying
    void incrementLoginFailures(String username);

    @Query("update AppUser au set au.loginFailures = 0 where au.username=?1")
    @Modifying
    void resetLoginFailures(String username);

    @Query("update AppUser au set au.accountNonLocked = false where au.username=?1")
    @Modifying
    void lockUser(String username);

    @Query("select au.loginFailures from AppUser au where au.username=?1")
    Integer getLoginFailuresByUsername(String username);

    @Query("select au.email from AppUser au where au.username=?1")
    String getEmailByUsername(String username);
}
