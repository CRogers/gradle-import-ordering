package com.github.crogers.importordering

import groovy.transform.CompileStatic
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
            .withArguments("jar")
            .build()
    }

    @Before
    public void createBuildFile() throws IOException {
        buildFile = projectDir.newFile("build.gradle");
    }

    @Test
    public void produce_a_single_entry_in_the_ipr_xml_with_a_single_entry() {
        String buildDir = new File("build").absolutePath

        buildFile << """
            buildscript {
                dependencies {
                    classpath files("${buildDir}/classes/main")
                    classpath files("${buildDir}/resources/main")
                }
            }

            apply plugin: 'idea'
            apply plugin: 'import-ordering'

            importOrdering {
                importLine "foo.bar.*"
            }
        """.stripIndent()

        GradleRunner.create()
            .withProjectDir(projectDir.getRoot())
            .withArguments("idea")
            .build()

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        Document doc = documentBuilder.parse(new File(projectDir.getRoot(), "foo.ipr"))

        assertThat(doc, hasXPath("/some/crazy/xpath"))
    }
}
