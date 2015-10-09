package org.mifosplatform.infrastructure.notification.service;

public interface NotificationService {
    void notifyPaymentReminders();

    void notifyFollowUps();

    void notifyExpiredLoanPaymentReminders();
}
