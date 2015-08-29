package com.github.crogers.importordering

import org.gradle.api.XmlProvider
import org.gradle.plugins.ide.idea.model.IdeaProject

public class XmlWriter {
    private final IdeaProject project;

    public XmlWriter(IdeaProject project) {
        this.project = project
    }

    public void writeXml() {
        project.ipr.withXml { XmlProvider xml ->
            NodeBuilder builder = new NodeBuilder()
            Node res = builder.component(name: "ProjectCodeStyleSettingsManager")

            xml.asNode().append(res)
        }
    }
}
