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
            Node res = builder.component(name: "ProjectCodeStyleSettingsManager")

            xml.asNode().append(res)
        }
    }
}
