import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Ошибка при указании различных пороля и подтверждения пароля"
    name "register_password_mismatch"
    label "register_password_mismatch"

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
                confirmPassword: "1234567",
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
            login: "newuser",
            errors: ["Пароли не совпадают"]
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