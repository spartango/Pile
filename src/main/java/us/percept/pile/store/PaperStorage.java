package us.percept.pile.store;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import us.percept.pile.model.Paper;

import java.io.File;
import java.util.Collection;
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
    private ConcurrentMap<String, Paper> queuedPapers;
    private ConcurrentMap<String, Paper> archivedPapers;

    public PaperStorage() {
        queuedPapers = masterDatabase.getHashMap("queuedPapers");
        archivedPapers = masterDatabase.getHashMap("archivedPapers");
    }

    public void enqueuePaper(Paper paper) {
        queuedPapers.put(paper.getIdentifier(), paper);
    }

    public Paper dequeuePaper(String id) {
        return queuedPapers.remove(id);
    }

    public void archivePaper(Paper paper) {
        archivedPapers.put(paper.getIdentifier(), paper);
    }

    public Paper unarchivePaper(String id) {
        return archivedPapers.remove(id);
    }

    public void deletePaper(String id) {
        queuedPapers.remove(id);
        archivedPapers.remove(id);
    }

    public Collection<Paper> getQueue() {
        return queuedPapers.values();
    }

    public Collection<Paper> getArchived() {
        return archivedPapers.values();
    }

    public void updatePaper(Paper paper) {
        if(queuedPapers.containsKey(paper.getIdentifier())) {
            queuedPapers.put(paper.getIdentifier(), paper);
        }

        if(archivedPapers.containsKey(paper.getIdentifier())) {
            archivedPapers.put(paper.getIdentifier(), paper);
        }
    }
}
