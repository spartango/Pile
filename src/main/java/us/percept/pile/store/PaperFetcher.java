package us.percept.pile.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.*;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.streams.Pump;
import org.vertx.java.core.streams.ReadStream;
import us.percept.pile.model.Paper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: spartango
 * Date: 8/4/13
 * Time: 3:12 PM.
 */
public class PaperFetcher {
    private String                     paperFolder;
    private List<PaperFetcherListener> listeners;

    protected static     Vertx  vertx  = VertxFactory.newVertx();
    private static final Logger logger = LoggerFactory.getLogger(PaperFetcher.class);

    public PaperFetcher(String paperFolder) {
        this.paperFolder = paperFolder;
        listeners = new ArrayList<>();
    }

    public void fetch(final Paper paper) {
        try {
            final URL url = new URL(paper.getFileLocation());

            // Check the protocol
            if (!url.getProtocol().equals("http")) {
                notifyFetchFailed(paper, new Exception("Invalid protocol"));
                return;
            }

            // Build a client
            final HttpClient client = vertx.createHttpClient();
            client.setHost(url.getHost());
            client.setPort(url.getPort() == -1 ? url.getDefaultPort() : url.getPort());
            client.get(url.getPath(), new Handler<HttpClientResponse>() {
                @Override public void handle(HttpClientResponse event) {
                    // Check the status code
                    if (event.statusCode() != 200) {
                        logger.error("Error fetching paper, bad status: " + event.statusCode());
                        notifyFetchFailed(paper, new Exception("Not OK Status code " + event.statusCode()));
                        return;
                    }

                    // Open up a file to write to
                    writePaper(paper, url.getFile(), event, client);
                }
            }).exceptionHandler(new Handler<Throwable>() {
                @Override public void handle(Throwable event) {
                    logger.error("Error fetching paper", event);
                    notifyFetchFailed(paper, event);
                }
            }).end();

        } catch (MalformedURLException e) {
            logger.error("Invalid URL specified for fetching ", e);
            notifyFetchFailed(paper, e);
        }
    }

    private void writePaper(final Paper paper, String filename, final ReadStream stream, final HttpClient client) {
        final String path = paperFolder+ File.pathSeparator+filename;
        vertx.fileSystem().open(path, new AsyncResultHandler<AsyncFile>() {
            public void handle(AsyncResult<AsyncFile> ar) {
                if (!ar.succeeded()) {
                    // File failed to open
                    logger.error("File "+path+" failed to open");
                    notifyFetchFailed(paper, ar.cause());
                    client.close();
                    return;
                }

                // Pump!
                Pump pump = Pump.createPump(stream, ar.result());

                // When pumping is finished, notify
                stream.endHandler(new Handler<Void>() {
                    @Override public void handle(Void event) {
                        // Update the paper location
                        logger.info("Paper downloaded to "+path);
                        paper.setFileLocation("file://"+path);
                        client.close();
                        notifyFetched(paper);
                    }
                });

                pump.start();
            }
        });
    }

    protected void notifyFetched(Paper p) {
        for (PaperFetcherListener listener : listeners) {
            listener.onPaperFetched(p);
        }
    }

    protected void notifyFetchFailed(Paper p, Throwable e) {
        for (PaperFetcherListener listener : listeners) {
            listener.onFetchFailed(p, e);
        }
    }


}
