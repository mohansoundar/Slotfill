/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.slotfill;

import ie.slotfill.data.SGMLDocument;
import ie.slotfill.kb.KBDocument;
import in.mohansoundar.utils.logging.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.apache.lucene.document.Document;

/**
 *
 * @author mohan
 */
public class DocumentFactory {

    public static Document getDocument( File file ){
        Document document = null;
        try {
            if ( file.getName().endsWith(".sgm") )      // data document
                document = new SGMLDocument(new FileReader(file)).getDocument();
            else if ( file.getName().endsWith(".xml") )
                document = new KBDocument(new FileReader(file)).getDocument();
        } catch (FileNotFoundException ex) {
            Logger.log( ex.getMessage() );
        }
        return document;
    }
}
