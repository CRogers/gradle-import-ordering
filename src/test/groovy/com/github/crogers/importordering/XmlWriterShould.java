package com.github.crogers.importordering;

import org.apache.tools.ant.util.ReaderInputStream;
import org.gradle.internal.xml.XmlTransformer;
import org.gradle.plugins.ide.api.XmlFileContentMerger;
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
    private final XmlFileContentMerger xmlFileContentMerger = new XmlFileContentMerger(new XmlTransformer());
    private final XmlWriter xmlWriter = new XmlWriter(xmlFileContentMerger);

    @org.junit.Test public void
    produce_a_component_with_a_name_of_ProjectCodeStyleSettingsManager() throws ParserConfigurationException, IOException, SAXException {
        xmlWriter.writeXml();
        String result = xmlFileContentMerger.getXmlTransformer().transform("<project/>");

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = documentBuilder.parse(new ReaderInputStream(new StringReader(result)));

        assertThat(doc, hasXPath("/project/component[@name='ProjectCodeStyleSettingsManager']"));
    }
}
