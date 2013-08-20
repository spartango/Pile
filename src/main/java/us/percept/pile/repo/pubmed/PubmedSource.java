package us.percept.pile.repo.pubmed;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Author: spartango
 * Date: 8/19/13
 * Time: 2:02 AM.
 */
public class PubmedSource extends AsyncPaperSource {

    private static final Logger logger = LoggerFactory.getLogger(PubmedSource.class);

    private static final String PUBMED_HOST   = "eutils.ncbi.nlm.nih.gov";
    private static final String BASE_URL      = "/entrez/eutils";
    private static final String FETCH_PATH    = "/efetch.fcgi";
    private static final String SEARCH_PATH   = "/esearch.fcgi";
    private static final String FETCH_ACTION  = "id";
    private static final String DB_PARAM      = "db=pubmed";
    private static final String RETURN_PARAM  = "retmode=xml";
    private static final String SEARCH_ACTION = "term";
    private static final String PUBMED_URL    = "http://www.ncbi.nlm.nih.gov/pubmed";

    private HttpClient client;

    public PubmedSource() {
        client = vertx.createHttpClient().setHost(PUBMED_HOST);
    }

    private String buildPaperRequest(String identifier) {
        return BASE_URL
               + "/"
               + FETCH_PATH
               + "?"
               + DB_PARAM
               + "&"
               + FETCH_ACTION
               + "="
               + identifier
               + "&"
               + RETURN_PARAM;
    }

    @Override public void requestPaper(final String identifier) {
        String request = buildPaperRequest(identifier);

        client.get(request, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse event) {
                // If its not a good response, don't carry on
                if (event.statusCode() != 200) {
                    // Notify that there's been an error
                    notifyPaperFailure(identifier, new Exception("Not OK Status code " + event.statusCode()));
                    return;
                }

                // Otherwise download the body
                event.bodyHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer event) {
                        handlePaperResponse(identifier, event.toString());
                    }
                });

            }
        }).exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable event) {
                // Notify that there's been an error
                notifyPaperFailure(identifier, event);
            }
        }).end();
    }

    private void handlePaperResponse(String identifier, String body) {
        try {
            Paper paper = parsePaper(parseXML(body));
            notifyPaperReceived(paper);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            logger.error("Failed to parse body ", e);
            notifyPaperFailure(identifier, e);
        }
    }

    private String buildSearchRequest(String query) {
        return BASE_URL
               + "/"
               + SEARCH_PATH
               + "?"
               + DB_PARAM
               + "&"
               + SEARCH_ACTION
               + "="
               + query
               + "&"
               + RETURN_PARAM;
    }

    @Override public void requestSearch(final String query) {
        String request = buildSearchRequest(query);

        client.get(request,
                   new Handler<HttpClientResponse>() {
                       @Override
                       public void handle(HttpClientResponse event) {
                           // If its not a good response, don't carry on
                           if (event.statusCode() != 200) {
                               // Notify that there's been an error
                               notifySearchFailure(query, new Exception("Not OK Status code " + event.statusCode()));
                               return;
                           }

                           // Otherwise download the body
                           event.bodyHandler(new Handler<Buffer>() {
                               @Override
                               public void handle(Buffer event) {
                                   handleSearchResponse(query, event.toString());
                               }
                           });

                       }
                   })
                .exceptionHandler(new Handler<Throwable>() {
                    @Override public void handle(Throwable event) {
                        // Notify that there's been an error
                        notifySearchFailure(query, event);
                    }
                }).end();
    }


    private void handleSearchResponse(String query, String body) {
        try {
            Collection<String> results = parseResults(parseXML(body));
            requestPapers(query, results);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            logger.error("Failed to parse body ", e);
            notifySearchFailure(query, e);
        }
    }


    private void requestPapers(final String query, Collection<String> papers) {
        StringBuilder paperList = new StringBuilder();
        for (String id : papers) {
            paperList.append(id).append(",");
        }

        String request = buildPaperRequest(paperList.toString());

        client.get(request, new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse event) {
                // If its not a good response, don't carry on
                if (event.statusCode() != 200) {
                    // Notify that there's been an error
                    notifySearchFailure(query, new Exception("Not OK Status code " + event.statusCode()));
                    return;
                }

                // Otherwise download the body
                event.bodyHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer event) {
                        handleListingResponse(query, event.toString());
                    }
                });

            }
        }).exceptionHandler(new Handler<Throwable>() {
            @Override
            public void handle(Throwable event) {
                // Notify that there's been an error
                notifySearchFailure(query, event);
            }
        }).end();
    }

    private void handleListingResponse(String query, String body) {
        try {
            Collection<Paper> results = parseListing(parseXML(body));
            notifyResultsReceived(query, results);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            logger.error("Failed to parse body ", e);
            notifySearchFailure(query, e);
        }
    }

    private Document parseXML(String body) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(new InputSource(new StringReader(body)));
    }

    private Collection<String> parseResults(Document document) {
        // Get each <Id> node
        NodeList entries = document.getElementsByTagName("Id");
        List<String> ids = new ArrayList<>(entries.getLength());

        // The PMID is the text of the <Id>
        for (int i = 0; i < entries.getLength(); i++) {
            ids.add(entries.item(i).getTextContent());
        }

        return ids;
    }

    private Collection<Paper> parseListing(Document document) {
        NodeList citations = document.getElementsByTagName("MedlineCitation");
        List<Paper> papers = new ArrayList<>(citations.getLength());
        for (int i = 0; i < citations.getLength(); i++) {
            papers.add(parsePaper(citations.item(i)));
        }
        return papers;
    }

    private Paper parsePaper(Document document) {
        // Get the medline citation <MedlineCitation>
        NodeList citations = document.getElementsByTagName("MedlineCitation");

        if (citations.getLength() < 1) {
            return null;
        } else {
            return parsePaper(citations.item(0));
        }
    }

    private Paper parsePaper(Node citationNode) {
        Paper paper = new Paper();
        // For each child node
        NodeList childNodes = citationNode.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            String nodeName = child.getNodeName();

            if (nodeName.equals("PMID")) {
                // <PMID Version="1">11748933</PMID>
                String id = child.getTextContent();
                paper.setIdentifier(id);

                // Generate the URL
                paper.setFileLocation(PUBMED_URL + "/" + id);

            } else if (nodeName.equals("DateCreated")) {
                // Parse the date
                Date date = parseDate(child);
                paper.setDate(date);

            } else if (nodeName.equals("otherArticle")) {
                // If its the otherArticle, parse and mutate the paper
                parseOtherArticle(child, paper);
            }
        }

        return paper;
    }

    private Date parseDate(Node child) {
        return null; //TODO implement PubmedSource.parseDate
    }

    private void parseOtherArticle(Node otherArticle, Paper target) {
        // <otherArticle PubModel="Print">
        NodeList childNodes = otherArticle.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            String nodeName = child.getNodeName();
            if (nodeName.equals("ArticleTitle")) {
                // Get the ArticleTitle
                target.setTitle(child.getTextContent());
            } else if (nodeName.equals("Abstract")) {
                // Get the AbstractText
                Node abstractNode = child.getFirstChild();

                // Get the body
                target.setSummary(abstractNode.getTextContent());
            } else if (nodeName.equals("AuthorList")) {
                // Get the AuthorList
                target.setAuthors(parseAuthors(child));
            }

        }
    }

    private List<String> parseAuthors(Node child) {
        // For each author, parse the author

        return null;
    }

}
