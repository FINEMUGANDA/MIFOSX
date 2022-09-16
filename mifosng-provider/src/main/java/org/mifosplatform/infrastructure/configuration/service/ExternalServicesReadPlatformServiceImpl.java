/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.infrastructure.configuration.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifosplatform.infrastructure.configuration.data.EmailCredentialsData;
import org.mifosplatform.infrastructure.configuration.data.S3CredentialsData;
import org.mifosplatform.infrastructure.configuration.data.SmsCredentialsData;
import org.mifosplatform.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

@Service
public class ExternalServicesReadPlatformServiceImpl implements ExternalServicesReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ExternalServicesReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class S3CredentialsDataExtractor implements ResultSetExtractor<S3CredentialsData> {

        @Override
        public S3CredentialsData extractData(final ResultSet rs) throws SQLException, DataAccessException {
            String accessKey = null;
            String bucketName = null;
            String secretKey = null;
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.S3_ACCESS_KEY)) {
                    accessKey = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.S3_BUCKET_NAME)) {
                    bucketName = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.S3_SECRET_KEY)) {
                    secretKey = rs.getString("value");
                }
            }
            return new S3CredentialsData(bucketName, accessKey, secretKey);
        }
    }

    @Override
    public S3CredentialsData getS3Credentials() {
        final ResultSetExtractor<S3CredentialsData> resultSetExtractor = new S3CredentialsDataExtractor();
        final String sql = "SELECT es.name, es.value FROM c_external_service es where es.name in('s3_bucket_name','s3_access_key','s3_secret_key')";
        final S3CredentialsData s3CredentialsData = this.jdbcTemplate.query(sql, resultSetExtractor, new Object[] {});
        return s3CredentialsData;
    }

    private static final class EmailCredentialsDataExtractor implements ResultSetExtractor<EmailCredentialsData> {

        @Override
        public EmailCredentialsData extractData(final ResultSet rs) throws SQLException, DataAccessException {
            String host = null;
            String authUsername = null;
            String authPassword = null;
            String senderName = null;
            boolean startTls = false;
            boolean debug = false;
            int smtpPort = 0;
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.EMAIL_HOST)) {
                    host = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.EMAIL_AUTH_USERNAME)) {
                    authUsername = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.EMAIL_AUTH_PASSWORD)) {
                    authPassword = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.EMAIL_SENDER_NAME)) {
                    senderName = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.EMAIL_STARTTLS)) {
                    startTls = "true".equals(rs.getString("value"));
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.EMAIL_DEBUG)) {
                    debug = "true".equals(rs.getString("value"));
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.EMAIL_SMTP_PORT)) {
                    smtpPort = rs.getInt("value");
                }
            }
            return new EmailCredentialsData(host, authUsername, authPassword, senderName, startTls, debug, smtpPort);
        }
    }

    @Override
    public EmailCredentialsData getEmailCredentials() {
        final ResultSetExtractor<EmailCredentialsData> resultSetExtractor = new EmailCredentialsDataExtractor();
        final String sql = "SELECT es.name, es.value FROM c_external_service es where es.name like 'email_%'";
        final EmailCredentialsData credentialsData = this.jdbcTemplate.query(sql, resultSetExtractor, new Object[] {});
        return credentialsData;
    }

    private static final class SmsCredentialsDataExtractor implements ResultSetExtractor<SmsCredentialsData> {

        @Override
        public SmsCredentialsData extractData(final ResultSet rs) throws SQLException, DataAccessException {
            String authUsername = null;
            String authPassword = null;
            String senderName = null;
            String senderAddress = null;
            Long outboundMaxPerDay = -1L;
            String notifyUrl = null;
            boolean debug = false;
            String debugPhone = null;
            String apiKey = null;
            String baseUrl = null;
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMS_AUTH_USERNAME)) {
                    authUsername = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMS_AUTH_PASSWORD)) {
                    authPassword = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMS_SENDER_NAME)) {
                    senderName = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMS_SENDER_ADDRESS)) {
                    senderAddress = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMS_OUTBOUND_MAX_PER_DAY)) {
                    outboundMaxPerDay = Long.valueOf(rs.getString("value"));
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMS_NOTIFY_URL)) {
                    notifyUrl = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMS_DEBUG)) {
                    debug = "true".equals(rs.getString("value"));
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMS_DEBUG_PHONE)) {
                    debugPhone = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMS_API_KEY)) {
                    apiKey = rs.getString("value");
                } else if (rs.getString("name").equalsIgnoreCase(ExternalServicesConstants.SMS_BASE_URL)) {
                    baseUrl = rs.getString("value");
                }
            }
            SmsCredentialsData smsCredentialsData = new SmsCredentialsData(authUsername, authPassword, senderName, senderAddress, outboundMaxPerDay, notifyUrl, debug, debugPhone);
            smsCredentialsData.setApiKey(apiKey);
            smsCredentialsData.setBaseUrl(baseUrl);
            return smsCredentialsData;
        }
    }

    @Override
    public SmsCredentialsData getSmsCredentials() {
        final ResultSetExtractor<SmsCredentialsData> resultSetExtractor = new SmsCredentialsDataExtractor();
        final String sql = "SELECT es.name, es.value FROM c_external_service es where es.name like 'sms_%'";
        final SmsCredentialsData credentialsData = this.jdbcTemplate.query(sql, resultSetExtractor, new Object[] {});
        return credentialsData;
    }
}
