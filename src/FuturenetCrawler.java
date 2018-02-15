import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuturenetCrawler {
    FuturenetDatabase db = new FuturenetDatabase();
    private final String login = "kajak22";
    private final String password = "javaIsBetterThanC++11";

    void login() throws IOException{
        HttpHandler.enableCookieStoring();
        HttpURLConnection con = (HttpURLConnection) new URL("http://kajak22.futurenet.club/postLogin")
                .openConnection();
        Map<String,String> parameters = new LinkedHashMap<>();
        parameters.put("login", login);
        parameters.put("password",password);
        HttpHandler.sendPost(con,parameters);
        HttpHandler.handleRedirecting(con);
    }

    void crawl(int limit, int sleepSeconds, int sleepSecondsAfterTimeout, int maxTimeouts)
            throws SQLException, InterruptedException{
        int timeoutsCounter = 0;
        try {
            db.connect();
        }
        catch(ClassNotFoundException e){
            Logger.log("Can't find jdbc modules: " + e);
        }
        catch(SQLException e){
            Logger.log("Database error: " + e);
        }
        if(limit == -1){
            limit = Integer.MAX_VALUE;
        }
        if(sleepSeconds != -1){
            Thread.sleep(sleepSeconds*1000);
        }
        for (int i =0;i<limit;i++){
            //System.out.println(i);
            String rootLabel = db.getNextUnvisited();
            if(rootLabel == null)
                break;
            Logger.log(rootLabel);
            String url = Person.getFriendsListLink(rootLabel);
            String page = "";
            try {
                page = HttpHandler.getHtmlFromURL(url);
            }
            catch (SocketTimeoutException | UnknownHostException e){
                Logger.log("Request timeout number" + timeoutsCounter + " : " + e);
                if(++timeoutsCounter < maxTimeouts) {
                    Thread.sleep(1000 * sleepSecondsAfterTimeout);
                }
                else {
                    Logger.log("Too many timeouts");
                    System.exit(1);
                }
            }
            catch (IOException e) {
                Logger.log("Link Error: " + e);
                db.setInvalidLink(rootLabel);
                continue;
            }
            timeoutsCounter = 0;
            try {
                db.insertPersons(pageToPersonList(page),rootLabel);
            } catch(SQLException e){
                Logger.log("Insertion error: " + e);
            }
        }
    }

    List<Person> pageToPersonList(String page) {
        Pattern pattern =
                Pattern.compile("<div class=\"name\">\\s+<a href=\".*?\" >\\s+(.*?)\\s+</a>.*?</div>",
                        Pattern.DOTALL);
        Matcher divMatcher = pattern.matcher(page);
        List<Person> personList = new ArrayList<>();
        while(divMatcher.find()) {
            Person p = new Person();
            p.name = divMatcher.group(1);
            if(p.name.length() > 100){
                p.name = p.name.substring(0,100);
            }

            String div = divMatcher.group();

            pattern = Pattern.compile("class=\"friend\"\\s*cid=\"(\\d*)\"\\s*label=\"(.*)\"\\s*.*\\s+gender=\"(\\w*)" +
                    "\"\\s+lang=\"(.*)\" data-user=\"(\\d*)");
            Matcher matcher = pattern.matcher(div);
            if (matcher.find()){
                try {
                    p.cid = Integer.parseInt(matcher.group(1));
                }
                catch (NumberFormatException e) {
                    p.cid = 0;
                }
                try {
                    p.data_user = Integer.parseInt(matcher.group(5));
                }
                catch (NumberFormatException e) {
                    p.data_user = 0;
                }
                p.label = matcher.group(2);
                p.male = matcher.group(3).equals("male") ? 1 : matcher.group(3).equals("female") ? 0 : 2;
                p.lang = matcher.group(4);
                personList.add(p);
            }
        }
        return personList;
    }

    public static void main(String args[]){
        System.setProperty("file.encoding","UTF-8");
        System.out.println(System.getProperty("file.encoding"));
        FuturenetCrawler crawler = new FuturenetCrawler();

        try{
            Logger.initializeLogFile();
        } catch (IOException e){
            System.err.println("Couldn't create error log: " + e);
            return;
        }
        try {
            crawler.login();
        } catch (IOException e){
            Logger.log("Couldn't log in: " + e);
            return;
        }
        try{
            crawler.crawl(-1,-1,60,5);
        } catch (SQLException e){
            Logger.log("Database error: " + e);
        } catch (InterruptedException e){
            Logger.log("Interrupted during sleep: " + e);
        }
    }
}
