import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешное обновление пароля"
    name "update_password_success"
    label "update_password_success"

    request {
        method POST()
        urlPath("/api/accounts/existinguser/password") {
            queryParameters {
                parameter("password": "newpassword123")
                parameter("confirmPassword": "newpassword123")
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
            name: "Иван Иванов",
            login: "testuser",
            email: "test@yandex.ru",
            password: $(regex(".+")),
            birthdate: "1984-03-31",
            balance: 1500.0,
            errors: []
        ])
    }
}