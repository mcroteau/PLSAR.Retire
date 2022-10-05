package io.service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import qio.annotate.Property;
import qio.annotate.Service;

import java.io.InputStream;

@Service
public class SeaService {

    @Property("digital.ocean.key")
    String key;

    @Property("digital.ocean.secret")
    String secret;

    private static final String BUCKET = "barter";

    public PutObjectResult send(String name, InputStream stream){
        try {
            Regions region = Regions.AP_NORTHEAST_1;
            AWSCredentialsProvider oceanCredentials = new AWSStaticCredentialsProvider(
                    new BasicAWSCredentials(key, secret));
            AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(oceanCredentials)
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration("sfo3.digitaloceanspaces.com", "sfo3"))
                    .build();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(stream.available());

            PutObjectRequest putObj = new PutObjectRequest(SeaService.BUCKET, name, stream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            return s3.putObject(putObj);

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
