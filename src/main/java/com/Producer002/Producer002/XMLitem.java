package com.Producer002.Producer002;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLitem {
	
	private String sender;
	private String destination;
	private String server;
	private String content;
	
	public XMLitem(File x) throws IOException {
		super();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		//reads file
		this.content = FileUtils.readFileToString(x, Charset.defaultCharset());
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document;
			try {
				document = db.parse(x);
				document.getDocumentElement().normalize();  
				NodeList nodeList = document.getChildNodes();
				{
					for(int i=0;i<nodeList.getLength();i++) {
						Node theNode = nodeList.item(i);
						if(theNode.getNodeType() == Node.ELEMENT_NODE) {
							Element nodeElement = (Element) theNode;
							//header elements
							this.sender = nodeElement.getElementsByTagName("sender")
                                    .item(0).getChildNodes().item(0).getNodeValue();
							this.destination = nodeElement.getElementsByTagName("destination")
                                    .item(0).getChildNodes().item(0).getNodeValue();
							this.server = nodeElement.getElementsByTagName("server")
                                    .item(0).getChildNodes().item(0).getNodeValue();
						}
					}
				}
			} catch (SAXException e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			//e.printStackTrace();
			System.out.println("Hello this");
		}
	}

	//getter for sender
	public String getSender() {
		return sender;
	}

	//setter for sender
	public void setSender(String sender) {
		this.sender = sender;
	}

	//getter for destination
	public String getDestination() {
		return destination;
	}

	//setter for destination
	public void setDestination(String destination) {
		this.destination = destination;
	}

	//getter for server
	public String getServer() {
		return server;
	}

	//setter for server
	public void setServer(String server) {
		this.server = server;
	}

	//getter for content
	public String getContent() {
		return content;
	}

	//setter for content
	public void setContent(String content) {
		this.content = content;
	}

	
	
}
