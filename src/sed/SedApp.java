/*
 * SedApp.java
 */

package sed;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.logging.Handler;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import net.sourceforge.tuned.FileUtilities;
import org.jdesktop.application.Application;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pt.unl.fct.di.tsantos.util.FileUtils;
import pt.unl.fct.di.tsantos.util.app.Data;
import pt.unl.fct.di.tsantos.util.net.WebRequest;
import pt.unl.fct.di.tsantos.util.app.DefaultSingleFrameApplication;
import pt.unl.fct.di.tsantos.util.app.Setting;
import pt.unl.fct.di.tsantos.util.download.subtitile.Addic7edSubtitleDownloader;
import pt.unl.fct.di.tsantos.util.download.subtitile.Language;
import pt.unl.fct.di.tsantos.util.download.subtitile.LegendasTVDownloader;
import pt.unl.fct.di.tsantos.util.download.subtitile.OpenSubtitlesDownloader;
import pt.unl.fct.di.tsantos.util.download.subtitile.SubtitleDownloader;
import pt.unl.fct.di.tsantos.util.download.subtitile.SubtitleDownloaderTask;
import pt.unl.fct.di.tsantos.util.download.subtitile.TVSubtitlesDownloader;
import pt.unl.fct.di.tsantos.util.time.Ticker;

/**
 * The main class of the application.
 */
public class SedApp extends DefaultSingleFrameApplication {

    @Setting protected File searchDirectory;
    @Setting protected File saveDirectory;
    @Setting protected boolean searchAtStartup = false;
    @Setting protected int searchInterval = 30;
    @Setting protected boolean tvsubsEnabled = true;
    @Setting protected int tvsubsPriority = 2;
    @Setting protected boolean opensubsEnabled = true;
    @Setting protected int opensubsPriority = 1;
    @Setting protected boolean legtvEnabled = false;
    @Setting protected String legtvUser;
    @Setting protected String legtvPwd;
    @Setting protected int legtvPriority = 4;
    @Setting protected boolean addic7edEnabled = false;
    @Setting protected String addic7edUser;
    @Setting protected String addic7edPwd;
    @Setting protected int addic7edPriority = 3;
    @Setting protected boolean firstMatch = true;

    protected Map<String, String> IMDBIDsMap;
    protected static Collection<Language> LANGUAGES =
            loadLanguages().values();
    @Data protected Set<Language> selectedLanguages;
    protected List<Handler> subtitleHandlers;
    protected List<Handler> subtitleTaskHandlers;
    protected Level subtitleLoggerLevel = Level.INFO;

    protected Ticker ticker;
    protected Timer timer;
    protected SubtitleDownloaderTask currentTask;
    protected List<SubtitleDownloader> subtitleDownloaders;
    protected String showsFileLocation;

    @Override
    protected void initApplication() {
        super.initApplication();
        IMDBIDsMap = loadIMDBIDsMap();
        /*languages = loadLanguages().values();*/
        selectedLanguages = new HashSet<Language>();
        subtitleHandlers = new LinkedList<Handler>();
        subtitleTaskHandlers = new LinkedList<Handler>();
        timer = new Timer();
        subtitleDownloaders = new LinkedList<SubtitleDownloader>();
        showsFileLocation = getContext().getResourceMap()
                .getString("Application.showsFileLocation");
        setTrayImageIcon(getContext().getResourceMap().
                getImageIcon("Application.trayIcon"));
        setFrameIcon(getContext().getResourceMap().
                getImageIcon("Application.trayIcon"));
    }

    protected void updateShowList() throws IOException, SAXException {
        populateSettingsDirectory();
        File xmlFile = new File(getSettingsDirectory(), "shows.xml");
        double old_version = -1.0;
        InputStream is = null;
        if (xmlFile.exists()) {
            Document doc = WebRequest.getDocumentD(xmlFile.toURI().toURL());
            Element elem = doc.getDocumentElement();
            NodeList elementsByTagName = elem.getElementsByTagName("version");
            if (elementsByTagName == null || elementsByTagName.getLength() <= 0)
                return;
            Element versionElem = (Element) elementsByTagName.item(0);
            if (versionElem == null) return;
            Node firstChild = versionElem.getFirstChild();
            if (firstChild == null) return;
            String text = firstChild.getTextContent();
            if (text == null) return;
            old_version = Double.parseDouble(text);
        }
        URL url = new URL(showsFileLocation);
        Document doc = WebRequest.getDocumentD(url);
        Element elem = doc.getDocumentElement();
        NodeList elementsByTagName = elem.getElementsByTagName("version");
        if (elementsByTagName == null || elementsByTagName.getLength() <= 0)
            return;
        Element versionElem = (Element) elementsByTagName.item(0);
        if (versionElem == null) return;
        Node firstChild = versionElem.getFirstChild();
        if (firstChild == null) return;
        String text = firstChild.getTextContent();
        if (text == null) return;
        double new_version = Double.parseDouble(text);
        if (new_version > old_version) {
            URLConnection conn = url.openConnection();
            is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(xmlFile);
            FileUtils.copy(is, fos);
            fos.close();
            is.close();
        }
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of SedApp
     */
    public static SedApp getApplication() {
        return Application.getInstance(SedApp.class);
    }

    /*@Override
    protected String exportPropertyExtended(Object obj) {
        String result = super.exportPropertyExtended(obj);
        if (result != null) return result;
        if (obj instanceof File) {
            File file = (File) obj;
            return file.getAbsolutePath();
        }
        return null;
    }

    @Override
    protected <T> T importObjectPropertyExtended(String property,
            Class<T> type) {
        try {
            return super.importObjectPropertyExtended(property, type);
        } catch (UnsupportedOperationException ex) {}
        if (type.equals(File.class)) {
            return type.cast(new File(property));
        }
        throw new UnsupportedOperationException();
    }*/

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(SedApp.class, args);
    }

    @Override
    protected void createSettingsDirectory() {}

    @Override
    protected void populateSettingsDirectory() {
        File xmlFile = new File(getSettingsDirectory(), "shows.xml");
        if (!xmlFile.exists()) {
            try {
                InputStream is = SedApp.class.getResourceAsStream(
                        "resources/shows.xml");
                FileOutputStream fos = new FileOutputStream(xmlFile);
                FileUtils.copy(is, fos);
                is.close();
                fos.close();
            } catch (IOException ex) {
                // Should not happen
            }
        }
    }

    @Override
    protected void update() throws Exception {
        updateShowList();
    }

    @Override
    protected String initSettingsDirectory() {
        return ".sed";
    }

    @Override
    protected URL initWebLocation() {
        try {
            String webLocation = getContext().getResourceMap()
                    .getString("Application.webLocation");
            return new URL(webLocation);
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    @Override
    protected String initName() {
        return "Subtitle Episode Downloader";
    }

    @Override
    protected Long initUpdateInterval() {
        return new Long(12*60*60*1000);
    }

    protected final Map<String, String> loadIMDBIDsMap() {
        try {
            File xmlFile = new File(getSettingsDirectory(), "shows.xml");
            // Process response
            Document response = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().parse(xmlFile);

            XPathFactory factory = XPathFactory.newInstance();
            XPath xPath = factory.newXPath();

            //Get all search Result nodes
            NodeList nodes =
                    (NodeList) xPath.evaluate("/sed/shows/show",
                    response, XPathConstants.NODESET);
            int nodeCount = nodes.getLength();

            Map<String, String> map = new HashMap<String, String>();

            //iterate over search Result nodes
            for (int i = 0; i < nodeCount; i++) {
                Node n = nodes.item(i);
                String showName = (String) xPath.evaluate("name", n,
                        XPathConstants.STRING);
                if (showName == null || showName.length() <= 0) {
                    continue;
                }

                String imdbID = (String) xPath.evaluate("imdb_id", n,
                        XPathConstants.STRING);
                if (imdbID == null || imdbID.length() <= 0) {
                    continue;
                }

                map.put(showName.toLowerCase(), imdbID);
            }

            //System.out.println(map);
            return map;
        } catch (Exception e) {
            return new HashMap<String, String>();
        }
    }

    private static Map<String, Language> loadLanguages() {
        Map<String, Language> result = new HashMap<String, Language>();
        InputStream is = SedApp.class.getResourceAsStream(
                "resources/languages.txt");
        if (is == null) return result;
        Scanner sc = new Scanner(is);
        int count = 0;
        Map<String, Integer> map = new HashMap<String,Integer>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (++count >= 2) {
                StringTokenizer st = new StringTokenizer(line,"\t\r\n");
                int tokens = st.countTokens();
                if (tokens < 4) continue;
                String code2 = st.nextToken().trim();
                String code2U = code2.toUpperCase();
                String code1 = null;
                boolean upEnabled = false;
                boolean webEnabled = false;
                if (tokens == 4) {
                    code1 = null;
                } else {
                    code1 = st.nextToken().trim();
                }
                String name = st.nextToken().trim();
                Integer a = Integer.parseInt(st.nextToken().trim());
                if (a.intValue() == 1) upEnabled = true;
                a = Integer.parseInt(st.nextToken().trim());
                if (a.intValue() == 1) webEnabled = true;
                if (code1 != null && code1.isEmpty()) code1 = null;
                if (!upEnabled || !webEnabled) continue;
                Integer value = map.get(code2U);
                if (value == null) value = new Integer(0);
                map.put(code2U, ++value);
                result.put(code2U + (value > 1 ? value : "")
                        ,new Language(name, code2, code1));
            }
        }
        return result;
    }

    public Map<String, String> getIMDBIDsMap() {
        return IMDBIDsMap;
    }

    public static Collection<Language> getLanguages() {
        return LANGUAGES;
    }

    public File getSearchDirectory() {
        return searchDirectory;
    }

    public File getSaveDirectory() {
        return saveDirectory;
    }

    public boolean isAddic7edEnabled() {
        return addic7edEnabled;
    }

    public String getAddic7edPwd() {
        return addic7edPwd;
    }

    public String getAddic7edUser() {
        return addic7edUser;
    }

    public boolean isFirstMatch() {
        return firstMatch;
    }

    public boolean isLegtvEnabled() {
        return legtvEnabled;
    }

    public String getLegtvPwd() {
        return legtvPwd;
    }

    public String getLegtvUser() {
        return legtvUser;
    }

    public boolean isOpensubsEnabled() {
        return opensubsEnabled;
    }

    public boolean isSearchAtStartup() {
        return searchAtStartup;
    }

    public int getSearchInterval() {
        return searchInterval;
    }

    public boolean isTvsubsEnabled() {
        return tvsubsEnabled;
    }

    public void setAddic7edEnabled(boolean addic7edEnabled) {
        this.addic7edEnabled = addic7edEnabled;
        if (!this.addic7edEnabled) invalidateAddic7ed();
        else configurationChanged();
        silentSaveData();
    }

    public void setAddic7edPwd(String addic7edPwd) {
        this.addic7edPwd = addic7edPwd;
        if (this.addic7edPwd == null) invalidateAddic7ed();
        else configurationChanged();
        silentSaveData();
    }

    public void setAddic7edUser(String addic7edUser) {
        this.addic7edUser = addic7edUser;
        if (this.addic7edUser == null) invalidateAddic7ed();
        else configurationChanged();
        silentSaveData();
    }

    public void setFirstMatch(boolean firstMatch) {
        this.firstMatch = firstMatch;
        configurationChanged();
        silentSaveData();
    }

    public void setLegtvEnabled(boolean legtvEnabled) {
        this.legtvEnabled = legtvEnabled;
        if (!this.legtvEnabled) invalidateLegtv();
        else configurationChanged();
        silentSaveData();
    }

    public void setLegtvPwd(String legtvPwd) {
        this.legtvPwd = legtvPwd;
        if (this.legtvPwd == null) invalidateLegtv();
        else configurationChanged();
        silentSaveData();
    }

    public void setLegtvUser(String legtvUser) {
        this.legtvUser = legtvUser;
        if (this.legtvUser == null) invalidateLegtv();
        else configurationChanged();
        silentSaveData();
    }

    public void setOpensubsEnabled(boolean opensubsEnabled) {
        this.opensubsEnabled = opensubsEnabled;
        configurationChanged();
        silentSaveData();
    }

    public void setSaveDirectory(File saveDirectory) {
        this.saveDirectory = saveDirectory;
        configurationChanged();
        silentSaveData();
    }

    public void setSearchAtStartup(boolean searchAtStartup) {
        this.searchAtStartup = searchAtStartup;
        configurationChanged();
        silentSaveData();
    }

    public void setSearchDirectory(File searchDirectory) {
        this.searchDirectory = searchDirectory;
        configurationChanged();
        silentSaveData();
    }

    public void setSearchInterval(int searchInterval) {
        this.searchInterval = searchInterval;
        configurationChanged();
        silentSaveData();
    }

    public void setTvsubsEnabled(boolean tvsubsEnabled) {
        this.tvsubsEnabled = tvsubsEnabled;
        configurationChanged();
        silentSaveData();
    }

    public Set<Language> getSelectedLanguages() {
        return selectedLanguages;
    }

    public boolean addSelectedLanguage(Language lang) {
        boolean result = selectedLanguages.add(lang);
        silentSaveData();
        return result;
    }

    public boolean isSelectedLanguage(Language lang) {
        return selectedLanguages.contains(lang);
    }

    public boolean removeSelectedLanguage(Language lang) {
        boolean result = selectedLanguages.remove(lang);
        silentSaveData();
        return result;
    }

    public void invalidateLegtv() {
        legtvEnabled = false;
        legtvUser = null;
        legtvPwd = null;
        configurationChanged();
        silentSaveData();
    }

    public void invalidateAddic7ed() {
        addic7edEnabled = false;
        addic7edUser = null;
        addic7edPwd = null;
        configurationChanged();
        silentSaveData();
    }

    public List<SubtitleDownloader> getSubtitleDownloaders() {
        return subtitleDownloaders;
    }

    private void configurationChanged() {
        if (currentTask != null) {
            ticker.remove(currentTask);
            //tiker.cancel();
            timer.purge();
        }
        if (searchDirectory == null || saveDirectory == null) return;
        List<String> empty = new LinkedList<String>();
        subtitleDownloaders.clear();
        if (tvsubsEnabled) {
            SubtitleDownloader sd = new TVSubtitlesDownloader(empty,
                getSelectedLanguages(), saveDirectory);
            sd.setPriority(tvsubsPriority);
            subtitleDownloaders.add(sd);
        }
        if (opensubsEnabled) {
            SubtitleDownloader sd = new OpenSubtitlesDownloader(empty,
                getSelectedLanguages(), saveDirectory, getIMDBIDsMap());
            sd.setPriority(opensubsPriority);
            subtitleDownloaders.add(sd);
        }
        if (legtvEnabled &&
                legtvUser != null && legtvPwd != null) {
            SubtitleDownloader sd = new LegendasTVDownloader(empty,
                getSelectedLanguages(), saveDirectory, legtvUser, legtvPwd);
            sd.setPriority(legtvPriority);
            subtitleDownloaders.add(sd);
        }
        if (addic7edEnabled &&
                addic7edUser != null && addic7edPwd != null) {
            SubtitleDownloader sd = new Addic7edSubtitleDownloader(empty,
                getSelectedLanguages(),
                    saveDirectory, addic7edUser, addic7edPwd);
            sd.setPriority(addic7edPriority);
            subtitleDownloaders.add(sd);
        }
        for (SubtitleDownloader sd : subtitleDownloaders) {
            sd.addLoggerHandlers(subtitleHandlers);
            sd.setLoggerLevel(subtitleLoggerLevel);
        }
        currentTask = new SubtitleDownloaderTask(searchDirectory,
                new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return FileUtilities.hasExtension(pathname,
                                "mkv", "avi");
                    }
                }, subtitleDownloaders, firstMatch);
        currentTask.addLoggerHandlers(subtitleTaskHandlers);
        currentTask.setLoggerLevel(subtitleLoggerLevel);

        ticker.setTick(searchInterval);
        ticker.add(currentTask);
    }

    public void addObserver(Observer obs) {
        if (ticker == null) {
            ticker = new Ticker(searchInterval, searchAtStartup);
            ticker.add(obs);
            configurationChanged();
            timer.schedule(ticker, 0, 60*1000);            
        } else ticker.add(obs);
    }

    public void addSubtitleHandler(Handler handler) {
        subtitleHandlers.add(handler);
    }

    public void addSubtitleTaskHandler(Handler handler) {
        subtitleTaskHandlers.add(handler);
    }

    public void setSubtitleLoggerLevel(Level subtitleLoggerLevel) {
        this.subtitleLoggerLevel = subtitleLoggerLevel;
        if (currentTask != null)
            currentTask.setLoggerLevel(subtitleLoggerLevel);
        for (SubtitleDownloader sd : subtitleDownloaders)
            sd.setLoggerLevel(subtitleLoggerLevel);
    }

    public void checkSubtitles() {
        if (ticker != null) ticker.execute();
    }

    public void setLegtvPriority(int priority) {
        this.legtvPriority = priority;
        configurationChanged();
        silentSaveData();
    }

    public void setAddic7edPriority(int priority) {
        this.addic7edPriority = priority;
        configurationChanged();
        silentSaveData();
    }

    public void setTvsubsPriority(int priority) {
        this.tvsubsPriority = priority;
        configurationChanged();
        silentSaveData();
    }

    public void setOpensubsPriority(int priority) {
        this.opensubsPriority = priority;
        configurationChanged();
        silentSaveData();
    }

    public int getAddic7edPriority() {
        return addic7edPriority;
    }

    public int getLegtvPriority() {
        return legtvPriority;
    }

    public int getOpensubsPriority() {
        return opensubsPriority;
    }

    public int getTvsubsPriority() {
        return tvsubsPriority;
    }
}
