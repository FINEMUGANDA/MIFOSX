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
    private final String senderName;
    private final boolean startTls;
    private final boolean debug;
    private final int smtpPort;

    public EmailCredentialsData(final String host, final String authUsername, final String authPassword, final String senderName, final boolean startTls, final boolean debug, int smtpPort) {
        this.host = host;
        this.authUsername = authUsername;
        this.authPassword = authPassword;
        this.senderName = senderName;
        this.startTls = startTls;
        this.debug = debug;
        this.smtpPort = smtpPort;
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

    public String getSenderName() {
        return senderName;
    }

    public boolean isStartTls() {
        return startTls;
    }

    public boolean isDebug() {
        return debug;
    }

    public int getSmtpPort() {
        return smtpPort;
    }
}
