package com.github.crogers.importordering
import groovy.transform.CompileStatic
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.w3c.dom.Document

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.xml.HasXPath.hasXPath

@CompileStatic
public class ImportOrderingPluginShould {
    @Rule public final TemporaryFolder projectDir = new TemporaryFolder();
    private File buildFile;

    @BeforeClass
    public static void buildProject() {
        GradleRunner.create()
            .withProjectDir(new File("."))
            .withArguments("jar", "createClasspathManifest")
            .build()
    }

    @Before
    public void printTempFolder() {
        println projectDir.root
    }

    @Before
    public void createBuildFile() throws IOException {
        buildFile = projectDir.newFile("build.gradle");
    }

    @Test
    public void produce_a_single_entry_in_the_ipr_xml_with_a_single_entry() {
        File pluginClasspathManifest = new File("build/createClasspathManifest/plugin-classpath.txt")

        String pluginClasspath = pluginClasspathManifest.readLines()
                .collect { it.replace('\\', '\\\\') } // escape backslashes in Windows paths
                .collect { "'$it'" }
                .join(", ")

        buildFile << """
            buildscript {
                dependencies {
                    classpath files($pluginClasspath)
                }
            }
        """

        buildFile << """
            apply plugin: 'idea'
            apply plugin: 'import-ordering'

            importOrdering {
                importLine "foo.bar"
            }
        """.stripIndent()

        BuildResult result = GradleRunner.create()
            .withProjectDir(projectDir.getRoot())
            .withArguments("idea")
            .build()

        println result.standardOutput

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        File iprFile = new File(projectDir.getRoot(), "${projectDir.root.name}.ipr")
        Document doc = documentBuilder.parse(iprFile)

        println iprFile.readLines().join("\n")

        assertThat(doc, hasXPath(
                "/project/component[@name='ProjectCodeStyleSettingsManager']"
                    + "/option[@name='PER_PROJECT_SETTINGS']/value"
                        + "/option[@name='PACKAGES_TO_USE_IMPORT_ON_DEMAND']/value"
                            + "/package[@name='foo.bar'][@withSubpackages='false'][@static='false']"));
    }
}
