package ie.slotfill.kb;

import ie.slotfill.model.Entity;
import ie.slotfill.model.KnowledgeBase;
import in.mohansoundar.utils.logging.Logger;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author mohan
 */
public class KBDocument extends DefaultHandler{
    private Reader contentReader;
    private Document luceneDocument;
    private List<Entity> entityList;

    public KBDocument( Reader reader ){
        contentReader = reader;
        parse();
    }

    public Document getDocument() {
        return luceneDocument;
    }

    public KnowledgeBase getKnowledgeBase(){
        return new KnowledgeBase(entityList);
    }

    private void parse(){
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = parserFactory.newSAXParser();
            parser.parse(new InputSource(contentReader), this);
        } catch (Exception ex) {
            Logger.log(ex.getMessage());
        }
    }

    private String tempTagValue;
    
    @Override
    public void startDocument() throws SAXException {
        this.luceneDocument = new Document();
        this.entityList = new ArrayList<Entity>();
    }

    String factName;
    @Override
    public void startElement(String uri, String name, String qname, Attributes attr) throws SAXException {
        // prepare lucene document's fields
        if( qname.equals("entity") || qname.equals("facts") ){
            for(int i=0; i<attr.getLength(); i++){
                Field field = new Field( attr.getQName(i), attr.getValue(i), Field.Store.YES, Field.Index.ANALYZED);
                this.luceneDocument.add(field);
            }
        }else if ( qname.equals("fact") ){
            factName = attr.getValue(0);        // <fact name="xxx"> interested in xxx here
        }

        // prepare the entities
        if( qname.equals("entity") ){
            Entity entity = new Entity();
            entity.setName( attr.getValue("name") );
            entity.setId( attr.getValue("id") );
            entity.setType( attr.getValue("type") );
            this.entityList.add(entity);
        }
        //System.out.println( qname + " element started");
    }

    @Override
    public void endElement(String uri, String name, String qname) throws SAXException {
        //System.out.println( qname + " ===>" + tempTagValue );
        // lucene related
        if( qname.equals("fact") ){
            Field field = new Field(this.factName, tempTagValue, Field.Store.YES, Field.Index.ANALYZED);
            this.luceneDocument.add(field);
        }else if( qname.equals("wiki_text") ) {
            Field field = new Field( qname, tempTagValue, Field.Store.YES, Field.Index.ANALYZED );
            this.luceneDocument.add(field);
        }

        if( qname.equals("fact") ){
            getLastEntity().addFact(factName, tempTagValue );
            factName = "";
        }

        tempTagValue="";
    }

    @Override
    public void characters(char content[], int start, int length){
            char[] realContent = new char[ length ];
            System.arraycopy(content, start, realContent, 0, length);
            tempTagValue += new String(realContent);
    }

    public static void main(String args[]) throws Exception{
        Document doc = new KBDocument(new FileReader("/home/mohan/test/ie/test-run/kb/1-kb.xml")).getDocument();
        for(Fieldable field : doc.getFields() ){
            System.out.println(field.name() + " - " + field.stringValue());
        }
    }

    private Entity getLastEntity() {
        return this.entityList.get( entityList.size()-1 );
    }

}
