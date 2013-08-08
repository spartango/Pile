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
    protected static final String DB_PATH        = "paperStore.mdb";
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

    public Paper getArchivedPaper(String id) {
        return archivedPapers.get(id);
    }

    public void enqueuePaper(Paper paper) {
        queuedPapers.put(paper.getIdentifier(), paper);
        masterDatabase.commit();
    }

    public Paper dequeuePaper(String id) {
        Paper removed = queuedPapers.remove(id);
        masterDatabase.commit();
        return removed;
    }

    public void archivePaper(Paper paper) {
        archivedPapers.put(paper.getIdentifier(), paper);
        masterDatabase.commit();
    }

    public Paper unarchivePaper(String id) {
        Paper paper = archivedPapers.remove(id);
        masterDatabase.commit();
        return paper;
    }

    public void deletePaper(String id) {
        queuedPapers.remove(id);
        archivedPapers.remove(id);
        masterDatabase.commit();
    }

    public Collection<Paper> getQueue() {
        return queuedPapers.values();
    }

    public Collection<Paper> getArchived() {
        return archivedPapers.values();
    }

    public void updatePaper(Paper paper) {
        if (queuedPapers.containsKey(paper.getIdentifier())) {
            queuedPapers.put(paper.getIdentifier(), paper);
        }

        if (archivedPapers.containsKey(paper.getIdentifier())) {
            archivedPapers.put(paper.getIdentifier(), paper);
        }
        masterDatabase.commit();
    }
}
