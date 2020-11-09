package ru.yandex.money.gradle.plugins.documentation.render

/**
 * Объект расширение DSL gradle для конфигурации плагина
 * @author Igor Popov
 * @since 06.11.2020
 */
open class DocumentationRenderExtension {
    /**
     * Рутовые файлы, которые необходимо отрендерить в .html
     */
    var rootFiles: MutableList<String> = mutableListOf()
}
