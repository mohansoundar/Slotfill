/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ie.slotfill.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mohan
 */
public class KnowledgeBase {
    private List<Entity> entityList;
    private Map<String, Entity> entityMap;

    public KnowledgeBase(List<Entity> entityList) {
        this.entityList = entityList;
        entityMap = new HashMap<String, Entity>();
        for( Entity entity : entityList ){
            entityMap.put(entity.getId(), entity);
        }
    }

    public Entity getEntity(String id){
        return entityMap.get(id);
    }
}
