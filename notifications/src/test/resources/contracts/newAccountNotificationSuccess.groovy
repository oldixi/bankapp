import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Информирование об успешном создании аккаунта клиента"
    name "send_welcome_notification"
    label "send_welcome_notification"

    request {
        method POST()
        urlPath("/api/notifications/newuser@example.com/mail") {
            queryParameters {
                parameter("text": "Добро пожаловать в банковское приложение! Ваш аккаунт успешно создан.")
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