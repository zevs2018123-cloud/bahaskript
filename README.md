# bahaskript

Скрипт менеджера v3 (воронка 9 этапов, @bahaxau) — переписан с HTML+CSS+JS
на Java 21. Контент оформлен как типизированная модель (`Content.Section`,
`CardBlock`, `Message`, `RiskBlock`, …); рендер генерирует ту же страницу,
что и исходный HTML. Запускается либо как локальный HTTP-сервер, либо как
одноразовый рендер в файл.

## Требования

- JDK 21+ (Maven опционален)

## Собрать и запустить

Через Maven:

```
mvn package
java -jar target/bahaskript.jar
```

Без Maven, только javac:

```
mkdir -p target/classes
find src/main/java -name '*.java' | xargs javac -d target/classes --release 21
java -cp target/classes com.bahaskript.Application
```

По умолчанию поднимается сервер на `http://localhost:8080/`. Ctrl+C — стоп.

## Аргументы CLI

```
bahaskript                    сервер на порту 8080
bahaskript --port 9000        сервер на выбранном порту
bahaskript --out page.html    один раз отрендерить в файл и выйти
```

## GitHub Pages

Сайт (bahaskr.com) раздаётся GitHub Pages из корня этой ветки как статика:
Pages не исполняет Java, поэтому в корне лежит сгенерированный `index.html`.
После правок контента в `ScriptData.java` его нужно перегенерировать и
закоммитить:

```
java -cp target/classes com.bahaskript.Application --out index.html
```

### DNS и HTTPS для bahaskr.com

Чтобы домен работал и GitHub выпустил HTTPS-сертификат, у регистратора
домена должны стоять записи:

| Тип   | Имя (host)    | Значение                       |
|-------|---------------|--------------------------------|
| A     | @ (bahaskr.com) | 185.199.108.153              |
| A     | @             | 185.199.109.153                |
| A     | @             | 185.199.110.153                |
| A     | @             | 185.199.111.153                |
| CNAME | www           | zevs2018123-cloud.github.io    |

Опционально IPv6 (AAAA для @): 2606:50c0:8000::153, 2606:50c0:8001::153,
2606:50c0:8002::153, 2606:50c0:8003::153.

После добавления записей: Settings → Pages → дождаться «DNS check
successful», затем включить «Enforce HTTPS» (галка станет доступна после
выпуска сертификата, обычно от 15 минут до нескольких часов). Других
A-записей у @ быть не должно; если у DNS-провайдера есть проксирование
(например, оранжевое облако Cloudflare), на время выпуска сертификата его
нужно выключить. CAA-записи, если есть, должны разрешать letsencrypt.org.

## Структура

```
src/main/java/com/bahaskript/
├── Application.java           точка входа + HTTP-сервер (jdk.httpserver)
├── model/Content.java         sealed модели: Page, Section, Block, CardElement, …
├── render/HtmlRenderer.java   рендерит модель в HTML, инлайнит CSS и JS
└── data/
    ├── Dsl.java               короткие фабричные методы для читаемости
    └── ScriptData.java        сам контент: 9 этапов, дожим, процессы, риски
```

Контент правится в `ScriptData.java`. Стили и клиентский JS — в
`HtmlRenderer.java` как константы.
