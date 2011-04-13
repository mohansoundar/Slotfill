package ie.slotfill.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mohan
 */
public class Entity {
    private String name;
    private String id;
    private String type;

    private Map<String, String> factsMap = new HashMap<String, String>();

    public Map<String, String> getFacts() {
        return factsMap;
    }

    public void setFacts(Map<String, String> facts) {
        this.factsMap = facts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String title) {
        this.type = title;
    }

    public void addFact(String factName, String fact){
        this.factsMap.put(factName, fact);
    }

    public String[] getAliases(){
        String[] aliasFacts = new String[]{ "playername", "fullname", "official_name", "native_name" };
        List<String> aliasList = new ArrayList<String>();
        for( String fact : aliasFacts ){
            String factValue = factsMap.get(fact);
            if ( factValue != null ) aliasList.add( factValue );
        }
        return aliasList.toArray(new String[0]);
    }
}
