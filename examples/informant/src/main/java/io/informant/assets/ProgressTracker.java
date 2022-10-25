package io.informant.assets;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import dev.blueocean.model.FileComponent;
import io.informant.model.response.ProgressResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ProgressTracker extends Thread {

    String objectKey;
    String uploadKey;
    String accessKey;
    String secret;
    Long totalBytesTransferred;
    FileComponent fileComponent;
    Map<String, ProgressResponse> progressResponses;

    final String BUCKET = "love.fahrenheit";

    public ProgressTracker(String objectKey,
                           String uploadKey,
                           String accessKey, String secret,
                           Long totalBytesTransferred, FileComponent fileComponent,
                           Map<String, ProgressResponse> progressResponses){
        this.objectKey = objectKey;
        this.secret = secret;
        this.uploadKey = uploadKey;
        this.accessKey = accessKey;
        this.fileComponent = fileComponent;
        this.totalBytesTransferred = totalBytesTransferred;
        this.progressResponses = progressResponses;
    }

    @Override
    public void run(){
        InputStream inputStream = new ByteArrayInputStream(fileComponent.getFileBytes());
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secret);

        Regions region = Regions.US_EAST_1;
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();

        ObjectMetadata metadata = new ObjectMetadata();

        try {
            metadata.setContentLength(inputStream.available());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        PutObjectRequest request = new PutObjectRequest(BUCKET, objectKey, inputStream, metadata)
                .withCannedAcl(CannedAccessControlList.AuthenticatedRead);

        request.withGeneralProgressListener(new ProgressListener() {
            @Override
            public void progressChanged(ProgressEvent progressEvent) {
                if(progressResponses.containsKey(uploadKey)){
                    Long progressBytes = progressEvent.getBytesTransferred();
                    totalBytesTransferred = progressBytes + totalBytesTransferred;
                    ProgressResponse progressResponse = progressResponses.get(uploadKey);
                    progressResponse.setProgressBytes(totalBytesTransferred);
                    progressResponse.setStatus("ok");
                    progressResponses.replace(uploadKey, progressResponse);
                }else{
                    Long progressBytes = progressEvent.getBytesTransferred();
                    ProgressResponse progressResponse = new ProgressResponse("ok", progressBytes);
                    progressResponses.put(uploadKey, progressResponse);
                }
            }
        });

        PutObjectResult result = s3.putObject(request);

    }

    public ProgressResponse getStatus(String guid){
        if(progressResponses.containsKey(guid)){
            return progressResponses.get(guid);
        }
        return new ProgressResponse("0", 0L);
    }

}
