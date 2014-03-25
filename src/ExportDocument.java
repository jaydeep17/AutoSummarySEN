import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Exports results to Various file formats
 */
public class ExportDocument {

    private static final String HEADER = "Summary created using " + Globals.APP_NAME + " v[" + Globals.APP_VERSION + "]\n" +
            "============================================================\n\n";

    /**
     * Creates a proper title for the document, along with the query used
     * to get the results
     * @param query (query used to get the results)
     * @return String (title)
     */
    private static String getTitle(String query) {
        return "Query : [ "+ query + " ]\n------------------------------------------------------------\n\n";
    }

    /**
     * Generates a proper footer for the document,
     * along with current date/time
     * @return String (footer)
     */
    private static String getFooter() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return "\n\n\n- " + dateFormat.format(date);
    }


    /**
     * Exports the results to text file
     * @param path Path to store the text file
     * @param query Query used to get the results
     * @param content What to write in the text file
     * @throws IOException
     */
    public static void toText(String path, String query, String content) throws IOException {
        final String title = getTitle(query);
        final String footer = getFooter();
        String contentToWrite = HEADER + title + content + footer;
        FileWriter fw = new FileWriter(path);
        fw.write(contentToWrite);
        fw.close();
    }


    /**
     * Exports the results to PDF file
     * @param path Path to store the text file
     * @param query Query used to get the results
     * @param content What to write in the text file
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public static void toPDF(String path, String query, String content) throws FileNotFoundException, DocumentException {
        final String title = getTitle(query);
        final String footer = getFooter();
        String contentToWrite = HEADER + title + content + footer;

        com.itextpdf.text.Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(path));
        doc.open();
        doc.addCreationDate();
        doc.add(new Paragraph(contentToWrite));
        doc.close();
    }
}