/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.data;

public class SmsCredentialsData {

    private final String authUsername;
    private final String authPassword;
    private final String sender;
    private final Long outboundMaxPerDay;
    private final String notifyUrl;
    private final boolean debug;

    public SmsCredentialsData(final String authUsername, final String authPassword, final String sender, final Long outboundMaxPerDay, final String notifyUrl, final boolean debug) {
        this.authUsername = authUsername;
        this.authPassword = authPassword;
        this.sender = sender;
        this.outboundMaxPerDay = outboundMaxPerDay;
        this.notifyUrl = notifyUrl;
        this.debug = debug;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public String getSender() {
        return sender;
    }

    public Long getOutboundMaxPerDay() {
        return outboundMaxPerDay;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public boolean isDebug() {
        return debug;
    }
}
