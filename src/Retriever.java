import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Retriever {

    private IndexSearcher searcher;
    private Analyzer standardAnalyzer;

    public Retriever(String indxDir) throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indxDir)));
        searcher = new IndexSearcher(reader);
        standardAnalyzer = new StandardAnalyzer(Version.LUCENE_46);
    }

    // depreciated
    public void search(String searchFor, String searchInField, int maxHits) throws ParseException, IOException {
        QueryParser parser = new QueryParser(Version.LUCENE_46, searchInField, standardAnalyzer);
        Query qry = parser.parse(searchFor);
        System.out.println("Searching for: " + qry.toString());
        TopDocs results = searcher.search(qry, maxHits, new Sort(new SortField("title", SortField.Type.STRING)));
        ScoreDoc[] hits = results.scoreDocs;

        int numTotalHits = results.totalHits;
        System.out.println(numTotalHits + " total matching documents");

        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            System.out.println(doc.get("filename"));
//            System.out.printf("%.10f %s\n", hits[i].score, doc.get("filename"));
        }
    }

    public ArrayList<XmlDocument> topRelevantResults(String searchFor, int maxHits) throws ParseException, IOException {
        QueryParser parser1 = new QueryParser(Version.LUCENE_46, "title",standardAnalyzer);
        Query qry1 = parser1.parse(searchFor);
        qry1.setBoost((float) 2.0);
        QueryParser parser2 = new QueryParser(Version.LUCENE_46, "contents",standardAnalyzer);
        Query qry2 = parser2.parse(searchFor);

        BooleanQuery finalQuery = new BooleanQuery();
        finalQuery.add(qry1, BooleanClause.Occur.SHOULD);
        finalQuery.add(qry2, BooleanClause.Occur.SHOULD);

        TopDocs results = searcher.search(finalQuery, maxHits);
        ScoreDoc[] hits = results.scoreDocs;

        int numTotalHits = results.totalHits;
        System.out.println(numTotalHits + " total matching documents");

        ArrayList<XmlDocument> xmlDocuments = new ArrayList<XmlDocument>();
        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            XmlDocument xml = new XmlDocument();
            xml.setFilename(doc.get("filename"));
            xml.setTitle(doc.get("title"));
            xml.setContent(doc.get("contents"));
            xmlDocuments.add(xml);
        }

        return xmlDocuments;
    }
}