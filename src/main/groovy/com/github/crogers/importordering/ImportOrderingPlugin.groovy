package com.github.crogers.importordering

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.plugins.ide.idea.IdeaPlugin

public class ImportOrderingPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("importOrdering", ImportOrderingExtension.class);

        project.getPlugins().withType(IdeaPlugin, { plugin ->
            plugin.model.project.ipr.withXml { XmlProvider xml ->
                NodeBuilder builder = new NodeBuilder()
                Node res = builder.some {
                    crazy {
                        xpath()
                    }
                }
                xml.asNode().append(res)
            }
        });
    }
}
