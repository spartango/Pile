package us.percept.pile.repo;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import us.percept.pile.model.Paper;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * User: spartango
 * Date: 8/2/13
 * Time: 6:47 PM
 */
public abstract class AsyncPaperSource implements PaperSource {

    protected static Vertx vertx = VertxFactory.newVertx();
    //protected static ExecutorService executor = Executors.newCachedThreadPool();

    protected List<PaperSourceListener> listeners = new ArrayList<>();

    /* protected void async(Runnable task) {
        executor.submit(task);
    }
    protected <T> Future<T> async(Callable<T> task) {
        return executor.submit(task);
    }
    */

    @Override
    public void addListener(PaperSourceListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(PaperSourceListener listener) {
        listeners.remove(listener);
    }

    protected void notifyPaperReceived(Paper paper) {
        for(PaperSourceListener listener : listeners) {
            listener.onPaperReceived(paper);
        }
    }

    protected void notifyResultsReceived(Collection<Paper> results) {
        for(PaperSourceListener listener : listeners) {
            listener.onResultsReceived(results);
        }
    }

    protected void notifyPaperFailure(String id, Throwable cause) {
        for(PaperSourceListener listener : listeners) {
            listener.onLookupFailure(id, cause);
        }
    }

    protected void notifySearchFailure(String query, Throwable cause) {
        for(PaperSourceListener listener : listeners) {
            listener.onSearchFailure(query, cause);
        }
    }
}
