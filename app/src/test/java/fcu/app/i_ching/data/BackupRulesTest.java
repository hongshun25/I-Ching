package fcu.app.i_ching.data;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.assertTrue;

public class BackupRulesTest {
    @Test
    public void autoBackupExcludesSensitiveRecordStores() throws Exception {
        Document document = parse("src/main/res/xml/backup_rules.xml");

        assertHasRule(document, "exclude", "sharedpref", "i_ching_accounts.xml");
        assertHasRule(document, "exclude", "sharedpref", "i_ching_records.xml");
        assertHasRule(document, "exclude", "database", "i_ching_records.db");
        assertHasRule(document, "exclude", "database", "i_ching_records.db-wal");
        assertHasRule(document, "exclude", "database", "i_ching_records.db-shm");
    }

    @Test
    public void cloudBackupExcludesRecordsAndDeviceTransferRemainsEnabled() throws Exception {
        Document document = parse("src/main/res/xml/data_extraction_rules.xml");

        assertHasRule(document, "exclude", "sharedpref", "i_ching_accounts.xml");
        assertHasRule(document, "exclude", "sharedpref", "i_ching_records.xml");
        assertHasRule(document, "exclude", "database", "i_ching_records.db");
        assertHasRule(document, "exclude", "database", "i_ching_records.db-wal");
        assertHasRule(document, "exclude", "database", "i_ching_records.db-shm");
        assertHasRule(document, "include", "sharedpref", ".");
        assertHasRule(document, "include", "database", ".");
    }

    private Document parse(String path) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        File file = new File(path);
        if (!file.exists()) file = new File("app", path);
        return factory.newDocumentBuilder().parse(file);
    }

    private void assertHasRule(Document document, String tag, String domain, String path) {
        NodeList nodes = document.getElementsByTagName(tag);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            if (domain.equals(element.getAttribute("domain"))
                    && path.equals(element.getAttribute("path"))) {
                return;
            }
        }
        assertTrue("Missing " + tag + " rule for " + domain + ":" + path, false);
    }
}
