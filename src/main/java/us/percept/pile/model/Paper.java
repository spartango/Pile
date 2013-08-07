package us.percept.pile.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User: spartango
 * Date: 8/2/13
 * Time: 6:21 PM
 */
public class Paper implements Serializable {

    private String       identifier;
    private String       title;
    private List<String> authors;
    private String       summary;
    private Date         date;
    private String       fileLocation;

    public Paper() {
        this("", "", new ArrayList<String>(0), "", new Date(), null);
    }

    public Paper(String identifier,
                 String title,
                 List<String> authors,
                 String summary,
                 Date date,
                 String fileLocation) {
        this.identifier = identifier;
        this.title = title;
        this.authors = authors;
        this.summary = summary;
        this.date = date;
        this.fileLocation = fileLocation;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public void addAuthor(String author) {
        authors.add(author);
    }

    public void addAuthors(String... newAuthors) {
        authors.addAll(Arrays.asList(newAuthors));
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}
