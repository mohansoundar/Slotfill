package ie.slotfill.query;

import ie.slotfill.model.Query;
import in.mohansoundar.utils.logging.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author mohan
 */
public class QueryDocument extends DefaultHandler{
    private List<Query> queryList;

    public QueryDocument(Reader reader) {
        parse( reader );
    }

    public List<Query> getQueries(){
        return queryList;
    }
    
    private void parse(Reader reader) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse( new InputSource(reader), this );
        } catch (Exception ex) {
            Logger.log(ex.getMessage());
        }
    }


    @Override
    public void startDocument() throws SAXException {
        queryList = new ArrayList<Query>();
    }

    String tempTagValue;
    @Override
    public void startElement(String uri, String name, String qname, Attributes attr) throws SAXException {
        tempTagValue="";
        if( qname.equals("query") )
            queryList.add(new Query());
        //System.out.println( qname + " element started");
    }

    @Override
    public void endElement(String uri, String name, String qname) throws SAXException {
        if( qname.equals("name") )
            getLastQuery().setEntityName(tempTagValue);
        else if ( qname.equals("docid") )
            getLastQuery().setContextDocId(tempTagValue);
        else if ( qname.equals("enttype") )
            getLastQuery().setEntityType(tempTagValue);
        else if ( qname.equals("nodeid") )
            getLastQuery().setKnowledgeBaseId(tempTagValue);
        
        //System.out.println( qname + " - " + tempTagValue );
        tempTagValue="";
    }

    @Override
    public void characters(char content[], int start, int length){
        char[] realContent = new char[length];
        System.arraycopy(content, start, realContent, 0, length);
        tempTagValue += new String(realContent);
    }

    private Query getLastQuery() {
        return queryList.get(queryList.size()-1);
    }

    public static void main(String args[]) throws FileNotFoundException{
        File queryFile = new File("/home/mohan/test/ie/test-run/1-query.xml");
        new QueryDocument( new FileReader(queryFile) );
    }
}
