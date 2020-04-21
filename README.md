# TinderClientServer
Разработать клиент-серверное прилоежние на spring boot
Сервер один 
Клиентов может быть много 

ФУНКЦИОНАЛЬНЫЕ ТРЕБОВАНИЯ

1. При запуске показывается первая анкета в формате:
----------------------------------
- Мстительный авантюрист мечтает -
- отойти от дел в уютной усадьбе -
- с любимой женщиной             -
----------------------------------
Возможные команды:
shell> влево
shell> вправо
shell> анкета
shell> любимцы

2. shell> влево
Смахивает анкету, показывается следующая

3. shell> вправо
Подтверждает свой интерес и если произошел матч то должна появиться надпись:
|| Вы любимы ||
И показывается новая анкета

ПРИ ЭТОМ ЕСЛИ ПОЛЬЗОВАТЕЛЬ АНОНИМНЫЙ ТО ПОКАЗЫВАЮТСЯ ВСЕ ПОДРЯД АНКЕТЫ ПО КРУГУ

4. shell> анкета
Создание и редактирование вашей анкеты.
Доступные команды:
shell> Войти
shell> Новая

5. shell> Войти
|| Сударь иль сударыня введите  логинъ  и пароль черезъ пробѣлъ: ||
shell>Альбертъ Обольститель777
Если авторизация прошла то должна быть надпись:
|| Успехъ ||
И возвращается на показ анкет. Выводит одну на экран.
Если нет, то:
|| Неудача, попробуйте снова ||

6. shell> Новая
|| Вы сударь иль сударыня? Как вас величать? Ваш секретный шифръ? ||
shell> сударь "Мечтательный анархистъ" д0л0йцарR
Если ввод верный то выводится надпись:
|| Успехъ ||
И возвращается на показ анкет. Выводит одну на экран. Теперь анкеты показываются только противоположного пола и не повторяются.

7. shell> Любимцы
Показывает матчи
Если есть матч, то выводятся имена матчей:
|| 1. Милая проказница ||
|| 2. Бѣдная Лиза ||
|| 3. Состоятельная особа ||

Можно просмотреть анкету если написать её номер:
shell> 2
------------------------------
- Бѣдная Лиза уже не знает   -
- чем себя занять, возможно  -
- человѣкъ съ сильной волей  -
- способенъ обуздать её тягу -
- ко всему новому?           -
------------------------------
Можно вернуться на на просмотр анкет командой:
shell> Уйти

НЕФУНКЦИОНАЛЬНЫЕ ТРЕБОВАНИЯ

1. Адрес сервера подключения должен задаваться в application.properties клиента
2. Сервер должен быть написан на Rest-Controller-ах
3. Можно хранить всё в памяти на сервере
4. Максимум логики вынести на сервер