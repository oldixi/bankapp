package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Уведомление об успешном удалении аккаунта из банковского приложения"
    name "send_account_deletion_notification"
    label "send_account_deletion_notification"

    request {
        method POST()
        urlPath("/api/notifications/user@example.com/mail") {
            queryParameters {
                parameter("text": "Ваш аккаунт был удален из банковского приложения")
            }
        }
        headers {
            contentType(applicationJson())
        }
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
    }
}
