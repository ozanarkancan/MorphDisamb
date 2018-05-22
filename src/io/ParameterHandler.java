package io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ParameterHandler {
	private String disambiguator;
	private ArrayList<String> trainParameters;
	private ArrayList<String> devParameters;
	private ArrayList<String> testParameters;
	private String trainFileName;
	private String devFileName;
	private String testFileName;
	private HashSet<String> svmStrategies;
	
	public void readParameters(String parameterFileName)
	{
		File parameterFile = new File(parameterFileName);
		try {
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(parameterFile);
			doc.getDocumentElement().normalize();
			NodeList configNode = doc.getElementsByTagName("configuration");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
