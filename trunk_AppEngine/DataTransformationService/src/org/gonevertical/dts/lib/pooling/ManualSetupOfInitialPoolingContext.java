package org.gonevertical.dts.lib.pooling;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.file.MoveFileData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ManualSetupOfInitialPoolingContext {
	
  private static final Logger log = LoggerFactory.getLogger(ManualSetupOfInitialPoolingContext.class);
	
	private InitialContext initalContext = null;

	/**
	 * context.xml path - in servlet context format
	 */
	private String contextPath = null;
	
	/**
	 * instead of context.xml, use databaseData for the properties
	 */
  private DatabaseData databaseData;
  
  /**
   * setup DatabaseData pooling
   * @param tmpPath
   * @param databaseData
   */
  public static void registerContext(String tmpPath, DatabaseData databaseData) {
    new ManualSetupOfInitialPoolingContext(tmpPath, databaseData);
  }

	public ManualSetupOfInitialPoolingContext(String tmpPath) {
	  setup(tmpPath);
		run();
	}
	
	public ManualSetupOfInitialPoolingContext(String tmpPath, String contextXmlPath) {
    this.contextPath = contextXmlPath;
    setup(tmpPath);
    run();
  }
	
	public ManualSetupOfInitialPoolingContext(String tmpPath, DatabaseData databaseData) {
	  this.databaseData = databaseData;
    setup(tmpPath);
    run();
  }
	
	public void setup(String tmpPath) {
	  System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
	  if (tmpPath == null) {
	    tmpPath = "~/tmp";
	  }
    File d = new File(tmpPath + "/jdbc");
    d.mkdirs();
    System.setProperty(Context.PROVIDER_URL, "file://" + tmpPath + "");
    try {
      initalContext = new InitialContext();
    } catch (NamingException e) {
      e.printStackTrace();
    }
	}
	
	/**
	 * get the available resources via context.xml - same as tomcat resource xml
	 */
	private void run() {
		ResourceData[] rds = getResources();
		for (int i=0; i < rds.length; i++) {
			String name = rds[i].getName();
			try {
	      initalContext.rebind(name, rds[i].getReference());
      } catch (NamingException e) {
	      e.printStackTrace();
      }
		}
		// tell the databaseData that it has context to reference
		databaseData.setServletContext(initalContext, databaseData.getResourceName());
	}

	private ResourceData[] getResources() {
	  ResourceData[] rds = null;
	  if (contextPath != null) {
	    rds = readXmlFile();
	  } else {
	    rds = readDatabaseDataProperties();
	  }
	  return rds;
  }
	
	private ResourceData[] readDatabaseDataProperties() {
	  ResourceData[] rds = new ResourceData[1];
	  rds[0] = parseAttributes(databaseData);
    return rds;
  }

  private ResourceData[] readXmlFile() {
		ResourceData[] rds = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(contextPath);
			Element docEle = dom.getDocumentElement();
			//get a nodelist of  elements
			NodeList nl = docEle.getElementsByTagName("Resource");
			if (nl != null && nl.getLength() > 0) {
				rds = new ResourceData[nl.getLength()];
				for (int i = 0 ; i < nl.getLength(); i++) {
					rds[i] = new ResourceData();
					Element element = (Element) nl.item(i);
					rds[i] = parseAttributes(element);
				}
			}
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch(SAXException se) {
			se.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		return rds;
	}
	
	private ResourceData parseAttributes(Element element) {
		String resourceName = null;
		HashMap<String, String> hm = new HashMap<String, String>();
		NamedNodeMap at = element.getAttributes();
		for (int i=0; i < at.getLength(); i++) {
			Node n = at.item(i);
			String name = n.getNodeName();
			String value = n.getNodeValue();
			if (name.equals("name") == true) {
				resourceName = value;
			} else {
				hm.put(name, value);
			}
		}
		ResourceData rd = new ResourceData();
		rd.setName(resourceName);
		rd.setHashMap(hm);
		return rd;
	}
	
  private ResourceData parseAttributes(DatabaseData databaseData) {
    ResourceData rd = new ResourceData();
    rd.setName(databaseData.getResourceName());
    rd.setHashMap(databaseData.getProperties());
    return rd;
  }

	public Context getInitialContext() {
	  return initalContext;
  }


	
}
