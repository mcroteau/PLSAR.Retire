package io.informant;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.StringInputStream;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import dev.blueocean.annotations.Bind;
import dev.blueocean.annotations.Controller;
import dev.blueocean.annotations.JsonOutput;
import dev.blueocean.annotations.Media;
import dev.blueocean.annotations.http.Get;
import dev.blueocean.annotations.http.Post;
import dev.blueocean.model.FileComponent;
import dev.blueocean.model.NetworkRequest;
import dev.blueocean.model.NetworkResponse;
import dev.blueocean.model.RequestComponent;
import io.informant.assets.BlockService;
import io.informant.model.MediaPartial;
import io.informant.model.ResponseRange;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MediaController {

    @Bind
    BlockService blockService;

    @Get("/view/upload")
    public String viewUpload(){
        return "upload.jsp";
    }

    @Post("/upload")
    public String upload(NetworkRequest req, NetworkResponse resp){

        System.out.println("before:");
        for(RequestComponent requestComponent : req.getRequestComponents()){
            System.out.println(":" + requestComponent.getName());
        }
        System.out.println("after:");

        RequestComponent requestComponent = req.getRequestComponent("media");
        FileComponent fileComponent = requestComponent.getFileComponents().get(0);
        String extension = Main.getExtension(fileComponent.getFileName());
        String guid = Main.getGuid(23) + "."  + extension;
        InputStream inputStream = new ByteArrayInputStream(fileComponent.getFileBytes());
        PutObjectResult putObjectResult = blockService.send(guid, inputStream);
        if(putObjectResult == null){
            resp.set("message", "issue please try again.");
            return "[redirect]/view/upload";
        }
        resp.set("message", "uploaded");
        return "upload.jsp";
    }



    @JsonOutput
    @Get("/mock")
    public String mock() throws IOException {
        StringBuilder une = new StringBuilder();
        une.append("first line\n");
        une.append("second line\n");

        StringBuilder deux = new StringBuilder();
        deux.append("third line");

        InputStream inputStream = new StringInputStream(une.toString());
        OutputStream baos = new ByteArrayOutputStream();

        int bytesread = 0;
        int totalread = 0;
        int unesize = une.toString().getBytes().length;
        int deuxsize = deux.toString().getBytes().length;

        byte[] data = new byte[unesize];
        int iterations = 1;
        while (inputStream.available() != 0){
            bytesread = inputStream.read(data, 0, data.length);
            baos.write(data, 0, bytesread);
            totalread = totalread + bytesread;
            if(inputStream.available() == 0 && iterations != 2){
                inputStream = new StringInputStream(deux.toString());
                data = new byte[deuxsize];
            }
            iterations++;
        }

        baos.flush();
        baos.close();

        return baos.toString();
    }

    @Media
    @Get("/media/{file}")
    public void getVideo(@Variable String mediaFile, HttpExchange httpExchange) throws IOException, InterruptedException {

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        Regions region = Regions.US_EAST_1;
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();

        GetObjectMetadataRequest metadataRequest = new GetObjectMetadataRequest(bucket, mediaFile);
        ObjectMetadata objectMetadata = s3.getObjectMetadata(metadataRequest);
        Long fileSize = objectMetadata.getContentLength();

        Headers headers = httpExchange.getResponseHeaders();
        headers.add("Content-Type", "multipart/byteranges;");

        List<ResponseRange> responseRanges = new ArrayList();

        Long openRangeHeaders = 0L;
        Long endRangeHeaders = 1024L * 10;
        Long increment = 1024L * 10;
        Long introPartials = 14L;


        MediaExecutor finalExecutor = null;
        List<MediaExecutor> allExecutors = new ArrayList<>();
        for(int foo = 0; foo < fileSize.intValue(); foo+=increment){
            String range = "bytes " + openRangeHeaders + "-" + endRangeHeaders + "/" + fileSize;
            ResponseRange responseRange = new ResponseRange(range);
            responseRanges.add(responseRange);
            openRangeHeaders+= increment;
            endRangeHeaders+= increment;
            if(endRangeHeaders > fileSize)endRangeHeaders = fileSize;
            MediaExecutor mediaExecutor = new MediaExecutor(openRangeHeaders, endRangeHeaders, bucket, mediaFile, s3);
            if(foo < fileSize.intValue()){
                allExecutors.add(mediaExecutor);
            }else{
                finalExecutor = mediaExecutor;
            }
        }


        for(ResponseRange header : responseRanges){
            headers.add("Content-Range", header.getRange());
        }
        headers.add("Content-Type", "video/mp4");
        httpExchange.sendResponseHeaders(206, fileSize);

        Integer activeIndex = 0;
        List<MediaExecutor> introExecutors = new ArrayList();
        introExecutors.add(finalExecutor);
        for(MediaExecutor mediaExecutor : allExecutors){
            if(activeIndex  < introPartials){
                mediaExecutor.fetch();
                introExecutors.add(mediaExecutor);
            }
        }


        OutputStream outputStream = httpExchange.getResponseBody();


        List<InputStream> inputStreams = introExecutors.stream().map(executor -> executor.getMediaObject().getObjectContent()).collect(Collectors.toList());
        InputStream inputStream = inputStreams.remove(0);

        int bytesread = 0;
        int totalread = 0;
        byte[] byteholder = new byte[inputStream.available()];

        while (inputStream.available() != 0){
            bytesread = inputStream.read(byteholder, 0, byteholder.length);
            outputStream.write(byteholder, 0, bytesread);
            totalread = totalread + bytesread;
            if(inputStream.available() == 0 && inputStreams.size() > 0){
                inputStream = inputStreams.remove(0);
                byteholder = new byte[inputStream.available()];
            }
        }
        System.out.println(":" + totalread);
        outputStream.flush();
        outputStream.close();

    }



    public class MediaExecutor{

        Long openRange;
        Long endRange;
        String bucket;
        String mediaFile;
        AmazonS3 amazonS3;
        S3Object mediaObject;
        MediaPartial mediaPartial;

        public MediaExecutor(Long openRange, Long endRange, String bucket, String mediaFile, AmazonS3 amazonS3){
            this.openRange = openRange;
            this.endRange = endRange;
            this.bucket = bucket;
            this.mediaFile = mediaFile;
            this.amazonS3 = amazonS3;
        }

        public void fetch(){
            try {
                GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucket, mediaFile)
                        .withRange(openRange, endRange);
                this.mediaObject = amazonS3.getObject(rangeObjectRequest);
                byte[] mediaBytes = new byte[(int)mediaObject.getObjectMetadata().getContentLength()];
                InputStream inputStream = mediaObject.getObjectContent();
                OutputStream outputStream = new ByteArrayOutputStream();
                int bytesRead;
                while ((bytesRead = inputStream.read(mediaBytes, 0, mediaBytes.length)) != -1) {
                    outputStream.write(mediaBytes, 0, bytesRead);
                }
                this.mediaPartial = new MediaPartial(mediaBytes);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        public MediaPartial getMediaPartial(){
            return this.mediaPartial;
        }

        public S3Object getMediaObject(){
            return this.mediaObject;
        }

    }



}
