package us.percept.pile.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import us.percept.pile.model.Paper;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

/**
 * User: spartango
 * Date: 8/2/13
 * Time: 6:28 PM
 */
public class ArxivSource extends AsyncPaperSource {
    private static final String ARXIV_HOST = "export.arxiv.org";
    private static final long TIMEOUT = 60000; // ms

    private static final Logger logger = LoggerFactory.getLogger(ArxivSource.class);

    private HttpClient client;

    public ArxivSource() {
        client = vertx.createHttpClient().setHost(ARXIV_HOST);
    }

    // Synchronous requests for arxiv materials

    @Override
    public Paper getPaper(final String identifier) {
        final Buffer body = new Buffer();
        synchronized (body) {
            client.get("/api/query?id_list=" + identifier, new Handler<HttpClientResponse>() {
                @Override
                public void handle(HttpClientResponse event) {
                    // If its not a good response, don't carry on
                    if (event.statusCode() != 200) {
                        logger.error("Arxiv returned " + event.statusCode() + " for " + identifier);
                        synchronized (body) {
                            body.notify();
                        }
                        return;
                    }

                    // Otherwise download the body
                    event.bodyHandler(new Handler<Buffer>() {
                        @Override
                        public void handle(Buffer event) {
                            logger.info("Arxiv request succeeded with body of ", event.length() + "b");

                            // Pass the data on for parsing
                            body.setBuffer(0, event);
                            synchronized (body) {
                                // Notify the caller that we're done getting data
                                body.notify();
                            }
                        }
                    });

                }
            }).exceptionHandler(new Handler<Throwable>() {
                @Override
                public void handle(Throwable event) {
                    logger.error("Arxiv request failed with ", event);
                    synchronized (body) {
                        body.notify();
                    }
                }
            }).end();

            try {
                body.wait(TIMEOUT);
            } catch (InterruptedException e) {
                logger.error("Arxiv request was interrupted by ", e);
            }
        }

        // Check that there's a body to parse
        if (body.length() == 0) {
            return null;
        }

        return parsePaper(body.toString());
    }


    @Override
    public Collection<Paper> findPapers(final String query) {
        final Buffer body = new Buffer();
        synchronized (body) {
            client.get("/api/query?search_query=" + query, new Handler<HttpClientResponse>() {
                @Override
                public void handle(HttpClientResponse event) {
                    // If its not a good response, don't carry on
                    if (event.statusCode() != 200) {
                        logger.error("Arxiv returned " + event.statusCode() + " for query " + query);
                        synchronized (body) {
                            body.notify();
                        }
                        return;
                    }

                    // Otherwise download the body
                    event.bodyHandler(new Handler<Buffer>() {
                        @Override
                        public void handle(Buffer event) {
                            logger.info("Arxiv query succeeded with body of ", event.length() + "b");

                            // Pass the data on for parsing
                            body.setBuffer(0, event);
                            synchronized (body) {
                                // Notify the caller that we're done getting data
                                body.notify();
                            }
                        }
                    });

                }
            }).exceptionHandler(new Handler<Throwable>() {
                @Override
                public void handle(Throwable event) {
                    logger.error("Arxiv request failed with ", event);
                    synchronized (body) {
                        body.notify();
                    }
                }
            }).end();

            try {
                body.wait(TIMEOUT);
            } catch (InterruptedException e) {
                logger.error("Arxiv request was interrupted by ", e);
            }
        }

        // Check that there's a body to parse
        if (body.length() == 0) {
            return null;
        }

        return parseResults(body.toString());
    }


    // Asynchronous requests for arxiv materials

    @Override
    public void requestPaper(final String identifier) {
        client.get("/api/query?id_list=" + identifier, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse event) {
                // If its not a good response, don't carry on
                if (event.statusCode() != 200) {
                    logger.error("Arxiv returned " + event.statusCode() + " for " + identifier);
                    // Notify that there's been an error
                    notifyPaperFailure(identifier, new Exception("Not OK Status code " + event.statusCode()));
                    return;
                }

                // Otherwise download the body
                event.bodyHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer event) {
                        logger.info("Arxiv request succeeded with body of ", event.length() + "b");
                        Paper downloaded = parsePaper(event.toString());
                        notifyPaperReceived(downloaded);
                    }
                });

            }
        }).exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable event) {
                logger.error("Arxiv request failed with ", event);
                // Notify that there's been an error
                notifyPaperFailure(identifier, event);
            }
        }).end();

    }

    @Override
    public void requestSearch(final String query) {
        client.get("/api/query?search_query=" + query, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse event) {
                // If its not a good response, don't carry on
                if (event.statusCode() != 200) {
                    logger.error("Arxiv returned " + event.statusCode() + " for " + query);
                    // Notify that there's been an error
                    notifySearchFailure(query, new Exception("Not OK Status code " + event.statusCode()));
                    return;
                }

                // Otherwise download the body
                event.bodyHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer event) {
                        logger.info("Arxiv search succeeded with body of ", event.length() + "b");
                        Collection<Paper> downloaded = parseResults(event.toString());
                        notifyResultsReceived(downloaded);
                    }
                });

            }
        }).exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable event) {
                logger.error("Arxiv request failed with ", event);
                // Notify that there's been an error
                notifyPaperFailure(query, event);
            }
        }).end();
    }


    private Paper parsePaper(String body) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(new InputSource(new StringReader(body)));

        return parsePaper(document);
    }

    private Paper parsePaper(Document document) {
        // Get the Entry tag

        // Get the identifier
        // Get the title
        // Get each of the authors
        // Get the summary
        // Get the PDF URL
        // Get the publication date

        // Build a Paper
        return null;
    }

    private Collection<Paper> parseResults(String s) {

    }
}
