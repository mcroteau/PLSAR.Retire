package io.informant.assets;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import io.kakai.annotate.Property;
import io.kakai.annotate.Service;

import java.io.InputStream;

@Service
public class BlockService {

    @Property("s3.bucket")
    String bucket;

    @Property("s3.accessKey")
    String accessKey;

    @Property("s3.secretKey")
    String secretKey;

    public PutObjectResult send(String name, InputStream stream){
        try {

//            String serviceEndpoint = "https://" + "ewr1.vultrobjects.com";
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

            Regions region = Regions.US_EAST_1;
            AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(region)
                    .build();

//            AmazonS3 s3 = AmazonS3ClientBuilder.standard()
//                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, ""))
//                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                    .build();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(stream.available());

            PutObjectRequest request = new PutObjectRequest(bucket, name, stream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            return s3.putObject(request);

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
