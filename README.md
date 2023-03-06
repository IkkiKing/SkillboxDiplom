###Блоговый движок
Дипломная работа по курсу Java-разработчик с нуля. 
Блоговый движок представляет из себя веб-сайт с возможность для общения пользователей. 
Можно читать публикации, добавлять свои, оценивать имеющиеся и оставлять комментарии. 
Веб-сайт написан с применением следующего стека технологий:
  spring-boot
  hibernate
  flyway
  mysql
  junit5
Развёрнут на heroku тут : https://smelov-java-skillbox.herokuapp.com/
  

###Локальный запуск
Создать БД mysql версии 5 либо выше.
Задать ряд переменных окружения можно через bash-скрипт [Пример скрипта создания](runapp.sh.sample)
1. Порт для запуска приложения: PORT(default = 8080)
2. База данных:
    2.1 Строка подключения к базе данных: DB_URL
    2.2 Схема базы данных: DB_USERNAME
    2.3 Пароль базы данных: DB_PASSWORD
3. Тип файлового хранилища(Облачное/ПК): STORAGE_TYPE(cloud/pc)
4. Параметры почтового ящика для восстановления пароля:
    4.1 Почтовый сервер = EMAIL_HOST(Например = smtp.yandex.ru)
    4.2 Порт почтового сервера сервер = EMAIL_PORT(Например = 465)
    4.3 Почтовый ящик для рассылки о восстановлении = EMAIL_RESTORE(Например = something@yandex.ru)
    4.4 Пароль к почтовому ящику = EMAIL_PASSWORD(QWERTY123)
    4.5 Протокол передачи почты = EMAIL_PROTOCOL(smtps)
5. Собрать пакет командой mvn package
6. Запустить проект в консоли командой ```bash runapp.sh.sample```

###Структура проекта
1. Конфигурация: 
    1.1 \src\main\java\com\ikkiking\config
    1.2 \src\main\java\com\ikkiking\security

2. Слой сущностей БД: \src\main\java\com\ikkiking\model

3. Слой контроллеров: \src\main\java\com\ikkiking\controller

4. Сервисный слой: \src\main\java\com\ikkiking\service

5. Репозитории для работы с БД: \src\main\java\com\ikkiking\controller

6. DTO для работы с фронтом: \src\main\java\com\ikkiking\api

7. Общие классы: \src\main\java\com\ikkiking\base

###checkstyle
[Файл с правилами написания кода](checkstyle.xml)

###Документация к разработке
[Техническое задание](API%20BLOG.pdf)
[Спецификация БД](DB%20BLOG.pdf)

master commit