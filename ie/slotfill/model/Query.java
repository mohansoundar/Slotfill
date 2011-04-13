package ie.slotfill.model;

import java.util.List;

/**
 *
 * @author mohan
 */
public class Query {
    private String id;
    private String entityName;
    private String entityType;
    private String contextDocId;
    private String knowledgeBaseId;
    private List<String> ignoreList;

    public String getContextDocId() {
        return contextDocId;
    }

    public void setContextDocId(String contextDocId) {
        this.contextDocId = contextDocId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKnowledgeBaseId() {
        return knowledgeBaseId;
    }

    public void setKnowledgeBaseId(String knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
    }
    

}
