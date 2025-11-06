package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Информирование об изменении баланса ЛС"
    name "send_balance_change_notification"
    label "send_balance_change_notification"

    request {
        method POST()
        urlPath("/api/notifications/balance@gmail.com/email") {
            queryParameters {
                parameter("message": "Баланс вашего счета в банковском приложении изменился. Новый баланс:2500.0")
            }
        }
        headers {
            contentType(applicationJson())
        }
    }

    response {
        status OK()
    }
}
