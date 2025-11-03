package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Уведомление об успешном изменении информации по аккаунту клиента"
    name "send_profile_update_notification"
    label "send_profile_update_notification"

    request {
        method POST()
        urlPath("/api/notifications/user@example.com/mail") {
            queryParameters {
                parameter("text": "Информация о вашем аккаунте в банковском приложении успешно изменена")
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