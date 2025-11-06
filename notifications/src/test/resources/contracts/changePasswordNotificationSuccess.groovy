package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Информирование об успешном изменении пароля"
    name "send_password_change_notification"
    label "send_password_change_notification"

    request {
        method POST()
        urlPath("/api/notifications/password@gmail.com/email") {
            queryParameters {
                parameter("message": "Ваш пароль в банковском приложении успешно изменен")
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