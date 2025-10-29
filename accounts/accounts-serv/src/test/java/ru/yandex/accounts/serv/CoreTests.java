package ru.yandex.accounts.serv;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.accounts.serv.accounts.AccountRepository;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CoreTests {
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected ApplicationContext context;

    @BeforeAll
    public void createData() {
        System.out.println("Start createData");
        jdbcTemplate.execute("""
			DO $$
			DECLARE
				acccount_id numeric := 0;
				user_id numeric := 0;
			BEGIN
				delete from accounts;
				delete from users;
			    insert into accounts(login, password, name, email, birthdate, balance) values('user1', '$2a$10$jurTwq7mqMVbqAbvHuLtHeuInZsfSVj58hwms7qeDDS2YMAUwyHLe', 'Юзер 1', 'oldixi@yandex.ru', to_date('31.03.1984', 'dd.mm.yyyy'), 1000);
				insert into users(login, password, name, email, birthdate, balance) values('user2', '$2a$10$jurTwq7mqMVbqAbvHuLtHeuInZsfSVj58hwms7qeDDS2YMAUwyHLe', 'Юзер 2', 'oldixi@yandex.ru', to_date('31.03.1984', 'dd.mm.yyyy'), 100);
			END $$;
			""");
        accountRepository.findAll().forEach(System.out::println);
    }
}
