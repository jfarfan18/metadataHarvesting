/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import JFlex.gui.MainFrame;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Juanito
 */
public class CargarMetadatos {
    


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        

        OntModel modelPROV = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        modelPROV.read("file:///Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov.owl", "");
        OntModel modelFOAF = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        modelFOAF.read("file:///Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/foaf.owl", "");
        OntModel modelPROV_FOAF = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        modelPROV_FOAF.read("file:///Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov_foaf - Copy2.rdf", "");

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
//        /**
//         * (URI y Namespace) de la ontologia
//         */
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

        
        //Agregar propiedad title al individuo
        OntClass claseRecuperada = modelPROV_FOAF.getOntClass(ontologyURIProv + "Entity");
        DatatypeProperty propiedadTitle=modelPROV_FOAF.getDatatypeProperty(ontologyURIfoaf+"title");
        Individual individuoPropiedadTitle = modelPROV_FOAF.createIndividual(ontologyURIProv + "OA_Instanciado", claseRecuperada);
        individuoPropiedadTitle.setPropertyValue(propiedadTitle, modelPROV_FOAF.createTypedLiteral("TITULO DE PRUEBA"));
        File filePropiedades = new File("/Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov_foaf - Copy2.rdf");

        //Agregar propiedad relation al individuo
        DatatypeProperty propiedadRelation=modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf+"#relation");
        Individual individuoPropiedadRelation = modelPROV_FOAF.createIndividual(ontologyURIProv + "OA_Instanciado", claseRecuperada);
        individuoPropiedadRelation.setPropertyValue(propiedadRelation, modelPROV_FOAF.createTypedLiteral("RELACION DE PRUEBA"));
        filePropiedades = new File("/Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov_foaf - Copy2.rdf");
        
        //Agregar propiedad description al individuo
        DatatypeProperty propiedadDescription=modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf+"#description");
        Individual individuoPropiedadDescription = modelPROV_FOAF.createIndividual(ontologyURIProv + "OA_Instanciado", claseRecuperada);
        individuoPropiedadDescription.setPropertyValue(propiedadDescription, modelPROV_FOAF.createTypedLiteral("DESCRIPCION DE PRUEBA"));
        filePropiedades = new File("/Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov_foaf - Copy2.rdf");
        
        //Agregar propiedad identifier al individuo
        DatatypeProperty propiedadIdentifier=modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf+"#identifier");
        Individual individuoPropiedadIdentifier = modelPROV_FOAF.createIndividual(ontologyURIProv + "OA_Instanciado", claseRecuperada);
        individuoPropiedadIdentifier.setPropertyValue(propiedadIdentifier, modelPROV_FOAF.createTypedLiteral("IDENTIFIER DE PRUEBA"));
        filePropiedades = new File("/Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov_foaf - Copy2.rdf");
        
        //Agregar propiedad rigths al individuo
        DatatypeProperty propiedadRights=modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf+"#rights");
        Individual individuoPropiedadRights = modelPROV_FOAF.createIndividual(ontologyURIProv + "OA_Instanciado", claseRecuperada);
        individuoPropiedadRights.setPropertyValue(propiedadRights, modelPROV_FOAF.createTypedLiteral("RIGHTS DE PRUEBA"));
        filePropiedades = new File("/Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov_foaf - Copy2.rdf");
        
        //Agregar propiedad subject al individuo
        DatatypeProperty propiedadSubject=modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf+"#subject");
        Individual individuoPropiedadSubject = modelPROV_FOAF.createIndividual(ontologyURIProv + "OA_Instanciado", claseRecuperada);
        individuoPropiedadSubject.setPropertyValue(propiedadSubject, modelPROV_FOAF.createTypedLiteral("SUBJECT DE PRUEBA"));
        filePropiedades = new File("/Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov_foaf - Copy2.rdf");
        
        //Agregar propiedad type al individuo
        DatatypeProperty propiedadType=modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf+"#type");
        Individual individuoPropiedadType = modelPROV_FOAF.createIndividual(ontologyURIProv + "OA_Instanciado", claseRecuperada);
        individuoPropiedadType.setPropertyValue(propiedadType, modelPROV_FOAF.createTypedLiteral("TYPE DE PRUEBA"));
        filePropiedades = new File("/Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov_foaf - Copy2.rdf");
        
        //Agregar propiedad language al individuo
        DatatypeProperty propiedadLanguage=modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf+"#language");
        Individual individuoPropiedadLanguage = modelPROV_FOAF.createIndividual(ontologyURIProv + "OA_Instanciado", claseRecuperada);
        individuoPropiedadLanguage.setPropertyValue(propiedadLanguage, modelPROV_FOAF.createTypedLiteral("LANGUAGE DE PRUEBA"));
        filePropiedades = new File("/Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov_foaf - Copy2.rdf");
        
        //Agregar propiedad date al individuo
        DatatypeProperty propiedadDate=modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf+"#date");
        Individual individuoPropiedadDate = modelPROV_FOAF.createIndividual(ontologyURIProv + "OA_Instanciado", claseRecuperada);
        individuoPropiedadDate.setPropertyValue(propiedadDate, modelPROV_FOAF.createTypedLiteral("DATE DE PRUEBA"));
        filePropiedades = new File("/Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov_foaf - Copy2.rdf");
        
        //Agregar propiedad coverage al individuo
        DatatypeProperty propiedadCoverage=modelPROV_FOAF.getDatatypeProperty(ontologyURIProvFoaf+"#coverage");
        Individual individuoPropiedadCoverage = modelPROV_FOAF.createIndividual(ontologyURIProv + "OA_Instanciado", claseRecuperada);
        individuoPropiedadCoverage.setPropertyValue(propiedadCoverage, modelPROV_FOAF.createTypedLiteral("COVERAGE DE PRUEBA"));
        filePropiedades = new File("/Users/Juanito/Desktop/Ontologias_Papers/DUBLIN/foaf/jena/prov_foaf - Copy2.rdf");
        
        
        
        
        
        
        
//Captura de excepciones
        if (!filePropiedades.exists()) {
            try {
                filePropiedades.createNewFile();
            } catch (IOException ex) { 
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            modelPROV_FOAF.write(new PrintWriter(filePropiedades));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
        
        
    }
        
    
    
}
