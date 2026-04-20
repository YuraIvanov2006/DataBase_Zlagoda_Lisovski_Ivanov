package org.lisovskyi_ivanov.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"));

        SpringApplication.run(BackendApplication.class, args);
    }
}

//Наразі у тебе одна невирішена проблема:
//
//Помилка: "Помилка сервера" при вході
//Що відбувається:
//
//Фронт запущений на http://localhost:5173
//При натисканні "Увійти" йде запит на http://localhost:5173/api/v1/auth/login
//Vite proxy має перенаправити його на http://localhost:8080/api/v1/auth/login
//Але відповідь не приходить — фронт показує "Помилка сервера"
//
//Найімовірніша причина:
//Spring Boot не запущений або запущений не на порту 8080. Через це proxy не може нікуди переслати запит і повертає помилку.
//
//Що потрібно зробити прямо зараз:
//
//Запусти бекенд (Spring Boot)
//Виконай в терміналі:
//
//bashcurl http://localhost:8080/api/v1/auth/login \
//        -X POST \
//        -H "Content-Type: application/json" \
//        -d '{"login":"manager","password":"password"}'
//Скинь результат сюди — або відповідь від сервера, або текст помилки.
