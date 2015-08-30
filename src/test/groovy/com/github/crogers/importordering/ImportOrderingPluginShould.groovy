package com.github.crogers.importordering

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.hamcrest.Matcher
import org.junit.*
import org.junit.rules.TemporaryFolder

import javax.xml.transform.Source

import static org.hamcrest.MatcherAssert.assertThat
import static org.xmlmatchers.XmlMatchers.equivalentTo
import static org.xmlmatchers.XmlMatchers.hasXPath
import static org.xmlmatchers.transform.XmlConverters.the
import static org.xmlmatchers.transform.XmlConverters.xml
import static org.xmlmatchers.xpath.XpathReturnType.returningAnXmlNode

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

    @Before
    public void addClasspathToBuildfile() {
        File pluginClasspathManifest = new File("build/createClasspathManifest/plugin-classpath.txt")

        String pluginClasspath = pluginClasspathManifest.readLines()
                .collect { it.replace('\\', '\\\\') } // escape backslashes in Windows paths
                .collect { "'$it'" }
                .join(", ")

        addToBuildFile """
            buildscript {
                dependencies {
                    classpath files($pluginClasspath)
                }
            }
        """
    }

    @Before
    public void applyPlugins() {
        addToBuildFile """
            apply plugin: 'idea'
            apply plugin: 'import-ordering'
        """
    }

    @Test
    public void produce_a_single_entry_in_the_ipr_xml_with_a_single_entry() {
        addToBuildFile """
            importOrdering {
                importLine 'foo.bar'
            }
        """

        buildIdeaProject()

        assertThatIprHasPackages("<package name='foo.bar' withSubpackages='true' static='false'/>");
    }

    @Test
    public void produce_a_two_entries_in_the_ipr_xml_with_two_entries() {
        addToBuildFile """
            importOrdering {
                importLine 'foo.bar'
                importLine 'baz.quux'
            }
        """

        buildIdeaProject()

        assertThatIprHasPackages("""
            <package name='foo.bar' withSubpackages='true' static='false'/>
            <package name='baz.quux' withSubpackages='true' static='false'/>
        """);
    }

    @Test
    public void produce_a_static_entry_in_the_xml() {
        addToBuildFile """
            importOrdering {
                importStatic 'some.static.thing'
            }
        """

        buildIdeaProject()

        assertThatIprHasPackages("<package name='some.static.thing' withSubpackages='true' static='true'/>")
    }

    @Test
    @Ignore
    public void produce_an_entry_without_subpackages() {
        addToBuildFile """
            importOrdering {
                importLine withoutSubpackages(), 'javax.awt'
            }
        """

        buildIdeaProject()

        assertThatIprHasPackages("<package name='javax.awt' withSubpackages='false', static='false'/>")
    }

    public void addToBuildFile(String text) {
        buildFile << text.stripIndent()
    }

    private void buildIdeaProject() {
        BuildResult result = GradleRunner.create()
                .withProjectDir(projectDir.getRoot())
                .withArguments("idea")
                .build()

        println result.standardOutput
    }

    private void assertThatIprHasPackages(String packages) {
        Source packageXml = xml("<value>${packages}</value>")
        assertThat(the(iprFile()), hasXPathReturningAnXmlNode(
                "/project/component[@name='ProjectCodeStyleSettingsManager']"
                        + "/option[@name='PER_PROJECT_SETTINGS']/value"
                        + "/option[@name='PACKAGES_TO_USE_IMPORT_ON_DEMAND']/value", equivalentTo(packageXml)));
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private Matcher<Source> hasXPathReturningAnXmlNode(String xpath, Matcher<Source> matcher) {
        return hasXPath(xpath, returningAnXmlNode(), matcher)
    }

    private String iprFile() {
        File iprFile = new File(projectDir.getRoot(), "${projectDir.root.name}.ipr")
        return iprFile.readLines().join("\n")
    }
}
