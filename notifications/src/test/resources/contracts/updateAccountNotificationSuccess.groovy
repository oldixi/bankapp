package contracts


import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Уведомление об успешном изменении информации по аккаунту клиента"
    name "send_profile_update_notification"
    label "send_profile_update_notification"

    request {
        method POST()
        urlPath("/api/notifications/update@gmail.com/email") {
            queryParameters {
                parameter("message": "Информация о вашем аккаунте в банковском приложении успешно изменена")
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