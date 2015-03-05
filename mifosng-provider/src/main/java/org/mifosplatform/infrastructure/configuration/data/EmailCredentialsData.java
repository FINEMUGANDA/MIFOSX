/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.data;

public class EmailCredentialsData {

    private final String host;
    private final String authUsername;
    private final String authPassword;
    private final boolean startTls;
    private final boolean debug;

    public EmailCredentialsData(final String host, final String authUsername, final String authPassword, final boolean startTls, final boolean debug) {
        this.host = host;
        this.authUsername = authUsername;
        this.authPassword = authPassword;
        this.startTls = startTls;
        this.debug = debug;
    }

    public String getHost() {
        return host;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public boolean isStartTls() {
        return startTls;
    }

    public boolean isDebug() {
        return debug;
    }
}
