package us.percept.pile.repo.arxiv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import us.percept.pile.model.Paper;
import us.percept.pile.repo.AsyncPaperSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: spartango
 * Date: 8/2/13
 * Time: 6:28 PM
 */
public class ArxivSource extends AsyncPaperSource {
    private static final String ARXIV_HOST = "export.arxiv.org";

    private static final Logger logger = LoggerFactory.getLogger(ArxivSource.class);

    private static final String     DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final DateFormat dateFormat  = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);

    private static final int    MAX_RESULTS   = 25;
    private static final String QUERY_PATH    = "/api/query";
    private static final String PAPER_ACTION  = "id_list";
    private static final String SEARCH_ACTION = "search_query";

    private HttpClient client;

    public ArxivSource() {
        client = vertx.createHttpClient().setHost(ARXIV_HOST);
    }

    // Asynchronous requests for arxiv materials
    @Override
    public void requestPaper(final String identifier) {
        String request = QUERY_PATH + "?" + PAPER_ACTION + "=" + identifier;
        client.get(request, new Handler<HttpClientResponse>() {
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
                        logger.info("Arxiv request succeeded with body of " + event.length() + "b");
                        try {
                            Paper paper = parsePaper(parseAtomBody(event.toString()));
                            notifyPaperReceived(paper);
                        } catch (ParserConfigurationException | IOException | SAXException e) {
                            logger.error("Failed to parse body ", e);
                            notifyPaperFailure(identifier, e);
                        }
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
        String requestPath = QUERY_PATH
                             + "?"
                             + SEARCH_ACTION
                             + "="
                             + URLEncoder.encode(query)
                             + "&max_results="
                             + MAX_RESULTS;
        client.get(requestPath,
                   new Handler<HttpClientResponse>() {
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
                                   logger.info("Arxiv search succeeded with body of " + event.length() + "b");
                                   try {
                                       Collection<Paper> results = parseResults(parseAtomBody(event.toString()));
                                       notifyResultsReceived(query, results);
                                   } catch (ParserConfigurationException | IOException | SAXException e) {
                                       logger.error("Failed to parse body ", e);
                                       notifySearchFailure(query, e);
                                   }
                               }
                           });

                       }
                   }).exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable event) {
                logger.error("Arxiv request failed with ", event);
                // Notify that there's been an error
                notifySearchFailure(query, event);
            }
        }).end();
    }

    private Document parseAtomBody(String body) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(new InputSource(new StringReader(body)));
    }

    private Paper parsePaper(Document document) {
        // Get the Entry tag, it's the only one
        NodeList entries = document.getElementsByTagName("entry");
        Node entry = entries.item(0);
        return parsePaper(entry);
    }

    private Paper parsePaper(Node entry) {
        NodeList children = entry.getChildNodes();
        Paper paper = new Paper();

        // Read the fields sequentially, as we can't get them by name
        for (int i = 0; i < children.getLength(); i++) {
            Node field = children.item(i);
            if (field.getNodeName().equals("author")) {
                // First and only child node is a <name>
                String name = field.getTextContent().trim();
                paper.addAuthor(name);
            } else if (field.getNodeName().equals("id")) {
                paper.setIdentifier(field.getTextContent());
            } else if (field.getNodeName().equals("published")) {

                String dateString = field.getTextContent();
                // Date is in yyyy-MM-ddTHH:mm:ssZ
                try {
                    Date result = dateFormat.parse(dateString);
                    paper.setDate(result);
                } catch (ParseException e) {
                    logger.warn("Failed to parse publication date for " + dateString);
                }
            } else if (field.getNodeName().equals("title")) {
                paper.setTitle(field.getTextContent());
            } else if (field.getNodeName().equals("summary")) {
                paper.setSummary(field.getTextContent());
            } else if (field.getNodeName().equals("link")) {
                // Check if this is the PDF link
                Node titleNode = field.getAttributes().getNamedItem("title");
                Node hrefNode = field.getAttributes().getNamedItem("href");
                if (titleNode != null && titleNode.getNodeValue().equals("pdf")) {
                    paper.setFileLocation(hrefNode.getNodeValue());
                }
            }
            // Otherwise it's an extraneous field.
            // No, not everything in the arxiv metadata is actually useful. And who cares about DOIs?!
        }

        // Build a Paper
        return paper;
    }

    private Collection<Paper> parseResults(Document document) {
        NodeList entries = document.getElementsByTagName("entry");
        List<Paper> papers = new ArrayList<>(entries.getLength());

        logger.info("Query returned with " + entries.getLength() + " papers");

        // Parse each entry, which represents a paper
        for (int i = 0; i < entries.getLength(); i++) {
            papers.add(parsePaper(entries.item(i)));
        }

        return papers;
    }


}
