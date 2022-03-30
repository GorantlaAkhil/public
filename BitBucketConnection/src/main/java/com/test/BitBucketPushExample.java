package com.test;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

public class BitBucketPushExample {

    public static void main(String[] args) throws GitAPIException, IOException {

        String userName = "thotanarendra";
        String password = "fVgKVDJX94FaghJVU4Dw";
        String uri = "https://thotanarendra@bitbucket.org/thotanarendra/learnings.git";
        File srcFile = new File("C:\\Users\\Thota Narendra\\WorkDetails\\Intellij-Projects\\BitBucketConnection\\src\\main\\java\\com\\test\\GitExample.java");
        BitBucketPushExample.gitPublish(uri,userName,password,srcFile);
    }

    public static  void gitPublish(String uri , String userName,String password,File srcFile) throws GitAPIException, IOException {

        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(userName,password);
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        File tempDir = new File(baseDir, "local_checkout");
        if(tempDir.exists())
            FileUtils.deleteDirectory(tempDir);
        Git git =  Git.cloneRepository()
                .setURI(uri)
                .setDirectory(tempDir)
                .setCredentialsProvider(credentialsProvider)
                .call();
        String fileName = srcFile.getName();
        File myFile = new File(git.getRepository().getDirectory().getParent(),fileName);
        FileUtils.copyFile(srcFile,myFile);
        git.add().addFilepattern(srcFile.getName()).call();
        git.commit().setMessage(fileName +" file publish").call();
        PushCommand pushCommand = git.push();
        pushCommand.setCredentialsProvider(credentialsProvider);
        pushCommand.call();
    }

}


