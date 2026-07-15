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
