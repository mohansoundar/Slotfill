package ie.slotfill.utils;

import ie.slotfill.data.Constants;
import ie.slotfill.model.Entity;
import ie.slotfill.model.Query;
import ie.slotfill.query.QueryDocument;
import in.mohansoundar.utils.logging.Logger;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author mohan
 */
public class Retriever {

    public static List<Document> retrieve( File indexDir, String fieldToSearch, String queryString ) throws CorruptIndexException, IOException, ParseException{
        List<Document> resDocs = new ArrayList<Document>();

        IndexReader reader = IndexReader.open(FSDirectory.open(indexDir), true); // only searching, so read-only=true
        Searcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
        
        QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, fieldToSearch, analyzer);
        org.apache.lucene.search.Query query = parser.parse(queryString);
        System.out.println("Searching for: " + query.toString(fieldToSearch));
        
        int hitsPerPage = 10;
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
        searcher.search(query, collector);

        //Logger.log("Result Docs are : ");
        ScoreDoc docs[] = collector.topDocs().scoreDocs;
        for (ScoreDoc resDoc : docs) {
            int docId = resDoc.doc;
            Document doc = searcher.doc(docId);
            resDocs.add(doc);
        }

        reader.close();
        return resDocs;
    }

    public static List<Document> retrieve( File indexDir, Query query, File kbIndexDir ) throws CorruptIndexException, IOException, ParseException{
        List<Document> resDocs = new ArrayList<Document>();
        List<String> searchWordList = new ArrayList<String>();

        searchWordList.addAll(Arrays.asList(query.getEntityName().split(" ")));         // the name is split into individual physical words and added to the search list
        Entity kbEntity = getEntity( kbIndexDir, query.getKnowledgeBaseId() );

        for (String alias : kbEntity.getAliases()){   // the alias names of entity are collected and for each of it
            if ( alias == null ) continue;
            searchWordList.addAll(Arrays.asList(alias.split(" ")));                     // the name is again split into words as above
            alias = alias.replaceAll("\n", "").toLowerCase();
            //Logger.log("about to search : " + alias);
            resDocs.addAll( retrieve(indexDir, Constants.field_content, alias) );
        }
/*
        String fullSearchString = Helper.getString(searchWordList, " ").replace("\n", "").toLowerCase();
 Logger.log("Search String : " + fullSearchString);
        resDocs = retrieve(indexDir, Constants.field_content, fullSearchString);*/
        return resDocs;
    }

    public static void main( String args[] ) throws Exception{
        File docIndexDir = new File("/home/mohan/test/ie/test-run/index"/*arg[0]*/ + "/docs");
        List<Document> docs = new ArrayList<Document>();
        
        //docs = Retriever.retrieve(indexDir, Constants.field_content, "abcdefg");
        //System.out.println( "Total docs :" + docs.size() );
        //System.exit(0);
        final File queryFile = new File("/home/mohan/test/ie/test-run/1-query.xml"/*arg[1]*/);
        final File kbIndexDir = new File("/home/mohan/test/ie/test-run/index/kb"/*arg[2]*/);
        
        QueryDocument queryDoc = new QueryDocument(new FileReader(queryFile));

        for( Query query : queryDoc.getQueries() ){
            docs.addAll( Retriever.retrieve(docIndexDir, query, kbIndexDir ) );
        }
        System.out.println( "Total docs :" + docs.size() );
    }

    private static Entity getEntity(File kbIndexDir, String knowledgeBaseId) {
        Logger.log("About to search KB Entity : " + knowledgeBaseId);
        Document doc = null;
        try {
            doc = retrieve(kbIndexDir, "id", knowledgeBaseId).get(0); // max 1 doc will be returned
        }
        catch (Exception ex) {
            Logger.log(ex.getMessage());
        }
        if ( doc == null ) return null;

        List<String> mainFields = Arrays.asList( new String[]{"id","name","type" } );
        Entity entity = new Entity();
        entity.setId(doc.get("id"));
        entity.setName(doc.get("name"));
        entity.setType(doc.get("type"));

        for( Fieldable field : doc.getFields() ){
            if ( mainFields.contains(field.name()) ) continue;
            entity.addFact(field.name(), field.stringValue());
        }
        Logger.log("Entity Retrieved : " + entity.getName());
        return entity;
    }
}
