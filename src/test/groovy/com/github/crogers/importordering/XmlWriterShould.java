package com.github.crogers.importordering;

import com.google.common.base.Joiner;
import org.gradle.internal.xml.XmlTransformer;
import org.gradle.plugins.ide.api.XmlFileContentMerger;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import java.io.IOException;
import java.util.Optional;

import static com.github.crogers.importordering.ImportOrdering.importLines;
import static com.github.crogers.importordering.ImportOrdering.noImportLines;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.XmlMatchers.equivalentTo;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.transform.XmlConverters.xml;
import static org.xmlmatchers.xpath.XpathReturnType.returningAnXmlNode;

public class XmlWriterShould {
    private static final String COMPONENT_XPATH = "/project/component[@name='ProjectCodeStyleSettingsManager']";
    private static final String PROJECT_OPTION_XPATH = COMPONENT_XPATH + "/option[@name='PER_PROJECT_SETTINGS']/value";
    public static final String PACKAGE_OPTION_XPATH = PROJECT_OPTION_XPATH + "/option[@name='IMPORT_LAYOUT_TABLE']/value";

    @Test public void
    produce_a_component_with_a_name_of_ProjectCodeStyleSettingsManager() throws ParserConfigurationException, IOException, SAXException {
        xmlProducedBy(noImportLines())
                .shouldHaveXPath(COMPONENT_XPATH);
    }

    @Test public void
    produce_a_component_containing_an_option_with_a_name_of_PER_PROJECT_SETTINGS() {
        xmlProducedBy(noImportLines())
                .shouldHaveXPath(PROJECT_OPTION_XPATH);
    }

    @Test public void
    produce_a_component_containing_a_suboption_with_a_name_of_IMPORT_LAYOUT_TABLE() {
        xmlProducedBy(noImportLines())
                .shouldHaveXPath(PACKAGE_OPTION_XPATH);
    }

    @Test public void
    produce_a_package_entry_from_an_import_line() {
        xmlProducedBy(importLines("foo.bar"))
                .shouldHaveXPath(packageWithName("foo.bar"));
    }

    @Test public void
    produce_two_packages_entries_from_two_import_lines() {
        xmlProducedBy(importLines("foo.bar", "baz.quux"))
                .shouldHaveXPath(packageWithName("foo.bar"))
                .shouldHaveXPath(packageWithName("baz.quux"));
    }

    @Test public void
    produce_two_packages_entries_from_two_import_lines_in_the_right_order() {
        xmlProducedBy(importLines("foo.bar", "baz.quux"))
            .shouldHavePackageXmlEquivalentTo(
                "<package name='foo.bar' withSubpackages='true' static='false'/>",
                "<package name='baz.quux' withSubpackages='true' static='false'/>"
            );
    }

    @Test public void
    produce_a_static_package_entry() {
        xmlProducedBy(ImportOrdering.from(ImportLine.fromStatic("static.foo")))
            .shouldHavePackageXmlEquivalentTo(
                "<package name='static.foo' withSubpackages='true' static='true'/>"
            );
    }

    @Test public void
    produce_an_entry_without_subpackages() {
        xmlProducedBy(ImportOrdering.from(ImportLine.instance("nah.bah", WithSubpackages.WITHOUT_SUBPACKAGES)))
            .shouldHavePackageXmlEquivalentTo(
                "<package name='nah.bah' withSubpackages='false' static='false'/>"
            );
    }

    @Test public void
    have_the_class_count_to_use_star_import_option() {
        Settings settings = settingsWithClassCount(Optional.of(23));

        xmlProducedBy(settings)
            .shouldHaveXPath(PROJECT_OPTION_XPATH + "/option[@name='CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND'][@value='23']");
    }

    @Test public void
    not_have_the_class_count_to_use_star_import_options_if_it_has_not_been_specified() {
        Settings settings = settingsWithClassCount(Optional.empty());

        xmlProducedBy(settings)
            .shouldNotHaveXPath(PROJECT_OPTION_XPATH + "/option[@name='CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND']");
    }

    @Test public void
    have_the_name_count_to_use_star_import_option() {
        Settings settings = settingsWithNameCount(Optional.of(33));

        xmlProducedBy(settings)
            .shouldHaveXPath(PROJECT_OPTION_XPATH + "/option[@name='NAMES_COUNT_TO_USE_IMPORT_ON_DEMAND'][@value='33']");
    }

    private Settings settingsWithClassCount(Optional<Integer> value) {
        Settings settings = defaultSettings();
        when(settings.getClassCountToImportStar()).thenReturn(value);
        return settings;
    }

    private Settings settingsWithNameCount(Optional<Integer> value) {
        Settings settings = defaultSettings();
        when(settings.getNameCountToStaticImportStar()).thenReturn(value);
        return settings;
    }

    private String packageWithName(String name) {
        return PACKAGE_OPTION_XPATH + "/package[@name='" + name +"'][@withSubpackages='true'][@static='false']";
    }

    private static class XmlProducedBy {
        private final Source xmlDocument;

        public XmlProducedBy(Settings settings) {
            XmlFileContentMerger xmlFileContentMerger = new XmlFileContentMerger(new XmlTransformer());
            XmlWriter xmlWriter = new XmlWriter(xmlFileContentMerger);

            xmlWriter.writeXml(settings);
            String result = xmlFileContentMerger.getXmlTransformer().transform("<project/>");

            this.xmlDocument = xml(result);
        }

        public XmlProducedBy shouldHaveXPath(String xPath) {
            assertThat(xmlDocument, hasXPath(xPath));
            return this;
        }

        public XmlProducedBy shouldNotHaveXPath(String xPath) {
            assertThat(xmlDocument, not(hasXPath(xPath)));
            return this;
        }

        public void shouldHavePackageXmlEquivalentTo(String... packageXmls) {
            String packageXml = Joiner.on("").join(packageXmls);
            assertThat(xmlDocument, hasXPath(PACKAGE_OPTION_XPATH, returningAnXmlNode(), equivalentTo(xml("<value>" + packageXml + "</value>"))));
        }
    }

    private XmlProducedBy xmlProducedBy(ImportOrdering importOrdering) {
        Settings settings = defaultSettings();
        when(settings.getImportOrdering()).thenReturn(importOrdering);
        return xmlProducedBy(settings);
    }

    private XmlProducedBy xmlProducedBy(Settings settings) {
        return new XmlProducedBy(settings);
    }

    private Settings defaultSettings() {
        Settings settings = mock(Settings.class);
        when(settings.getImportOrdering()).thenReturn(ImportOrdering.from());
        when(settings.getClassCountToImportStar()).thenReturn(Optional.empty());
        when(settings.getNameCountToStaticImportStar()).thenReturn(Optional.empty());
        return settings;
    }
}
