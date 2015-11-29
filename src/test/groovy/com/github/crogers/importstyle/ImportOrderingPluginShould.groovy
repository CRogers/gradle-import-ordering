package com.github.crogers.importstyle
import com.google.common.collect.ImmutableList
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.PackageEntry
import groovy.transform.CompileStatic
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.hamcrest.Matcher
import org.jdom.Document
import org.jdom.Element
import org.jdom.input.SAXBuilder
import org.jdom.xpath.XPath
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.github.crogers.importstyle.PackageEntryMatchers.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.contains
import static org.xmlmatchers.XmlMatchers.hasXPath
import static org.xmlmatchers.transform.XmlConverters.the

@CompileStatic
public class ImportOrderingPluginShould {
    public static final String PER_PROJECT_SETTINGS_XPATH = "/project/component[@name='ProjectCodeStyleSettingsManager']/option[@name='PER_PROJECT_SETTINGS']/value"
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
            apply plugin: 'import-style'
        """
    }

    @Test
    public void produce_a_single_entry_in_the_ipr_xml_with_a_single_entry() {
        addToBuildFile """
            importStyle {
                importOrdering {
                    importLine 'foo.bar'
                }
            }
        """

        buildIdeaProject()

        assertThatIprHasPackages(
            package_(named('foo.bar'), withSubpackages(), notStatic())
        )
    }

    @Test
    public void produce_a_two_entries_in_the_ipr_xml_with_two_entries() {
        addToBuildFile """
            importStyle {
                importOrdering {
                    importLine 'foo.bar'
                    importLine 'baz.quux'
                }
            }
        """

        buildIdeaProject()

        assertThatIprHasPackages(
            package_(named('foo.bar'), withSubpackages(), notStatic()),
            package_(named('baz.quux'), withSubpackages(), notStatic())
        )
    }

    @Test
    public void produce_a_static_entry_in_the_xml() {
        addToBuildFile """
            importStyle {
                importOrdering {
                    importStatic 'some.static.thing'
                }
            }
        """

        buildIdeaProject()

        assertThatIprHasPackages(
            package_(named('some.static.thing'), withSubpackages(), isStatic())
        )
    }

    @Test
    public void produce_an_entry_without_subpackages() {
        addToBuildFile """
            importStyle {
                importOrdering {
                    importLine withoutSubpackages(), 'javax.awt'
                }
            }
        """

        buildIdeaProject()

        assertThatIprHasPackages(
            package_(named('javax.awt'), withoutSubpackages(), notStatic())
        )
    }

    @Test
    public void produce_a_static_entry_without_subpackages() {
        addToBuildFile """
            importStyle {
                importOrdering {
                    importStatic withoutSubpackages(), 'bat.man'
                }
            }
        """

        buildIdeaProject()

        assertThatIprHasPackages(
            package_(named('bat.man'), withoutSubpackages(), isStatic())
        )
    }

    @Test
    public void produce_a_whole_load_combinations_of_import_lines() {
        addToBuildFile """
            importStyle {
                importOrdering {
                    importStatic 'foo.bar'
                    importLine withoutSubpackages(), 'baz.quu'
                    importStatic withoutSubpackages(), 'bat.man'
                    importLine 'vanilla'
                }
            }
        """

        buildIdeaProject()

        assertThatIprHasPackages(
            package_(named("foo.bar"), withSubpackages(), isStatic()),
            package_(named("baz.quu"), withoutSubpackages(), notStatic()),
            package_(named("bat.man"), withoutSubpackages(), isStatic()),
            package_(named("vanilla"), withSubpackages(), notStatic())
        );
    }

    @Test
    public void set_the_class_count_to_use_import_on_demand() {
        addToBuildFile """
            importStyle {
                classCountToImportStar 29
            }
        """

        buildIdeaProject()

        assertThat(the(iprFile()), hasXPath(PER_PROJECT_SETTINGS_XPATH + "/option[@name='CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND'][@value='29']"))
    }

    @Test
    public void set_the_name_count_to_use_import_on_demand() {
        addToBuildFile """
            importStyle {
                nameCountToStaticImportStar 31
            }
        """

        buildIdeaProject()

        assertThat(the(iprFile()), hasXPath(PER_PROJECT_SETTINGS_XPATH + "/option[@name='NAMES_COUNT_TO_USE_IMPORT_ON_DEMAND'][@value='31']"))
    }
    
    @Test
    public void read_the_import_ordering_from_a_checkstyle_xml() {
        addToBuildFile """
            importStyle {
            }
        """

        buildIdeaProject()



        File checkstyleXml = projectDir.newFile("checkstyle.xml")
        checkstyleXml << """
            <?xml version="1.0"?>
            <!DOCTYPE module PUBLIC
                "-//Puppy Crawl//DTD Check Configuration 1.2//EN"
                "http://www.puppycrawl.com/dtds/configuration_1_2.dtd">

            <module name="Checker">
                <module name="TreeWalker">

                </module>
            </module>
        """.stripIndent()

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

    private void assertThatIprHasPackages(Matcher<PackageEntry>... packageEntryMatchers) {
        Document doc = new SAXBuilder().build(iprFileLocation());
        Element el = (Element) XPath.newInstance(PER_PROJECT_SETTINGS_XPATH).selectSingleNode(doc);

        CodeStyleSettings css = new CodeStyleSettings(false);
        css.readExternal(el);

        assertThat(Arrays.asList(css.IMPORT_LAYOUT_TABLE.getEntries()), contains(ImmutableList.<Matcher<PackageEntry>>builder()
            .add(packageEntryMatchers)
            .add(package_(named("<blank line>")))
            .add(package_(named("<all other static imports>")))
            .build()));
    }

    private String iprFile() {
        File iprFile = iprFileLocation()
        return iprFile.readLines().join("\n")
    }

    private File iprFileLocation() {
        return new File(projectDir.getRoot(), "${projectDir.root.name}.ipr")
    }
}
