package com.github.crogers.importordering;

import com.google.common.base.Joiner;
import org.gradle.internal.xml.XmlTransformer;
import org.gradle.plugins.ide.api.XmlFileContentMerger;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import java.io.IOException;

import static com.github.crogers.importordering.ImportLines.importLines;
import static com.github.crogers.importordering.ImportLines.noImportLines;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.xmlmatchers.XmlMatchers.equivalentTo;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.transform.XmlConverters.xml;
import static org.xmlmatchers.xpath.XpathReturnType.returningAnXmlNode;

public class XmlWriterShould {
    private static final String COMPONENT_XPATH = "/project/component[@name='ProjectCodeStyleSettingsManager']";
    private static final String PROJECT_OPTION_XPATH = COMPONENT_XPATH + "/option[@name='PER_PROJECT_SETTINGS']/value";
    public static final String PACKAGE_OPTION_XPATH = PROJECT_OPTION_XPATH + "/option[@name='PACKAGES_TO_USE_IMPORT_ON_DEMAND']/value";

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
    produce_a_component_containing_a_suboption_with_a_name_of_PACKAGES_TO_USE_IMPORT_ON_DEMAND() {
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
        xmlProducedBy(ImportLines.from(ImportLine.fromStatic("static.foo")))
            .shouldHavePackageXmlEquivalentTo(
                "<package name='static.foo' withSubpackages='true' static='true'/>"
            );
    }

    private String packageWithName(String name) {
        return PACKAGE_OPTION_XPATH + "/package[@name='" + name +"'][@withSubpackages='true'][@static='false']";
    }

    private static class XmlProducedBy {
        private final Source xmlDocument;

        public XmlProducedBy(ImportLines importLines) {
            XmlFileContentMerger xmlFileContentMerger = new XmlFileContentMerger(new XmlTransformer());
            XmlWriter xmlWriter = new XmlWriter(xmlFileContentMerger);

            xmlWriter.writeXml(importLines);
            String result = xmlFileContentMerger.getXmlTransformer().transform("<project/>");
            System.out.println(result);

            this.xmlDocument = xml(result);
        }

        public XmlProducedBy shouldHaveXPath(String xPath) {
            assertThat(xmlDocument, hasXPath(xPath));
            return this;
        }

        public void shouldHavePackageXmlEquivalentTo(String... packageXmls) {
            String packageXml = Joiner.on("").join(packageXmls);
            assertThat(xmlDocument, hasXPath(PACKAGE_OPTION_XPATH, returningAnXmlNode(), equivalentTo(xml("<value>" + packageXml + "</value>"))));
        }
    }

    private XmlProducedBy xmlProducedBy(ImportLines importLines) {
        return new XmlProducedBy(importLines);
    }
}
