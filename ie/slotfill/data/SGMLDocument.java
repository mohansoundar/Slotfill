package ie.slotfill.data;

import in.mohansoundar.utils.logging.Logger;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author mohan
 */
public class SGMLDocument extends DefaultHandler {
    private Document document;

    public SGMLDocument(Reader reader){
        parse( reader );
    }

    public Document getDocument(){
        return document;
    }

    private void parse( Reader contentReader ){
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = parserFactory.newSAXParser();
            parser.parse(new InputSource(contentReader), this);
        } catch (Exception ex) {
            Logger.log(ex.getMessage());
        }
    }
    
    @Override
    public void startDocument() throws SAXException {
        this.document = new Document();
    }

    @Override
    public void startElement(String uri, String name, String qname, Attributes attr) throws SAXException {
        tempContent="";
        curTagHier.add(qname);
        //System.out.println( qname + " element started");
    }

    @Override
    public void endElement(String uri, String name, String qname) throws SAXException {
        //System.out.println("===>" + tempContent );
        if ( isTrackableTag() ){
            String fieldName = getLuceneField(qname);
            Field field = new Field(fieldName, tempContent, Field.Store.YES, Field.Index.ANALYZED);
            this.document.add(field);
        }
        curTagHier.remove(curTagHier.size()-1);
        //System.out.println( name + " element ended");
    }

    private List<String> tagsToTrack = Arrays.asList( Constants.tagsToTrack );
    @Override
    public void characters(char content[], int start, int length){
        if ( isTrackableTag() ){       // last value of hier is the current tag
            char[] realContent = new char[ length ];
            System.arraycopy(content, start, realContent, 0, length);
            tempContent += new String(realContent);
        }
    }

    private String tempContent;
    private List<String> curTagHier = new ArrayList<String>();
    private boolean isTrackableTag(){
        return tagsToTrack.contains(getCurTagName());
    }
    private String getCurTagName(){
        return curTagHier.get(curTagHier.size()-1);
    }

    private String getLuceneField(String tagName){
        int i=-1;
        for(int j=0; j< Constants.tagsToTrack.length; j++ ){
            String tag = Constants.tagsToTrack[j];
            if( tag.equals(tagName) )
                i=j;
        }
        String luceneField = ( i>=0 )? Constants.luceneDocFieldNames[i] : "";
        return luceneField;
    }

    public static void main(String args[]) throws FileNotFoundException{
        SGMLDocument doc = new SGMLDocument(new FileReader("/home/mohan/temp/ie/test-data/eng-NG-31-100002-11516439.sgm"));
        doc = new SGMLDocument(new FileReader("/home/mohan/temp/ie/test-data/eng-NG-31-100002-11516441.sgm"));
    }
}
