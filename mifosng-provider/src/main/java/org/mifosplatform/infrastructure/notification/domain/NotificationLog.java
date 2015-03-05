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

    @Column(name = "sent_at", nullable = false)
    private Date sentAt;

    @Column(name = "sent", nullable = false)
    private Boolean sent;

    public NotificationLog(final NotificationType type, final Date sentAt, final Boolean sent) {
        this.type = type.name();
        this.sentAt = sentAt;
        this.sent = sent;
    }

    public NotificationType getType() {
        return NotificationType.valueOf(type);
    }

    public Date getSentAt() {
        return sentAt;
    }

    public Boolean isSent() {
        return sent;
    }
}
