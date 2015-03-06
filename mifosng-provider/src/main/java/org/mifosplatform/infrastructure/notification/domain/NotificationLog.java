package org.mifosplatform.infrastructure.notification.domain;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "notification_log")
public class NotificationLog extends AbstractPersistable<Long> {
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "entity_name", nullable = false)
    private String entityName;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "sent_at", nullable = false)
    private Date sentAt;

    @Column(name = "sent", nullable = false)
    private Boolean sent;

    @Column(name = "sms_error")
    private String smsError;

    @Column(name = "message_id")
    private String messageId;

    public NotificationLog(final NotificationType type, String recipient, final Date sentAt, final Boolean sent, String messageId) {
        this(type, recipient, sentAt, sent, null, null, null, messageId);
    }

    public NotificationLog(final NotificationType type, String recipient, final Date sentAt, final Boolean sent, String entityName, Long entityId, String smsError, String messageId) {
        this.type = type.name();
        this.recipient = recipient;
        this.entityName = entityName;
        this.entityId = entityId;
        this.sentAt = sentAt;
        this.sent = sent;
        this.smsError = smsError;
        this.messageId = messageId;
    }

    public NotificationType getType() {
        return NotificationType.valueOf(type);
    }

    public String getRecipient() {
        return recipient;
    }

    public String getEntityName() {
        return entityName;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getSmsError() {
        return smsError;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public Boolean isSent() {
        return sent;
    }

    public String getMessageId() {
        return messageId;
    }
}
