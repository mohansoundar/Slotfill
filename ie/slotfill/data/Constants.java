/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.slotfill.data;

/**
 *
 * @author mohan
 */
public interface Constants {
    public String tag_post = "POST";
    public String tag_docId = "DOCID";
    public String[] tagsToTrack = new String[]{ tag_post, tag_docId };

    public String field_content = "content";
    public String field_docId = "docId";
    public String[] luceneDocFieldNames = new String[]{ field_content, field_docId };  // it is considered parallel to tagsToTrack elements respectively
}
