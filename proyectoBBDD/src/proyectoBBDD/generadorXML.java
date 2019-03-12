package proyectoBBDD;

import java.awt.Dialog.ModalExclusionType;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import javax.print.attribute.standard.RequestingUserName;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
/*
 * Bienvenido a nuestro programa de inserccion de datos basicos de empleados,
 * Aqui podras añadir, eliminar, modificar y buscar tus empleados. Los datos
 * que podras añadir sera DNI que se usara como clave primaria y apellido.
 * Ivan, Luis. 12/03/2019 IesJoanD'austria
 * 
 * */
public class generadorXML {
	final public static String nombre_archivo = "ASD";

	/*Las funciones de pregunta sirven para agilizar las respuestas del usuario*/
	
	public static int preguntaInt(String pregunta) throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println(pregunta);
			int mod = Integer.parseInt(reader.readLine());
			return mod;
		} catch (Exception e) {
			System.out.println("Error");
			preguntaString(pregunta);
		}
		return 0;
	}

	public static String preguntaString(String pregunta) throws NumberFormatException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println(pregunta);
			String mod = reader.readLine();
			return mod;
		} catch (Exception e) {
			System.out.println("Error");
			preguntaString(pregunta);
		}
		return pregunta;
	}

	public static void preguntas(String... args) throws NumberFormatException, IOException {
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
	}

	/*La funcion eliminarXML mediante el dni borra el registro entero de una persona*/
	public static void eliminarXML(String dni, Document document, Source source) throws TransformerFactoryConfigurationError, TransformerException {
		NodeList items = document.getElementsByTagName("dni");
		System.out.println(items.getLength());
		
		
		for (int ix = 0; ix < items.getLength(); ix++) {
			Element element = (Element) items.item(ix);
			System.out.println(element.getTextContent());
			if(element.getTextContent().equals(dni)) {
				element.getParentNode().getParentNode().removeChild(element.getParentNode());
			}
			
		}

		Result result = new StreamResult(new java.io.File("src/proyectoBBDD/" + nombre_archivo + ".xml"));
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(source, result);

	}
	
	/*La funcion modificarXML mediante el dni busca el registro te pregunta que quiere modificar si dni o apellido y posteriomente borra el registro 'antigo' y crea uno nuevo con los datos actualizados*/
	public static void modificarXML(String dni, Document document, Source source) throws TransformerFactoryConfigurationError, TransformerException, NumberFormatException, IOException {
		
		NodeList items = document.getElementsByTagName("dni");
		System.out.println("Entra en modificar");
		
		for (int ix = 0; ix < items.getLength(); ix++) {
			Element element = (Element) items.item(ix);
			System.out.println("for");
			if(element.getTextContent().equals(dni)) {
				String apellido = element.getParentNode().getLastChild().getTextContent();
				element.getParentNode().getParentNode().removeChild(element.getParentNode());	
				System.out.println(apellido);
				preguntas("1 - Cambiar DNI" , "2 - Cambiar Apellido");
				
				String texto = preguntaString("Que quieres cambiar?");
				System.out.println(texto);
				
				String dniNuevo, apellidoNuevo;
				if (texto.equals("1")) {
					dniNuevo = preguntaString("DNI");
					insertarXML(dniNuevo,apellido,document,source);
				}else if(texto.equals("2")) {
					apellidoNuevo = preguntaString("Apellido");
					insertarXML(dni,apellidoNuevo,document,source);
				}else {
					System.out.println("Error");
				}
				
				
				
			}
			
		}

		Result result = new StreamResult(new java.io.File("src/proyectoBBDD/" + nombre_archivo + ".xml"));
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(source, result);

	}
	
	/*Mediante el dni busca la persona en el archivo xml*/
	public static void buscarRegistro(String dni, Document document, Source source) { 
		NodeList items = document.getElementsByTagName("dni");
		for (int ix = 0; ix < items.getLength(); ix++) {
			Element element = (Element) items.item(ix);
			if (element.getTextContent().equals(dni)) {
				String dniPersona = element.getParentNode().getFirstChild().getTextContent();
				String apellido = element.getParentNode().getLastChild().getTextContent();
				
				System.out.println("Dni: " + dniPersona + "\nApellido: " + apellido);
				
			}
		}
	}
	
	/*Esta funcionsirve para comprobar si el registro exite en caso de que si devolvera booleano, la utilizamos al insertar para que no insertes una persona con el mismo dni dos veces*/
	public static boolean comprobarRegistro(String dni, Document document, Source source) { 
		NodeList items = document.getElementsByTagName("dni");
		for (int ix = 0; ix < items.getLength(); ix++) {
			Element element = (Element) items.item(ix);
			if (element.getTextContent().equals(dni)) {
				return true;
			}
		}
		return false;
	}

	/*Te pregunta dni y nombre e inserta la persona en el registro*/
	public static void insertarXML(String dni, String nombre, Document document, Source source) throws TransformerException {
		
		if(comprobarRegistro(dni, document, source)) {
			System.out.println("El registro ya existe");
		}else {
			// Main Node
			Element raiz = document.getDocumentElement();

			// Item Node
			Element itemNode = document.createElement("persona");

			// Key Node
			Element keyNode = document.createElement("dni");
			Text nodeKeyValue = document.createTextNode(dni);
			keyNode.appendChild(nodeKeyValue);

			// Value Node
			Element valueNode = document.createElement("apellido");
			Text nodeValueValue = document.createTextNode(nombre);
			valueNode.appendChild(nodeValueValue);

			// append keyNode and valueNode to itemNode
			itemNode.appendChild(keyNode);
			itemNode.appendChild(valueNode);

			// append itemNode to raiz
			raiz.appendChild(itemNode);
			
			// Indicamos donde lo queremos almacenar
			Result result = new StreamResult(new java.io.File("src/proyectoBBDD/" + nombre_archivo + ".xml"));
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(source, result);
		}
		
	}

	public static void main(String[] args) throws Exception {
		try {
			boolean a = true;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation implementation = builder.getDOMImplementation();
			Document document = implementation.createDocument(null,  nombre_archivo , null);
			document.setXmlVersion("1.0");
			// Generate XML
			Source source = new DOMSource(document);
			// Indicamos donde lo queremos almacenar
			Result result = new StreamResult(new java.io.File("src/proyectoBBDD/" + nombre_archivo + ".xml"));
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(source, result);
			
			
			while (a == true) {
				preguntas("1 - Insertar Registro", "2 - Borrar Registro", "3 - Buscar Registro", "4 - Modificar Registro", "5 - Salir del Programa");
				int mod = preguntaInt("Que quieres hacer: ");

				switch (mod) {

				case 1:
					String nombre = preguntaString("Apellido");
					String dni = preguntaString("Dni");

					insertarXML(dni, nombre, document,source);
					break;
				case 2:
					dni = preguntaString("Dni");
					eliminarXML(dni, document, source);
					break;
				case 3:
					dni = preguntaString("Dni");
					buscarRegistro(dni, document, source);
					break;
				case 4:
					dni = preguntaString("Dni");
					modificarXML(dni, document, source);
					break;
				case 5:
					preguntas("Se acabó, chao");
					a = false;
					break;
				default:
					preguntas("ERROR");
					break;
				}
			}

			

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}