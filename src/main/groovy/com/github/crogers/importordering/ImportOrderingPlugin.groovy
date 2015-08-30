package com.github.crogers.importordering
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.ide.idea.IdeaPlugin

@CompileStatic
public class ImportOrderingPlugin implements Plugin<Project> {
    public void apply(Project project) {
        ImportOrderingExtension extension = (ImportOrderingExtension) project.getExtensions().create("importOrdering", ImportOrderingExtension.class);

        project.getPlugins().withType(IdeaPlugin, { IdeaPlugin plugin ->
            new XmlWriter(plugin.model.project.ipr).writeXml(extension.importLines);
        });
    }
}
