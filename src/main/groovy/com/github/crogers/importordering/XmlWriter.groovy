package com.github.crogers.importordering
import org.gradle.api.XmlProvider
import org.gradle.plugins.ide.api.XmlFileContentMerger

public class XmlWriter {
    private final XmlFileContentMerger xmlFileContentMerger;

    public XmlWriter(XmlFileContentMerger xmlFileContentMerger) {
        this.xmlFileContentMerger = xmlFileContentMerger
    }

    public void writeXml() {
        xmlFileContentMerger.withXml { XmlProvider xml ->
            NodeBuilder builder = new NodeBuilder()
            Node res = builder.component(name: "ProjectCodeStyleSettingsManager") {
                option(name: 'PER_PROJECT_SETTINGS') {
                    value() {
                        option(name: 'PACKAGES_TO_USE_IMPORT_ON_DEMAND') {
                            value()
                        }
                    }
                }
            }

            xml.asNode().append(res)
        }
    }
}
