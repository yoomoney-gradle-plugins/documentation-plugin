[![Build Status](https://travis-ci.com/yoomoney/documentation-plugin.svg?branch=master)](https://travis-ci.com/yoomoney/documentation-plugin)
[![codecov](https://codecov.io/gh/yoomoney/documentation-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/yoomoney/documentation-plugin)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# documentation-plugin

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
новые html файлы документации, сгенерированные задачей ```documentationRender```, и коммитит новые и измененные html файлы документации
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
        mavenCentral()
    }
    dependencies {
        classpath 'ru.yoomoney.gradle.plugins:documentation-plugin:1.+'
    }
}
apply plugin: 'ru.yoomoney.gradle.plugins.documentation-plugin'
documentations {
    rootFiles = ['department-book.adoc', 'developer-book.adoc']
}
```

В ```rootFiles``` указываются файлы документации, которые должны быть сконвертированы в html файлы
