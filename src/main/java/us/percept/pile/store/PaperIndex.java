package us.percept.pile.store;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.percept.pile.model.Paper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: spartango
 * Date: 8/8/13
 * Time: 1:30 PM.
 */
public class PaperIndex {
    private static final   Logger logger        = LoggerFactory.getLogger(PaperIndex.class);
    protected static final String INDEX_PATH    = "paperIndex.ldb";
    private static final   int    HITS_PER_PAGE = 10;

    private Directory        index;
    private IndexWriter      writer;
    private IndexReader      reader;
    private IndexSearcher    searcher;
    private StandardAnalyzer analyzer;


    private PaperStorage storage;

    public PaperIndex(PaperStorage storage) throws IOException {
        index = new RAMDirectory(); // TODO use the database file
        analyzer = new StandardAnalyzer(Version.LUCENE_44);
        writer = new IndexWriter(index, new IndexWriterConfig(Version.LUCENE_44, analyzer));
        reader = DirectoryReader.open(index);
        searcher = new IndexSearcher(reader);

        this.storage = storage;
    }

    public void addPaper(Paper paper) {
        Document document = new Document();

        document.add(new TextField("title", paper.getTitle(), Field.Store.YES));
        document.add(new TextField("summary", paper.getSummary(), Field.Store.YES));
        document.add(new StringField("identifier", paper.getIdentifier(), Field.Store.YES));

        // Add each of the authors
        for (String author : paper.getAuthors()) {
            document.add(new StringField("author", author, Field.Store.YES));
        }

        try {
            writer.addDocument(document);
        } catch (IOException e) {
            logger.error("Error writing paper to index", e);
        }
    }

    public List<Paper> search(String query) {
        ArrayList<Paper> results = new ArrayList<>(HITS_PER_PAGE);
        // Parse the query
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_44,
                                                                      new String[]{"title", "summary"},
                                                                      analyzer);
        queryParser.setDefaultOperator(QueryParser.Operator.OR);

        try {
            Query parsedQuery = queryParser.parse(query);

            TopScoreDocCollector collector = TopScoreDocCollector.create(HITS_PER_PAGE, true);
            searcher.search(parsedQuery, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            logger.info("Search complete: ");
            for (ScoreDoc doc : hits) {
                logger.info(searcher.doc(doc.doc).get("identifier") + ": " + doc.score);

                String identifier = searcher.doc(doc.doc).get("identifier");
                Paper paper = storage.getArchivedPaper(identifier);

                if (paper != null) {
                    results.add(paper);
                }
            }

        } catch (ParseException e) {
            logger.error("Failed to parse query ", e);
        } catch (IOException e) {
            logger.error("Failed to search index ", e);
        }

        return results;

    }

}
