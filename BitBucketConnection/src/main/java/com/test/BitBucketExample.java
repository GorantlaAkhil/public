package com.test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;

public class BitBucketExample {

    public static void main(String[] args) throws IOException {

        //GetConnection();
        String username = "thotanarendra551";
        String password = "Narendra@4669";
        String encodedCredentials = Base64.getEncoder().encodeToString((username+":"+password).getBytes("UTF-8"));
        createFileInRepository(username,"bitbucket","master","GM","test",encodedCredentials);
    }

    private static void GetConnection() throws IOException {
        String username = "thotanarendra551";
        String password = "Narendra@466";
        String encodedCredentials = Base64.getEncoder().encodeToString((username+":"+password).getBytes("UTF-8")); //Bitbucket REST API needs the credentials to be Base64 encoded with "UTF-8" formatting

        String bitBucketUrl = "https://bitbucket.org/thotanarendra/learnings/src/master/test";
        URL repositoryUrl = new URL (bitBucketUrl);
        HttpURLConnection connection = (HttpURLConnection) repositoryUrl.openConnection();
        //For authentication
        connection.setFollowRedirects(false);
        connection.addRequestProperty("Authorization", "Basic "+encodedCredentials);
        connection.setRequestMethod("GET");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line = null;
        while ( (line = bufferedReader.readLine()) != null)
            System.out.println(line);

    }

    static void createFileInRepository(String username, String repositoryName, String branchName, String data, String filename, String encodedCredentials) throws IOException {
       // String urlToAccess = "https://bitbucket.org/"+username+"/"+repositoryName+"/src/";
        String urlToAccess = "https://bitbucket.org/thotanarendra/bitbucket/src/master/";
        URL repositoryUrl = new URL (urlToAccess);
        HttpURLConnection connection = (HttpURLConnection) repositoryUrl.openConnection();
        //For authentication
        connection.addRequestProperty("Authorization", "Basic "+encodedCredentials);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        //For setting the form data in the post request to add a new file and fill it with your data
        try{
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
           // out.write("branch="+branchName+"&"+"/"+filename+"="+data);
            out.write(filename+"="+data);
            out.close();
            connection.connect();
            System.out.println(connection.getResponseCode()+" "+connection.getResponseMessage());
            System.out.println("File Created");
        }catch (Exception e){
            System.out.println("File creation failed.");
        }
    }
}
