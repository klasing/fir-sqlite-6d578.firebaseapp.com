package src;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import com.google.firebase.auth.FirebaseCredentials;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClientBuilder;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class Model {
    protected Model(final Control control) {
        Log.d(LOG_TAG, "<<constructor>> Model()");

        this.control = control;
    }
    //************************************************************************
    //*                 authorizeFirebase
    //************************************************************************
    protected boolean authorizeFirebase() {
        Log.d(LOG_TAG, ".authorizeFirebase()");

        try {
            FileInputStream serviceAccount = new FileInputStream(json_file_name);

            // initialize the app with a service account,
            // granting admin privileges
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl(firebase_url)
                .build();

            FirebaseApp.initializeApp(options);

        } catch(Exception e) {
            Log.d(LOG_TAG, e.getClass().getName() + ": " + e.getMessage());
            return false;
        }

        Log.i("authorizeFirebase successfull");
        return true;
    }
    //************************************************************************
    //*                 authorizeGoogle
    //************************************************************************
    protected boolean authorizeGoogle() {
        Log.d(LOG_TAG, ".authorizeGoogle()");

        try {
            FileInputStream serviceAccount = new FileInputStream(json_file_name);

            // authenticate a Google credential with the service account
            GoogleCredential googleCred = GoogleCredential.fromStream(serviceAccount);

            // add the required scopes to the Google credential
            GoogleCredential scoped = googleCred.createScoped(
                Arrays.asList(
                  "https://www.googleapis.com/auth/firebase.database",
                  "https://www.googleapis.com/auth/userinfo.email",
                  "https://www.googleapis.com/auth/cloud-platform",
                  "https://www.googleapis.com/auth/firebase.readonly"
                )
            );

            // use the Google credential to generate an access token
            // an access token will expire
            scoped.refreshToken();
            oauth2_token = scoped.getAccessToken();
            // get service account id
            serviceAccountId = scoped.getServiceAccountId();

        } catch(Exception e) {
            Log.d(LOG_TAG, e.getClass().getName() + ": " + e.getMessage());
            return false;
        }

        Log.i("authorizeGoogle successfull");
        return true;
    }
    //************************************************************************
    //*                 listenFirebase
    //************************************************************************
    protected void listenFirebase() {
        Log.d(LOG_TAG, ".listenFirebase()");

        // as an admin, the app has access to read and write all data,
        // regardless of Security Rules
        DatabaseReference databaseReference = FirebaseDatabase
            .getInstance()
            .getReference(root);

        // listen for database changes
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, ".onDataChange()");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOG_TAG, ".onCancelled()");
                Log.i("" + databaseError.getCode());

                Main.exit(3);
            }
        });

        // listen for child events
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.d(LOG_TAG, ".onChildAdded()");
                //Log.i("Value: " + dataSnapshot.getValue().toString());
                //Log.i("Ref: " + dataSnapshot.getRef().toString());

                // map the received json to an object
                JsonObject jsonObject =
                    dataSnapshot.getValue(JsonObject.class);

                if (jsonObject.getText() != null) {
                    // if text is not null, a message is received
                    control.receiveMessage(jsonObject);
                }
                if (jsonObject.getImageUrl() != null) {
                    // 1) if imageUrl contains http:// then
                    //    wait for an onChildChanged event
                    if (jsonObject.getImageUrl().contains("https://")) {
                        return;
                    }
                    // 2) if imageUrl contains gs:// then
                    //    download image message
                    if (jsonObject.getImageUrl().contains("gs://")) {
                        String imageUrl = jsonObject.getImageUrl();
                        int length = imageUrl.length();
                        int i = imageUrl.indexOf('/', 5);
                        // object_name is [MAP][KEY][FILE_NAME]
                        String object_name = imageUrl.substring(i + 1, length);
                        object_name = object_name.replaceAll("/", "%2F");


                        // search google-cloud-storage for metadata
                        // this must be synchronized to wait for a downloadToken value
                        String downloadToken = getToken(object_name);
                        // download image from google-cloud-storage
                        BufferedImage bufferedImage = getImage(object_name,
                            downloadToken);

                        control.receiveImage(jsonObject, bufferedImage);
                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.d(LOG_TAG, ".onChildChanged()");
                //Log.i(dataSnapshot.getValue().toString());

                // map the received json to an object
                JsonObject jsonObject =
                    dataSnapshot.getValue(JsonObject.class);

                if (jsonObject.getImageUrl() != null) {
                    // if imageUrl is not null, an image is created
                    // process imageUrl, to get object_name
                    String imageUrl = jsonObject.getImageUrl();
                    int length = imageUrl.length();
                    int i = imageUrl.indexOf('/', 5);
                    // object_name is [MAP][KEY][FILE_NAME]
                    String object_name = imageUrl.substring(i + 1, length);
                    object_name = object_name.replaceAll("/", "%2F");

                    // search google-cloud-storage for metadata
                    // this must be synchronized to wait for a downloadToken value
                    String downloadToken = getToken(object_name);

                    // download image from google-cloud-storage
                    BufferedImage bufferedImage = getImage(object_name,
                        downloadToken);

                    control.receiveImage(jsonObject, bufferedImage);
                }
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, ".onChildRemoved()");
                Log.i(dataSnapshot.getValue().toString());
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                Log.d(LOG_TAG, ".onChildMoved()");
                Log.i(dataSnapshot.getValue().toString());

                // do nothing
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(LOG_TAG, ".onCancelled()");
                Log.i("" + databaseError.getCode());

                Main.exit(3);
            }
        });
    }
    //************************************************************************
    //*                 getToken
    //************************************************************************
    private String getToken(final String object_name) {
        Log.d(LOG_TAG, ".getToken()");

        String downloadToken = null;

        try {
            String url = "https://www.googleapis.com/storage/v1/b/" +
                bucket_name + "/o/" + object_name;
            // set first header
            Map<String, String> mapHeader = new HashMap<String, String>();
            mapHeader.put("Authorization", "Bearer " + oauth2_token);
            // call httpRequestSync, to wait for a downloadToken value
            // to be received
            HttpEntity httpEntity =
                httpRequestSync("get", url, mapHeader, null, null, null);

            // read httpEntity content, from an inputstream
            String entityContent = readEntityContent(httpEntity);

            // process string to get value for token
            String string_for_search = "\"firebaseStorageDownloadTokens\": \"";
            int start_index = entityContent.indexOf(string_for_search) +
                string_for_search.length();
            int end_index = entityContent.indexOf('\"', start_index);
            downloadToken = entityContent.substring(start_index, end_index);

        } catch(Exception e) {
            Log.d(LOG_TAG, e.getClass().getName() + ": " + e.getMessage());
        }

        return downloadToken;
    }
    //************************************************************************
    //*                 getImage
    //************************************************************************
    private BufferedImage getImage(final String object_name,
        final String downloadToken) {

        Log.d(LOG_TAG, ".getImage()");

        BufferedImage bufferedImage = null;
        try {
            String url = "https://firebasestorage.googleapis.com/" +
                "v0/b/" + bucket_name + "/o/" + object_name +
                "?alt=media&token=" + downloadToken;
            // set first header
            Map<String, String> mapHeader = new HashMap<String, String>();
            mapHeader.put("Authorization", "Bearer " + oauth2_token);
            HttpEntity httpEntity =
                httpRequest("get", url, mapHeader, null, null, null);

            // read httpEntity content, from an inputstream,
            // for a BufferedImage
            InputStream inputStream = httpEntity.getContent();
            bufferedImage = ImageIO.read(inputStream);

        } catch(Exception e) {
            Log.d(LOG_TAG, e.getClass().getName() + ": " + e.getMessage());
        }

        return bufferedImage;
    }
    //************************************************************************
    //*                 sendMessage
    //************************************************************************
    protected void sendMessage(final JsonObject jsonObject) {
        Log.d(LOG_TAG, ".sendMessage()");

        try {
            String url = firebase_url + root + extension + "?access_token=" +
                oauth2_token;
            httpRequest("post", url, null, jsonObject, null, null);
        } catch(Exception e) {
            Log.d(LOG_TAG, e.getClass().getName() + ": " + e.getMessage());
        }

    }
    //************************************************************************
    //*                 sendImage
    //************************************************************************
    protected void sendImage(final JsonObject jsonObject,
        final String file_name, final Image image,
        final String file_extension) {

        Log.d(LOG_TAG, ".sendImage()");

        String key = null;

        try {
            String url = firebase_url + root + extension + "?access_token=" +
                oauth2_token;
            // 1) post spinner url in Firebase
            HttpEntity httpEntity =
                httpRequest("post", url, null, jsonObject, null, null);

            // read httpEntity content, from an inputstream
            String entityContent = readEntityContent(httpEntity);

            // 2) Firebase replies with a 'name' value,
            //    which is the key of the Firebase message,
            //    construct, with this key, the path to store the image in
            //    google-cloud-storage
            String string_for_search = "\"name\":\"";
            int start_index = entityContent.indexOf(string_for_search) +
                string_for_search.length();
            int end_index = entityContent.indexOf('\"', start_index);
            key = entityContent.substring(start_index, end_index);

            String object_name = key + "%2F" + file_name;

            url = "https://www.googleapis.com/" +
                "upload/storage/v1/b/" + bucket_name + "/o?uploadType=media" +
                "&name=" + object_name;
            Map<String, String> mapHeader = new HashMap<String, String>();
            // set first header
            mapHeader.put("Authorization", "Bearer " + oauth2_token);
            // set second header
            mapHeader.put("Content-Type", "image/" + file_extension);

            // add payload
            // TODO full path must be known to load from any directory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write((BufferedImage)image, file_extension, baos);
            byte[] data = baos.toByteArray();

            httpEntity =
                httpRequest("post", url, mapHeader, null, data, null);

            // 3) change the Firebase message
            //    the imageUrl in Firebase must now point to the image
            //    in google-cloud-storage
            url = firebase_url + root + "/" + key + extension + "?access_token=" +
                oauth2_token;

            Map<String, String> mapPatch = new HashMap<String, String>();
            // set key and value for patch
            mapPatch.put("imageUrl", "gs://" + bucket_name + "/" + key + "/" +
                file_name);

            httpEntity =
                httpRequest("patch", url, mapHeader, null, data, mapPatch);

        } catch(Exception e) {
            Log.d(LOG_TAG, e.getClass().getName() + ": " + e.getMessage());
        }

    }
    //************************************************************************
    //*                 readEntityContent
    //************************************************************************
    protected String readEntityContent(final HttpEntity httpEntity)
        throws Exception {

        Log.d(LOG_TAG, ".readEntityContent()");

        String string = null;

        // read httpEntity content, from an inputstream
        InputStream inputStream = httpEntity.getContent();
        final int MAX_BUF_LEN = 2048;
        char[] aChar = new char[MAX_BUF_LEN];
        Reader reader = new BufferedReader(
            new InputStreamReader(inputStream, "UTF-8"));
        int n;
        while ((n = reader.read(aChar)) != -1);
        string = String.valueOf(aChar);

        return string;
    }
    //************************************************************************
    //*                 httpRequest
    //************************************************************************
    private HttpEntity httpRequest(final String mode, final String url,
        final Map<String, String> mapHeader, final JsonObject jsonObject,
        final byte[] data, final Map<String, String> mapPatch)
        throws Exception {

        Log.d(LOG_TAG, ".httpRequest()");

        HttpResponse httpResponse = null;

        HttpClient httpClient = HttpClientBuilder.create().build();
        if (mode.toLowerCase().equals("post")) {
            // create post request
            HttpPost httpPost = new HttpPost(url);

            // include mapHeader for a post request
            if (mapHeader != null) {
                // iterate over mapHeader
                for (Map.Entry<String, String> entry : mapHeader.entrySet()) {
                    // set header
                    httpPost.setHeader(entry.getKey(), entry.getValue());
                }
            }

            if (jsonObject != null) {
                // bring json object into writer object
                Writer writer = new StringWriter();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(writer, jsonObject);
                httpPost.setEntity(new StringEntity(writer.toString()));
            }

            if (data != null) {
                httpPost.setEntity(new ByteArrayEntity(data));
            }

            // execute post request
            httpResponse = httpClient.execute((HttpRequestBase) httpPost);
        }
        if (mode.toLowerCase().equals("get")) {
            // create get request
            HttpGet httpGet = new HttpGet(url);

            // include mapHeader for a get request
            if (mapHeader != null) {
                // iterate over mapHeader
                for (Map.Entry<String, String> entry : mapHeader.entrySet()) {
                    // set header
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }

            // get request never carries a jsonObject

            // execute get request
            httpResponse = httpClient.execute((HttpRequestBase) httpGet);
        }
        if (mode.toLowerCase().equals("patch")) {
            // create patch request
            HttpPatch httpPatch = new HttpPatch(url);

            // create writer object
            Writer writer = new StringWriter();
            // point to first entry in map
            Map.Entry<String, String> entry = mapPatch.entrySet().iterator().next();
            writer.write("{\"" + entry.getKey() + "\":\"" +
                entry.getValue() + "\"}");
            httpPatch.setEntity(new StringEntity(writer.toString()));

            // execute patch request
            httpResponse = httpClient.execute((HttpRequestBase) httpPatch);
        }

        // log status code and status phrase
        Log.i(Integer.toString(httpResponse.getStatusLine().getStatusCode()) +
            " " + httpResponse.getStatusLine().getReasonPhrase());

        // return false, if operation fails
        if (httpResponse.getStatusLine().getStatusCode() != 200)
            return null;

        // return the HttpEntity
        return httpResponse.getEntity();
    }
    //************************************************************************
    //*                 httpRequestSync
    //************************************************************************
    private HttpEntity httpRequestSync(final String mode, final String url,
        final Map<String, String> mapHeader, final JsonObject jsonObject,
        final byte[] data, final Map<String, String> mapPatch)
        throws Exception {

        Log.d(LOG_TAG, ".httpRequestSync()");

        HttpResponse httpResponse = null;

        // now, let's try the HttpAsync
        CloseableHttpAsyncClient httpClient = HttpAsyncClients.createDefault();
        try {
            // start the client
            httpClient.start();

            // create get request
            final HttpGet httpGet = new HttpGet(url);

            // include mapHeader for a get request
            if (mapHeader != null) {
                // iterate over mapHeader
                for (Map.Entry<String, String> entry : mapHeader.entrySet()) {
                    // set header
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }

            // execute get request
            Future<HttpResponse> future = httpClient.execute(httpGet, null);
            // and wait until a response is received
            httpResponse = future.get();

        } finally {
            httpClient.close();
        }

        return httpResponse.getEntity();

        // for now call old httpRequest
        //HttpEntity httpEntity =
        //    httpRequest("get", url, mapHeader, null, null, null);

        //return httpEntity();

    }
    //************************************************************************
    //*                 declare
    //************************************************************************
    private static final String LOG_TAG =
        "*** " + Model.class.getSimpleName();

    private static final String json_file_name =
        "C:/Users/Klasing/MyProgramming/MyJava/java2018/" +
        "fir-sqlite-6d578-firebase-adminsdk-7ducb-b5526f6270.json";
    private static final String firebase_url =
        "https://fir-sqlite-6d578.firebaseio.com";
    private static final String root = "/messages";
    private static final String extension = ".json";
    private static final String bucket_name = "fir-sqlite-6d578.appspot.com";

    private static String oauth2_token = null;
    protected static String serviceAccountId = null;

    private static Control control = null;
}