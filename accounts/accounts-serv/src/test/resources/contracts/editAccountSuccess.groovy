import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешное изменение информации о пользователе"
    name "update_profile_success"
    label "update_profile_success"

    request {
        method POST()
        urlPath("/api/accounts/existinguser/edit") {
            queryParameters {
                parameter("name": "Новое Имя")
                parameter("email": "oldixi@yandex.ru")
                parameter("birthdate": "1983-03-17")
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
        body([
                name: "Новое Имя",
                login: "existinguser",
                password: $(regex(".+")),
                email: "newemail@example.com",
                birthdate: "1985-05-15",
                balance: 1500.0,
                errors: []
        ])
    }
}