package us.percept.pile.store;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import us.percept.pile.model.Paper;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: spartango
 * Date: 8/4/13
 * Time: 3:12 PM.
 */
public class PaperStorage {
    protected static final String DB_PATH        = "paperStore.db";
    private static         DB     masterDatabase =
            DBMaker.newFileDB(new File(DB_PATH))
                    .closeOnJvmShutdown()
                    .make();

    // MapDB persistent map of papers
    private ConcurrentMap<String, Paper> paperMap;

    public PaperStorage() {
        paperMap = masterDatabase.getHashMap("papersById");
    }

    public Paper getPaper(String id) {
        return paperMap.get(id);
    }

    public void removePaper(String id) {
        paperMap.remove(id);
    }

    public void addPaper(Paper paper) {
        paperMap.put(paper.getIdentifier(), paper);
    }

    public Collection<Paper> getAllPapers() {
        return paperMap.values();
    }

    public Set<String> getAllIds() {
        return paperMap.keySet();
    }

}
