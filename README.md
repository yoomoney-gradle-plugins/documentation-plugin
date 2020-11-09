# documentation-render-plugin

Предоставляет gradle task'и для проверки и конвертации документации из формата Asciidoc в Html

## Устройство плагина 

Существует несколько основных задач:

1. ```documentationValidation``` - задача валидирует находящиеся в проекте .adoc файлы на уникальность всех anchor'ов.
В случае непрохождения валидации задача завершается ошибкой.
2. ```convertDiagrams``` - задача конвертирует plantuml диаграммы в png картинки и кладет их рядом с диграммами.
3. ```documentationPreprocess``` - задача производит препроцессинг файлов .adoc для сборки html файлов - добавляет к путям до
ресурсов (директории resources) относительный путь до этой директории относительно корня проекта.
4. ```documentationRender``` - задача рендерит .adoc файлы, указанные в rootFiles в html, и размещает их рядом с .adoc файлами
5. ```commitEditedDocumentation``` - задача добавляет в индекс новые диаграммы, сконвертированные задачей ```convertDiagrams```, и
новые html файлы документации, сгенерированные задачей ```documentationRender```, и коммитит новые и измененные hml файлы документации
и файлы диаграмм.

Задача ```documentationPreprocess``` необходима в случае, если структура вашего репозитория следующая
```
- docDirA
    - resources
        - aa.png
        - ab.png
    - docAA.adoc
    - docAB.adoc
    - docAC.adoc
- docDirB
    - resources
        - ba.png
        - bb.png
    - docBA.adoc
    - docBB.adoc
    - docBC.adoc
- index.adoc
```
Где ```docAA.adoc```, ```docAB.adoc``` и т.д. отдельные файлы с документацией, из которых собирается общий файл с документацией
```index.adoc```. В этом случае перед генерацией файла index.html в файлах ```docAA.adoc```, ```docAB.adoc``` необходимо заменить
пути до ресурсов ```ba.png```, ```ab.png``` и т.д. на пути относительно корня, что данная задача и делает.

## Подключение
```groovy
buildscript {
    repositories {
        maven { url 'http://nexus.yamoney.ru/repository/thirdparty/' }
        maven { url 'http://nexus.yamoney.ru/repository/central/' }
        maven { url 'http://nexus.yamoney.ru/repository/releases/' }
        maven { url 'http://nexus.yamoney.ru/repository/jcenter.bintray.com/' }
        maven { url 'https://nexus.yamoney.ru/repository/gradle-plugins/' }
    }
    dependencies {
        classpath 'org.asciidoctor:asciidoctor-gradle-jvm:3.3.0'
        classpath 'ru.yandex.money.gradle.plugins:yamoney-documentation-render-plugin:0.+'
    }
}
apply plugin: 'org.asciidoctor.jvm.base'
apply plugin: 'yamoney-documentation-render-plugin'
documentations {
    rootFiles = ['department-book.adoc', 'developer-book.adoc']
}
```

Подключение плагина ```org.asciidoctor.jvm.base``` необходимо для того, чтобы был создан extension asciidoctorj, который используется
в задаче ```documentationRender```.
В ```rootFiles``` указываются файлы документации, которые должны быть сконвертированы в html файлы
