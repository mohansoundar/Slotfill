package ie.slotfill.utils;

import ie.slotfill.DocumentFactory;
import in.mohansoundar.utils.logging.Logger;
import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author mohan
 */
public class Indexer {

    public static void index(File docDir, File indexDir) {
        System.out.println("Indexing at - " + indexDir.getAbsolutePath());

        try {
            IndexWriter writer = new IndexWriter(FSDirectory.open(indexDir), new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.LIMITED);
            indexDocs(writer, docDir);
            writer.optimize();
            writer.close();

        } catch (IOException e) {
            Logger.log(e.getMessage());
        }
    }

    private static void indexDocs(IndexWriter writer, File file) throws IOException {
        if (file.isDirectory()) {
            for (String insideFileName : file.list())
                indexDocs(writer, new File(file, insideFileName));
        }
        else {
            System.out.println("adding " + file);
            Document doc = DocumentFactory.getDocument( file );
            writer.addDocument(doc);
        }
    }

    public static void main(String[] args) {
        final File dataDir = new File("/home/mohan/test/ie/test-run/docs"/*args[0]*/);
        File indexDir = new File("/home/mohan/test/ie/test-run/index"/*arg[2]*/ + "/docs");
        Indexer.index(dataDir, indexDir);

        final File kbDir = new File("/home/mohan/test/ie/test-run/kb"/*arg[1]*/);
        indexDir = new File("/home/mohan/test/ie/test-run/index"/*arg[2]*/ + "/kb");
        Indexer.index(kbDir, indexDir);
    }
}
