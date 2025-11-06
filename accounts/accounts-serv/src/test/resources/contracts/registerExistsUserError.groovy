import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ошибка при попытки зарегистрировать пользователя с логином, который уже существует в системе"
    name "register_user_exists"
    label "register_user_exists"

    request {
        method POST()
        urlPath("/api/accounts/signup")
        headers {
            contentType(applicationJson())
        }
        body([
                name: "Вася Пупкин",
                login: "existinguser",
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
                login: "existinguser",
                balance: 0.0,
                errors: ["Пользователь с логином existinguser уже существует"]
        ])
    }
}