package sed;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Source;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pt.unl.fct.di.tsantos.util.net.WebRequest;

public class SedXMLParser
{
    public static void main(String[] args) throws IOException {
        write(parse(new File("shows_clean_ted.xml")),
                new File("shows_clean_sed.xml"), 1);
    }

    private SedXMLParser() {}

    public static final void write(List<TVShowInfo> l, File f, double version)
            throws IOException {
        FileWriter fw = new FileWriter(f);
        fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        fw.write("<sed>\n");
        fw.write("\t<version>" + version + "</version>\n");
        fw.write("\t<shows>\n");
        for (TVShowInfo i : l) {
            fw.write("\t\t<show>\n");
            fw.write("\t\t\t<name>" + escapeXML(i.getName()) + "</name>\n");
            if (i.getTvcom() != null)
                fw.write("\t\t\t<tv_com>" + i.getTvcom() + "</tv_com>\n");
            if (i.getImdb() != null)
                fw.write("\t\t\t<imdb_id>" + i.getImdb() + "</imdb_id>\n");
            fw.write("\t\t</show>\n");
        }
        fw.write("\t</shows>\n");
        fw.write("</sed>\n");
        fw.flush();
        fw.close();
    }

    public static class TVShowInfo {
        String name;
        String imdb;
        String tvcom;

        public TVShowInfo(String name, String imdb, String tvcom) {
            this.name = name;
            this.imdb = imdb;
            this.tvcom = tvcom;
        }

        public String getName() {
            return name;
        }

        public String getImdb() {
            return imdb;
        }

        public String getTvcom() {
            return tvcom;
        }
    }

    private static List<TVShowInfo> parse(Element nodeList) {
        if (nodeList == null) return null;

        List<TVShowInfo> list = new LinkedList<TVShowInfo>();

        NodeList shows = nodeList.getElementsByTagName("show");

        // if there are shows
        if (shows != null && shows.getLength() > 0) {
            for (int i = 0; i < shows.getLength(); i++) {
                Element show = (Element) shows.item(i);
                String elName =
                        getTextValue(show, "name", "");
                
                String imdb = getTextValue(show, "imdb_id", null);
                String tv_com = getTextValue(show, "tv_com", null);

                if (imdb == null && tv_com == null) continue;

                if (imdb == null) {                  
                    // try to guess it
                    try {
                        String search = elName.replace(":", "");
                        search = URLEncoder.encode(search, "UTF-8");
                        System.out.println(search);
                        URL url = new URL("http://www.imdb.com/find?s=tt&q="
                                + search);
                        HttpURLConnection conn = (HttpURLConnection)
                                url.openConnection();
                        conn.setRequestProperty("User-Agent",
                                "Mozilla/5.0 (Windows; U; " +
                                "Windows NT 6.0; en-GB; rv:1.9.1.2) " +
                                "Gecko/20090729 " +
                                "Firefox/3.5.2 (.NET CLR 3.5.30729)");
                        Source source = new Source(conn.getInputStream());
                        source.setLogger(null);
                        String x = source.toString();
                        int l = x.indexOf("div id=\"main\"");
                        if (l >= 0)  {
                            x = x.substring(l);
                            Pattern pattern2 = Pattern.compile("tt\\d{7}");
                            Matcher matcher = pattern2.matcher(x);
                            if (matcher.find()) {
                                System.out.println(imdb = matcher.group(0));
                            }
                        } else {
                            l = x.indexOf("<head>");
                            if (l >= 0) {
                                x = x.substring(l);
                                Pattern pattern2 = Pattern.compile("tt\\d{7}");
                                Matcher matcher = pattern2.matcher(x);
                                if (matcher.find()) {
                                    System.out.println(imdb = matcher.group(0));
                                }
                            }
                        }
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }

                    if (imdb != null) imdb = imdb.substring(2);
                }

                TVShowInfo serie = new TVShowInfo(elName, imdb, tv_com);
                list.add(serie);
            }
        }

        return list;
    }
    
    public static List<TVShowInfo> parse(File f) {
        Element nodeList = null;
        try {
            Document dom = WebRequest.getDocumentD(f.toURI().toURL());
            nodeList = dom.getDocumentElement();
        } catch (SAXException ex) {
        } catch (IOException ex) {
        }
        return parse(nodeList);
    }

    public static List<TVShowInfo> parse(URL url) {
        Element nodeList = null;
        try {
            Document dom = WebRequest.getDocumentD(url);
            nodeList = dom.getDocumentElement();
        } catch (SAXException ex) {
        } catch (IOException ex) {
        }
        return parse(nodeList);
    }

    public static int getIntValue(Element elem, String object, int def) {
        try {
            return Integer.parseInt(getTextValue(elem, object, def + ""));
	} catch(NumberFormatException e) {
            return def;
	}
    }

    public static String getTextValue(Element elem, String object, String def) {
        String result = def == null ? null : new String(def);
        try {
            NodeList nl = elem.getElementsByTagName(object);
            if( nl != null && nl.getLength() > 0 ) {
                Element el1 = (Element)nl.item(0);
                if(el1.getFirstChild() != null)
                    result = el1.getFirstChild().getNodeValue();
            }
            return result;
        } catch(Exception e) {
            return result;
        }
    }

    private static String escapeXML(String s) {
        StringBuilder str = new StringBuilder();
        int len = (s != null) ? s.length() : 0;

        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<':
                    str.append("&lt;");
                    break;
                case '>':
                    str.append("&gt;");
                    break;
                case '&':
                    str.append("&amp;");
                    break;
                case '"':
                    str.append("&quot;");
                    break;
                case '\'':
                    str.append("&apos;");
                    break;

                default:
                    str.append(ch);
            }
        }
        return str.toString();
    }
}
