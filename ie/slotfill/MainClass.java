/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ie.slotfill;

import ie.slotfill.kb.KBDocument;
import ie.slotfill.utils.Indexer;
import ie.slotfill.utils.Retriever;
import ie.slotfill.model.KnowledgeBase;
import ie.slotfill.model.Query;
import ie.slotfill.query.QueryDocument;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.document.Document;

/**
 *
 * @author mohan
 */
public class MainClass {

    public static void main(String[] args) throws Exception {    // arg[0]=docs path, arg[1]=index gen path
        
        final File docDir = new File("/home/mohan/test/ie/test-run/docs"/*args[0]*/);
        File docIndexDir = new File("/home/mohan/test/ie/test-run/index"/*arg[1]*/ + "/docs");
        Indexer.index(docDir, docIndexDir);

        final File kbaseFile = new File("/home/mohan/test/ie/test-run/kb/1-kb.xml"/*arg[2]*/);
        File kbIndexDir = new File("/home/mohan/test/ie/test-run/index"/*arg[1]*/ + "/kb");
        Indexer.index(kbaseFile, kbIndexDir);
 
        List<Document> docs = new ArrayList<Document>();
        final File queryFile = new File("/home/mohan/test/ie/test-run/1-query.xml"/*arg[1]*/);
        QueryDocument queryDoc = new QueryDocument(new FileReader(queryFile));
        for( Query query : queryDoc.getQueries() ){
            docs.addAll( Retriever.retrieve(docIndexDir, query, kbIndexDir ) );
        }
        System.out.println( "Total docs :" + docs.size() );
    }
}
