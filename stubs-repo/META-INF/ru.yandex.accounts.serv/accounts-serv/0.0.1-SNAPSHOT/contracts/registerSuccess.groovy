import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Успешная регистрация пользователя"
    name "register_success"
    label "register_success"

    request {
        method POST()
        urlPath("/api/accounts/signup")
        headers {
            contentType(applicationJson())
        }
        body([
                name: "Иван Иванов",
                login: "testuser",
                password: "123456",
                confirmPassword: "123456",
                email: "test@yandex.ru",
                birthdate: "1984-03-31"
        ])
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
                name: "Иван Иванов",
                login: "testuser",
                password: $(regex(".+")),
                email: "test@yandex.ru",
                birthdate: "1984-03-31",
                balance: 0.0,
                errors: []
        ])
    }
}