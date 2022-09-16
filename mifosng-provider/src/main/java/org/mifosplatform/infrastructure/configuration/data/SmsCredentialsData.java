/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.data;

public class SmsCredentialsData {

    private final String authUsername;
    private final String authPassword;
    private final String senderName;
    private final String senderAddress;
    private final Long outboundMaxPerDay;
    private final String notifyUrl;
    private final String debugPhone;
    private final boolean debug;
    private String apiKey;
    private String baseUrl;

    public SmsCredentialsData(final String authUsername, final String authPassword, final String senderName, final String senderAddress, final Long outboundMaxPerDay, final String notifyUrl, final boolean debug, final String debugPhone) {
        this.authUsername = authUsername;
        this.authPassword = authPassword;
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.outboundMaxPerDay = outboundMaxPerDay;
        this.notifyUrl = notifyUrl;
        this.debug = debug;
        this.debugPhone = debugPhone;
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

    public String getSenderAddress() {
        return senderAddress;
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

    public String getDebugPhone() {
        return debugPhone;
    }

    @Override
    public String toString() {
        return "SmsCredentialsData{" +
                "authUsername='" + authUsername + '\'' +
                ", authPassword='" + authPassword + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderAddress='" + senderAddress + '\'' +
                ", outboundMaxPerDay=" + outboundMaxPerDay +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", debugPhone='" + debugPhone + '\'' +
                ", debug=" + debug +
                '}';
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
