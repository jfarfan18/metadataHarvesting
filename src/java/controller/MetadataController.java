package controller;

import JFlex.gui.MainFrame;
import static com.hp.hpl.jena.assembler.JA.OntModel;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import modelo.Repositorio;
import modelo.User;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.primefaces.event.FlowEvent;

@Named("metadataController")
@SessionScoped
public class MetadataController implements Serializable {

    @EJB
    private negocio.RepositorioFacade ejbFacadeRepositorio;
    private List<Repositorio> itemsRepositorio;
    private List<String> itemsGrupo;
    private List<String> itemsLO;
    private Repositorio selectedRepositorio;
    private String selectedGrupo;
    private String selectedLO;
    private String selectedMetadatos;

    private User user = new User();

    private boolean skip;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void save() {
        FacesMessage msg = new FacesMessage("Successful", "Welcome :" + user.getFirstname());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public String onFlowProcess(FlowEvent event) {
        if (skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        } else if (event.getOldStep().equals("repositorio") && event.getNewStep().equals("grupo")) {
            try {
                this.cargarGrupos();
            } catch (JDOMException ex) {
                Logger.getLogger(MetadataController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MetadataController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "grupo";
        } else if (event.getOldStep().equals("grupo") && event.getNewStep().equals("objeto")) {
            try {
                this.cargarRecords();
            } catch (JDOMException ex) {
                Logger.getLogger(MetadataController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MetadataController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "objeto";
        } else if (event.getOldStep().equals("objeto") && event.getNewStep().equals("metadatos")) {
            try {
                this.cargarMetadatos();
            } catch (JDOMException ex) {
                Logger.getLogger(MetadataController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MetadataController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "metadatos";
        } else {
            return event.getNewStep();
        }
    }

    private void cargarGrupos() throws JDOMException, IOException {
        String salida = callURL(this.getSelectedRepositorio().getUrlBase() + "?verb=ListSets");
        System.out.println("\nOutput: \n" + salida);

        this.itemsGrupo = new ArrayList<>();
        SAXBuilder builder = new SAXBuilder();
        InputStream in = new ByteArrayInputStream(salida.getBytes());
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("archivo.xml"), "utf-8"));
        writer.write(salida);
        writer.close();
        File xmlFile = new File("archivo.xml");

        Document document = (Document) builder.build(xmlFile);
        Element rootNode = document.getRootElement();

        List list = getElememnts(rootNode, "ListSets");
        for (int i = 0; i < list.size(); i++) {
            Element tabla = (Element) list.get(i);
            List lista_campos = getElememnts(tabla, "set");
            for (int j = 0; j < lista_campos.size(); j++) {
                Element campo = (Element) lista_campos.get(j);
                String nombre = getElememnts(campo, "setSpec").get(0).getValue();
                System.out.println("Nombre:\t" + nombre);
                itemsGrupo.add(nombre);
            }

        }
    }

    private void cargarRecords() throws JDOMException, IOException {
        String salida = callURL(this.getSelectedRepositorio().getUrlBase() + "?verb=ListRecords&metadataPrefix=oai_dc&set=" + this.selectedGrupo);
        System.out.println("\nOutput: \n" + salida);

        this.itemsLO = new ArrayList<>();
        SAXBuilder builder = new SAXBuilder();
        InputStream in = new ByteArrayInputStream(salida.getBytes());
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("archivo.xml"), "utf-8"));
        writer.write(salida);
        writer.close();
        File xmlFile = new File("archivo.xml");

        Document document = (Document) builder.build(xmlFile);
        Element rootNode = document.getRootElement();

        List list = getElememnts(rootNode, "ListRecords");
        for (int i = 0; i < list.size(); i++) {
            Element tabla = (Element) list.get(i);
            List lista_campos = getElememnts(tabla, "record");
            for (int j = 0; j < lista_campos.size(); j++) {
                Element campo = (Element) lista_campos.get(j);
                List lista_cabecera = getElememnts(campo, "header");
                for (int k = 0; k < lista_cabecera.size(); k++) {
                    Element ident = (Element) lista_cabecera.get(k);
                    String nombre = getElememnts(ident, "identifier").get(0).getValue();
                    System.out.println("Nombre:\t" + nombre);
                    itemsLO.add(nombre);
                }
            }

        }
    }

    private void cargarMetadatos() throws JDOMException, IOException {

        String salida = callURL(this.getSelectedRepositorio().getUrlBase() + "?verb=GetRecord&metadataPrefix=oai_dc&identifier=" + this.selectedLO);
        System.out.println("\nOutput: \n" + salida);

        SAXBuilder builder = new SAXBuilder();
        InputStream in = new ByteArrayInputStream(salida.getBytes());
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("archivo.xml"), "utf-8"));
        writer.write(salida);
        writer.close();
        File xmlFile = new File("archivo.xml");

        Document document = (Document) builder.build(xmlFile);
        Element rootNode = document.getRootElement();

        List list = getElememnts(rootNode, "GetRecord");

        OntModel modelPROV = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        modelPROV.read("file:///Users/Juanito/Documents/NetBeansProjects/GitHub/MetadatosDC/ontologias/prov.owl", "RDF/XML");
        OntModel modelFOAF = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        modelFOAF.read("file:///Users/Juanito/Documents/NetBeansProjects/GitHub/MetadatosDC/ontologias/foaf.owl", "");
        OntModel modelPROV_FOAF = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        modelPROV_FOAF.read("file:///Users/Juanito/Documents/NetBeansProjects/GitHub/MetadatosDC/ontologias/prov_foaf - Copy2.rdf", "");
//        OntModel modelPROV = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
//        modelPROV.read("file:///Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov.owl", "");
//        OntModel modelFOAF = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
//        modelFOAF.read("file:///Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/foaf.owl", "");
//        OntModel modelPROV_FOAF = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
//        modelPROV_FOAF.read("file:///Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov_foaf - Copy2.rdf", "");
//
//-----------------------Ontologia PROV------------------------------------------
        Iterator iterProv = modelPROV.listOntologies();
        Ontology ontoprov = (Ontology) iterProv.next();
        String ontologyURIProv = ontoprov.getURI();
        System.out.println("URIIIIIIIIIIII  Prov: " + ontologyURIProv);
//------------------------------------------------------------------------------        
//-----------------------Ontologia FOAF------------------------------------------
        Iterator iterfoaf = modelFOAF.listOntologies();
        Ontology ontofoaf = (Ontology) iterfoaf.next();
        String ontologyURIfoaf = ontofoaf.getURI();
        System.out.println("URIIIIIIIIIIII  FOAF: " + ontologyURIfoaf);
//------------------------------------------------------------------------------        
        /**
         * (URI y Namespace) de la ontologia
         */
        String ontologyURIProvFoaf = null;
        String ontologyNsProvFoaf = null;
        Iterator iterProvFoaf = modelPROV_FOAF.listOntologies();
        if (iterProvFoaf.hasNext()) {
            Ontology onto = (Ontology) iterProvFoaf.next();
            ontologyURIProvFoaf = onto.getURI();
            ontologyNsProvFoaf = onto.getNameSpace();
            System.out.println("PROV_ URI: " + ontologyURIProvFoaf);
            System.out.println("PROV_ Ns:  " + ontologyNsProvFoaf);
        }
//------------------------------------------------------------------------------
        for (int i = 0; i < list.size(); i++) {
            Element tabla = (Element) list.get(i);
            List lista_campos = getElememnts(tabla, "record");
            for (int j = 0; j < lista_campos.size(); j++) {
                Element campo = (Element) lista_campos.get(j);
                List lista_cabecera = getElememnts(campo, "metadata");
                for (int k = 0; k < lista_cabecera.size(); k++) {
                    Element ident = (Element) lista_cabecera.get(k);
                    System.out.println("element1: " + ident);
                    List lista_1 = getElememnts(ident, "dc");
                    System.out.println("-------IDENT" + ident.getChildren().get(k).getName());

                    for (int k1 = 0; k1 < lista_1.size(); k1++) {
                        Element identk1 = (Element) lista_1.get(k1);
                        String title = getElememnts(identk1, "title").get(k1).getValue();
                        System.out.println("title");
                        System.out.println(title);
                        String creator = getElememnts(identk1, "creator").get(k1).getValue();
                        System.out.println("creator");
                        System.out.println(creator);
                        String subject = getElememnts(identk1, "subject").get(k1).getValue();
                        System.out.println("subject");
                        System.out.println(subject);
                        String description = getElememnts(identk1, "description").get(k1).getValue();
                        System.out.println("description");
                        System.out.println(description);
                        String publisher = getElememnts(identk1, "publisher").get(k1).getValue();
                        System.out.println("publisher");
                        System.out.println(publisher);
//                        String contributor = getElememnts(identk1, "contributor").get(k1).getValue(); 
//                        System.out.println("contributor");
//                        System.out.println(contributor);
                        String date = getElememnts(identk1, "date").get(k1).getValue();
                        System.out.println("date");
                        System.out.println(date);
//                        String type = getElememnts(identk1, "type").get(k1).getValue();
//                        System.out.println("type");
//                        System.out.println(type);
//                        String format = getElememnts(identk1, "format").get(k1).getValue();
//                        System.out.println("format");
//                        System.out.println(format);
                        String identifier = getElememnts(identk1, "identifier").get(k1).getValue();
                        System.out.println("identifier");
                        System.out.println(identifier);
//                        String source = getElememnts(identk1, "source").get(k1).getValue();
//                        System.out.println("source");
//                        System.out.println(source);
                        String language = getElememnts(identk1, "language").get(k1).getValue();
                        System.out.println("language");
                        System.out.println(language);
//                        String relation = getElememnts(identk1, "relation").get(k1).getValue();
//                        System.out.println("relation");
//                        System.out.println(relation);
//                        String coverage = getElememnts(identk1, "coverage").get(k1).getValue();
//                        System.out.println("coverage");
//                        System.out.println(coverage);
//                        String rights = getElememnts(identk1, "rights").get(k1).getValue();
//                        System.out.println("rights");
//                        System.out.println(rights);
////--------------------------------------------------------------------------------------------------                        
                        /**
                         * Se crea el individuo principal OBJETO DE APRENDIZAJE
                         * con los dataproperties correspondientes
                         */
                        OntClass claseRecuperada1 = modelPROV_FOAF.getOntClass(ontologyURIProv + "Entity");
                        System.out.println("CLASE RECUPERADA: " + claseRecuperada1);
                        Individual OBJETOAPRENDIZAJE = modelPROV_FOAF.createIndividual(ontologyURIProv + "OBJETOAPRENDIZAJE" + "_ID:" + identifier, claseRecuperada1);
                        //propiedad titulo
                        DatatypeProperty propiedadTitle = modelPROV_FOAF.getDatatypeProperty(ontologyURIfoaf + "title");
                        OBJETOAPRENDIZAJE.setPropertyValue(propiedadTitle, modelPROV_FOAF.createTypedLiteral(title));
                        //propiedad relation
                        DatatypeProperty propiedadRelation = modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf + "#relation");
                        OBJETOAPRENDIZAJE.setPropertyValue(propiedadRelation, modelPROV_FOAF.createTypedLiteral("relation_N/E"));
                        //propiedad description
                        DatatypeProperty propiedadDescription = modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf + "#description");
                        OBJETOAPRENDIZAJE.setPropertyValue(propiedadDescription, modelPROV_FOAF.createTypedLiteral(description));
                        //propiedad identifier
                        DatatypeProperty propiedadIdentifier = modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf + "#identifier");
                        OBJETOAPRENDIZAJE.setPropertyValue(propiedadIdentifier, modelPROV_FOAF.createTypedLiteral(identifier));
                        //propiedad rights
                        DatatypeProperty propiedadRights = modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf + "#rights");
                        OBJETOAPRENDIZAJE.setPropertyValue(propiedadRights, modelPROV_FOAF.createTypedLiteral("rights_N/E"));
                        //propiedad subject
                        DatatypeProperty propiedadSubject = modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf + "#subject");
                        OBJETOAPRENDIZAJE.setPropertyValue(propiedadSubject, modelPROV_FOAF.createTypedLiteral(subject));
                        //propiedad type
                        DatatypeProperty propiedadType = modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf + "#type");
                        OBJETOAPRENDIZAJE.setPropertyValue(propiedadType, modelPROV_FOAF.createTypedLiteral("type_N/E"));
                        //propiedad language
                        DatatypeProperty propiedadLanguage = modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf + "#language");
                        OBJETOAPRENDIZAJE.setPropertyValue(propiedadLanguage, modelPROV_FOAF.createTypedLiteral(language));
                        //propiedad date
                        DatatypeProperty propiedadDate = modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf + "#date");
                        OBJETOAPRENDIZAJE.setPropertyValue(propiedadDate, modelPROV_FOAF.createTypedLiteral(date));
                        //propiedad coverage
                        DatatypeProperty propiedadCoverage = modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf + "#coverage");
                        OBJETOAPRENDIZAJE.setPropertyValue(propiedadCoverage, modelPROV_FOAF.createTypedLiteral("coverage_N/E"));

                        /**
                         * Se crean los individuos secundarios para
                         * relacionarlos con el OBJETO DE APRENDIZAJE
                         */
                        OntClass claseRecuperadaContributor = modelPROV_FOAF.getOntClass(ontologyURIfoaf + "Agent");
                        Individual OBJETOAPRENDIZAJE_CONTRIBUTOR = modelPROV_FOAF.createIndividual(ontologyURIProv + "OBJETOAPRENDIZAJE_CONTRIBUTOR" + "_ID:" + identifier, claseRecuperadaContributor);
                        //propiedad name 
                        DatatypeProperty propiedadNameContributor = modelFOAF.getDatatypeProperty(ontologyURIfoaf + "firstName");
                        OBJETOAPRENDIZAJE_CONTRIBUTOR.setPropertyValue(propiedadNameContributor, modelPROV_FOAF.createTypedLiteral("contributor_N/E"));

                        //Agrega object property contributor al OBJETO DE APRENDIZAJE
                        Property propiedadObjectPropertyContributor = modelPROV.getProperty(ontologyURIProv + "contributor");
                        OBJETOAPRENDIZAJE.addProperty(propiedadObjectPropertyContributor, OBJETOAPRENDIZAJE_CONTRIBUTOR);

                        OntClass claseRecuperadaCreator = modelPROV_FOAF.getOntClass(ontologyURIfoaf + "Agent");
                        Individual OBJETOAPRENDIZAJE_CREATOR = modelPROV_FOAF.createIndividual(ontologyURIProv + "OBJETOAPRENDIZAJE_CREATOR" + "_ID:" + identifier, claseRecuperadaCreator);
                        //propiedad name 
                        DatatypeProperty propiedadNameCreator = modelFOAF.getDatatypeProperty(ontologyURIfoaf + "firstName");
                        OBJETOAPRENDIZAJE_CREATOR.setPropertyValue(propiedadNameCreator, modelPROV_FOAF.createTypedLiteral(creator));

                        //Agrega object property creator al OBJETO DE APRENDIZAJE
                        Property propiedad_ObjectPropertyCreator = modelPROV.getProperty(ontologyURIProv + "creator");
                        OBJETOAPRENDIZAJE.addProperty(propiedad_ObjectPropertyCreator, OBJETOAPRENDIZAJE_CREATOR);

                        OntClass claseRecuperadaPublisher = modelPROV_FOAF.getOntClass(ontologyURIfoaf + "Agent");
                        Individual OBJETOAPRENDIZAJE_PUBLISHER = modelPROV_FOAF.createIndividual(ontologyURIProv + "OBJETOAPRENDIZAJE_PUBLISHER" + "_ID:" + identifier, claseRecuperadaPublisher);
                        //propiedad name 
                        DatatypeProperty propiedadNamePublisher = modelFOAF.getDatatypeProperty(ontologyURIfoaf + "firstName");
                        OBJETOAPRENDIZAJE_PUBLISHER.setPropertyValue(propiedadNamePublisher, modelPROV_FOAF.createTypedLiteral(publisher));

                        //Agrega object property creator al OBJETO DE APRENDIZAJE
                        Property propiedad_ObjectPropertyPublisher = modelPROV.getProperty(ontologyURIProv + "publisher");
                        OBJETOAPRENDIZAJE.addProperty(propiedad_ObjectPropertyPublisher, OBJETOAPRENDIZAJE_PUBLISHER);

                        OntClass claseRecuperadaFormat = modelPROV_FOAF.getOntClass(ontologyURIProv + "PhysicalResource");
                        Individual OBJETOAPRENDIZAJE_HASFORMAT = modelPROV_FOAF.createIndividual(ontologyURIProv + "OBJETOAPRENDIZAJE_HASFORMAT" + "_ID:" + identifier, claseRecuperadaFormat);
                        //propiedad format
                        DatatypeProperty propiedadFormat = modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf + "#format");
                        OBJETOAPRENDIZAJE_HASFORMAT.setPropertyValue(propiedadFormat, modelPROV_FOAF.createTypedLiteral("format_N/E"));

                        //Agrega object property creator al OBJETO DE APRENDIZAJE
                        Property propiedad_ObjectPropertyHasFormat = modelPROV.getProperty(ontologyURIProv + "hasformat");
                        OBJETOAPRENDIZAJE.addProperty(propiedad_ObjectPropertyHasFormat, OBJETOAPRENDIZAJE_HASFORMAT);

//                        OntClass claseRecuperadaSource = modelPROV_FOAF.getOntClass(ontologyURIfoaf + "Agent");
//                        Individual OBJETOAPRENDIZAJE_HASFORMAT=  modelPROV_FOAF.createIndividual(ontologyURIProv + "OBJETOAPRENDIZAJE_HASFORMAT", claseRecuperadaFormat);
//                        //propiedad format
//                        DatatypeProperty propiedadFormat = modelPROV.getDatatypeProperty(ontologyURIProv + "format");
//                        OBJETOAPRENDIZAJE_HASFORMAT.setPropertyValue(propiedadFormat, modelPROV_FOAF.createTypedLiteral("format_N/E"));
                        File filePropiedades = new File("/Users/Juanito/Documents/NetBeansProjects/GitHub/MetadatosDC/ontologias/prov_foaf - Copy2.rdf");

//Captura de excepciones
                        if (!filePropiedades.exists()) {
                            System.out.println("no existe");
                            try {
                                filePropiedades.createNewFile();
                            } catch (IOException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        try {
                            System.out.println("existe");

                            modelPROV_FOAF.write(new PrintWriter(filePropiedades));
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        /////AQUI DEBES TOMAR LOS VALORES DE LOS CAMPOS QUE ESTAN DENTRO DE oai_dc:dc
                        //Fijate en el nombre q tiene el campo puedes ver usando un for como el de arriba si no te coje de una
                        //lo que debes hacer es agregar un for mas para ver cada campo del DC asi como proceso antes los otros campos
                        //
//                        this.selectedMetadatos = nombre;
//                    for (int ii = 0; ii < lista_1.size(); ii++) {
//                        System.out.println("-------NOMBRE " + lista_1.get(ii));
//                    }
//                    for (int g = 0; g < ident.getChildren().size(); g++) {
//                        System.out.println("-------NOMBRE " + ident.getChildren().get(g).getName());
//                        System.out.println("-------PREFIJO " + ident.getChildren().get(g).getNamespacePrefix());
//                        System.out.println("-------URI " + ident.getChildren().get(g).getNamespaceURI());
//                        System.out.println("-------VALUE " + ident.getChildren().get(g).getValue());
//                        System.out.println("-------Q " + ident.getChildren().get(g).getQualifiedName());
//                        System.out.println("-------" + ident.getChildren().get(g).getChildren());
//
//                    }
                    }
                }
            }//fin de for
        }//fin del for
    }

    private static List<Element> getElememnts(Element raiz, String nombre) {
        List<Element> lista = new ArrayList();
        for (int l = 0; l < raiz.getChildren().size(); l++) {
            if (raiz.getChildren().get(l).getName().equals(nombre)) {
                lista.add(raiz.getChildren().get(l));
            }
        }
        return lista;
    }

    public static String callURL(String myURL) {
        System.out.println("Requeted URL:" + myURL);
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            URL url = new URL(myURL);
            urlConn = url.openConnection();
            if (urlConn != null) {
                urlConn.setReadTimeout(60 * 1000);
            }
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(),
                        Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
            }
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Exception while calling URL:" + myURL, e);
        }

        return sb.toString();
    }

    /**
     * @return the selectedRepositorio
     */
    public Repositorio getSelectedRepositorio() {
        return selectedRepositorio;
    }

    /**
     * @param selectedRepositorio the selectedRepositorio to set
     */
    public void setSelectedRepositorio(Repositorio selectedRepositorio) {
        this.selectedRepositorio = selectedRepositorio;
    }

    /**
     * @return the itemsRepositorio
     */
    public List<Repositorio> getItemsRepositorio() {
        itemsRepositorio = ejbFacadeRepositorio.findAll();
        return itemsRepositorio;
    }

    /**
     * @param itemsRepositorio the itemsRepositorio to set
     */
    public void setItemsRepositorio(List<Repositorio> itemsRepositorio) {
        this.itemsRepositorio = itemsRepositorio;
    }

    /**
     * @return the itemsGrupo
     */
    public List<String> getItemsGrupo() {
        return itemsGrupo;
    }

    /**
     * @param itemsGrupo the itemsGrupo to set
     */
    public void setItemsGrupo(List<String> itemsGrupo) {
        this.itemsGrupo = itemsGrupo;
    }

    /**
     * @return the selectedGrupo
     */
    public String getSelectedGrupo() {
        return selectedGrupo;
    }

    /**
     * @param selectedGrupo the selectedGrupo to set
     */
    public void setSelectedGrupo(String selectedGrupo) {
        this.selectedGrupo = selectedGrupo;
    }

    /**
     * @return the itemsLO
     */
    public List<String> getItemsLO() {
        return itemsLO;
    }

    /**
     * @param itemsLO the itemsLO to set
     */
    public void setItemsLO(List<String> itemsLO) {
        this.itemsLO = itemsLO;
    }

    /**
     * @return the selectedLO
     */
    public String getSelectedLO() {
        return selectedLO;
    }

    /**
     * @param selectedLO the selectedLO to set
     */
    public void setSelectedLO(String selectedLO) {
        this.selectedLO = selectedLO;
    }

    /**
     * @return the selectedMetadatos
     */
    public String getSelectedMetadatos() {
        return selectedMetadatos;
    }

    /**
     * @param selectedMetadatos the selectedMetadatos to set
     */
    public void setSelectedMetadatos(String selectedMetadatos) {
        this.selectedMetadatos = selectedMetadatos;
    }

}
