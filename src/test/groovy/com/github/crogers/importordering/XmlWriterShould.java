package com.github.crogers.importordering;

import org.apache.tools.ant.util.ReaderInputStream;
import org.gradle.internal.xml.XmlTransformer;
import org.gradle.plugins.ide.api.XmlFileContentMerger;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

import static com.github.crogers.importordering.ImportLines.importLines;
import static com.github.crogers.importordering.ImportLines.noImportLines;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasXPath;

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
    produce_two_packages_entires_from_two_import_lines() {
        xmlProducedBy(importLines("foo.bar", "baz.quux"))
                .shouldHaveXPath(packageWithName("foo.bar"))
                .shouldHaveXPath(packageWithName("baz.quux"));
    }

    private String packageWithName(String name) {
        return PACKAGE_OPTION_XPATH + "/package[@name='" + name +"'][@withSubpackages='false'][@static='false']";
    }

    private static class XmlProducedBy {
        private final Document xmlDocument;

        public XmlProducedBy(ImportLines importLines) {
            XmlFileContentMerger xmlFileContentMerger = new XmlFileContentMerger(new XmlTransformer());
            XmlWriter xmlWriter = new XmlWriter(xmlFileContentMerger);

            xmlWriter.writeXml(importLines);
            String result = xmlFileContentMerger.getXmlTransformer().transform("<project/>");
            System.out.println(result);

            try {
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                this.xmlDocument = documentBuilder.parse(new ReaderInputStream(new StringReader(result)));
            } catch (ParserConfigurationException | SAXException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        public XmlProducedBy shouldHaveXPath(String xPath) {
            assertThat(xmlDocument, hasXPath(xPath));
            return this;
        }
    }

    private XmlProducedBy xmlProducedBy(ImportLines importLines) {
        return new XmlProducedBy(importLines);
    }
}
