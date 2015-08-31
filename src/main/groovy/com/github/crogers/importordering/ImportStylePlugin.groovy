package com.github.crogers.importordering
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.ide.idea.IdeaPlugin

@CompileStatic
public class ImportStylePlugin implements Plugin<Project> {
    public void apply(Project project) {
        ImportStyleExtension extension = (ImportStyleExtension) project.getExtensions().create("importStyle", ImportStyleExtension.class);

        project.getPlugins().withType(IdeaPlugin, { IdeaPlugin plugin ->
            new XmlWriter(plugin.model.project.ipr).writeXml(extension);
        });
    }
}
