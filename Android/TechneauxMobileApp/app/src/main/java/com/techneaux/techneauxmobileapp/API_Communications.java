package com.techneaux.techneauxmobileapp;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class API_Communications implements Runnable {

    public static String urlSTR; //url string
    public static JSONObject json; //json obj
    public static String result; //result for other activities to pull

    /**
     * Preconditions: none
     * Post Conditions: init the url string json object and result variables
     * Parameters: url string json object and result
     * Use: when starting API Communications
     */
    public API_Communications(String setUrl, JSONObject setJson) {
        urlSTR = setUrl;
        json = setJson;
        result = null;

    }


    /**
     * Preconditions: none
     * Post Conditions: runs HTTPS Post to API
     * Parameters: None
     * Use: when starting API Communications
     */
    public void run() {

        postData();
    }

    /**
     * Note: Please only use this for development testing (for self signed in certificate). Adding this to the
     * public application is a serious blunder.
     *
     * @author Sandip Jadhav
     */
    public static DefaultHttpClient createSSLHTTP() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        // Setup a custom SSL <a class="zem_slink" title="Factory method pattern" href="http://en.wikipedia.org/wiki/Factory_method_pattern" target="_blank" rel="wikipedia">Factory object</a> which simply ignore the certificates
        // validation and accept all type of self signed certificates
        SSLSocketFactory sslFactory = new SimpleSSLSocketFactory(null);
        sslFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        // Enable <a class="zem_slink" title="Hypertext Transfer Protocol" href="http://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol" target="_blank" rel="wikipedia">HTTP</a> parameters
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        // Register the HTTP and HTTPS Protocols. For HTTPS, register our custom SSL Factory object.
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sslFactory, 443));

        // Create a new connection manager using the newly created registry and then create a new HTTP client
        // using this connection manager
        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
        DefaultHttpClient client = new DefaultHttpClient(ccm, params);

        // To-do: Get or Post the data using this newly created http client object
        return client;
    }
    /**
     * Preconditions: none
     * Post Conditions: HTTPS Post to API
     * Parameters: None
     * Use: when sending API Communications
     */
    public static void postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(urlSTR);


        //bypassing ssl self-signed cert
        try {
            httpclient = createSSLHTTP();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }


        try {
            httppost.setEntity(new ByteArrayEntity(
                    json.toString().getBytes("UTF8"))); //defines httppost body
            // Execute HTTP Post Request
            Log.d("API", "json: " + json);
            Log.d("API", "Count: " + json.toString().length());
            HttpContext localContext = new BasicHttpContext();

            //set http client params
            httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false);

            //get http response from api
            HttpResponse response = httpclient.execute(httppost, localContext);


            // for JSON response :
            if (response != null) {
                InputStream is = response.getEntity().getContent(); //get body response

                //decode response into string
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();

                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //end decode response to string
                result = sb.toString();
                Log.d("API", "Result: " + result);
                Log.d("API", response.getStatusLine().toString());


            }


        } catch (ClientProtocolException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
        } catch (IOException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block
        }


    }


}


/**
 * Note: Please only use this for development testing (for self signed in certificate). Adding this to the
 * public application is a serious blunder.
 *
 * @author Sandip Jadhav
 */

class SimpleSSLSocketFactory extends org.apache.http.conn.ssl.SSLSocketFactory {
    private javax.net.ssl.SSLSocketFactory sslFactory = HttpsURLConnection.getDefaultSSLSocketFactory();

    public SimpleSSLSocketFactory(KeyStore truststore)
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(null);

        try {
            SSLContext context = SSLContext.getInstance("TLS");

            // Create a trust manager that does not validate certificate chains and simply
            // accept all type of certificates
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }
            }};

            // Initialize the socket factory
            context.init(null, trustAllCerts, new SecureRandom());
            sslFactory = context.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException, UnknownHostException {
        return sslFactory.createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslFactory.createSocket();
    }
}