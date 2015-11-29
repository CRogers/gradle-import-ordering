package com.github.crogers.importstyle

import org.gradle.api.XmlProvider
import org.gradle.plugins.ide.api.XmlFileContentMerger

public class XmlWriter {
    private final XmlFileContentMerger xmlFileContentMerger;

    public XmlWriter(XmlFileContentMerger xmlFileContentMerger) {
        this.xmlFileContentMerger = xmlFileContentMerger
    }

    public void writeXml(Settings settings) {
        xmlFileContentMerger.withXml { XmlProvider xml ->
            NodeBuilder builder = new NodeBuilder()
            Node res = builder.component(name: "ProjectCodeStyleSettingsManager") {
                option(name: 'PER_PROJECT_SETTINGS') {
                    value() {
                        if  (settings.classCountToImportStar.present) {
                            option(name: 'CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND', value: settings.classCountToImportStar.get())
                        }
                        if  (settings.nameCountToStaticImportStar.present) {
                            option(name: 'NAMES_COUNT_TO_USE_IMPORT_ON_DEMAND', value: settings.nameCountToStaticImportStar.get())
                        }
                        option(name: 'IMPORT_LAYOUT_TABLE') {
                            value() {
                                for (ImportLine importLine : settings.importOrdering) {
                                    'package'(name: importLine.asString(), withSubpackages: importLine.withSubpackages().asBoolean(), static: importLine.static)
                                }
                            }
                        }
                    }
                }
                option(name: 'USE_PER_PROJECT_SETTINGS', value: 'true')
            }

            xml.asNode().append(res)
        }
    }
}
