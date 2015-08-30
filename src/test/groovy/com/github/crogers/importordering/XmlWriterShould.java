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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasXPath;

public class XmlWriterShould {
    private static final String COMPONENT_XPATH = "/project/component[@name='ProjectCodeStyleSettingsManager']";
    private static final String OPTION_XPATH = COMPONENT_XPATH + "/option[@name='PER_PROJECT_SETTINGS']/value";

    private final XmlFileContentMerger xmlFileContentMerger = new XmlFileContentMerger(new XmlTransformer());
    private final XmlWriter xmlWriter = new XmlWriter(xmlFileContentMerger);

    @Test public void
    produce_a_component_with_a_name_of_ProjectCodeStyleSettingsManager() throws ParserConfigurationException, IOException, SAXException {
        assertThatXmlHasXPath(COMPONENT_XPATH);
    }

    @Test public void
    produce_a_component_containing_an_option_with_a_name_of_PER_PROJECT_SETTINGS() {
        assertThatXmlHasXPath(OPTION_XPATH);
    }

    @Test public void
    produce_a_component_containing_a_suboption_with_a_name_of_PACKAGES_TO_USE_IMPORT_ON_DEMAND() {
        assertThatXmlHasXPath(OPTION_XPATH + "/option[@name='PACKAGES_TO_USE_IMPORT_ON_DEMAND']/value");
    }

    private void assertThatXmlHasXPath(String xPath) {
        xmlWriter.writeXml();
        String result = xmlFileContentMerger.getXmlTransformer().transform("<project/>");

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = documentBuilder.parse(new ReaderInputStream(new StringReader(result)));

            System.out.println(result);
            assertThat(doc, hasXPath(xPath));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
