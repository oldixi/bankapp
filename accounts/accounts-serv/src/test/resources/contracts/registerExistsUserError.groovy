import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ошибка при попытки зарегистрировать пользователя с логинром, который уже существует в системе"
    name "register_user_exists"
    label "register_user_exists"

    request {
        method POST()
        urlPath("/api/accounts/signup")
        headers {
            contentType(applicationJson())
        }
        body([
                name: "Иван Иванов",
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
                errors: ["Пользователь с логином existinguser уже существует"]
        ])
        bodyMatchers {
            jsonPath('$.name', byNull())
            jsonPath('$.password', byNull())
            jsonPath('$.email', byNull())
            jsonPath('$.birthdate', byNull())
            jsonPath('$.balance', byNull())
        }
    }
}