import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Map;

class HttpHandler{
    static void enableCookieStoring(){
        CookieManager cm = new CookieManager();
        CookieHandler.setDefault(cm);
    }
    static String getHtmlFromURL(String urlString) throws IOException {
        URL page = new URL(urlString);
        InputStream inputStream = page.openStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
    static void sendPost(HttpURLConnection con, Map<String, String> parameters) throws IOException{
        con.setDoOutput(true);
        StringBuilder queryStringBuilder = new StringBuilder();
        String encoding = "UTF-8";

        for(Map.Entry<String, String> entry : parameters.entrySet()){
            queryStringBuilder.append(URLEncoder.encode(entry.getKey(),encoding));
            queryStringBuilder.append("=");
            queryStringBuilder.append(URLEncoder.encode(entry.getValue(),encoding));
            queryStringBuilder.append("&");
        }
        // delete last "&"
        queryStringBuilder.deleteCharAt(queryStringBuilder.length()-1);

        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(queryStringBuilder.toString());
        out.flush();
        out.close();
    }

    static HttpURLConnection handleRedirecting(HttpURLConnection con) throws IOException {
        for(int i = 0;i<25;i++) {
            int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK){
                return con;
            }
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER) {

                String urlString = con.getHeaderField("Location");
                //System.out.println(urlString);
                URL url = new URL(urlString);
                con = (HttpURLConnection) url.openConnection();
            }
        }

        throw new IOException("Redirection error");
    }
}
