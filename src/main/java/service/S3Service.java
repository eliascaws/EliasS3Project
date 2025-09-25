package service;

import io.github.cdimascio.dotenv.Dotenv;
import se.elias.s3project.utility.Utils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.file.*;

public class S3Service {
    private final S3Client s3;
    private String bucket;

    public S3Service() {
        Dotenv dotenv = Dotenv.load();
        String accessKey = dotenv.get("ACCESS_KEY");
        String secretKey = dotenv.get("SECRET_KEY");
        this.bucket = dotenv.get("BUCKET_NAME");

        this.s3 = S3Client.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

    public void setBucket(String bucketName) {
        this.bucket = bucketName;
        System.out.println("Bytte bucket till: " + bucket);
    }

    public void listFiles() {
        try {
            ListObjectsV2Request req = ListObjectsV2Request.builder()
                    .bucket(bucket).build();
            ListObjectsV2Response res = s3.listObjectsV2(req);
            if (res.contents().isEmpty()) {
                System.out.println("Bucket är tom.");
            } else {
                res.contents().forEach(o ->
                        System.out.println(o.key() + " (" + o.size() + " bytes)"));
            }
        } catch (S3Exception e) {
            System.err.println("Fel vid listning: " + e.awsErrorDetails().errorMessage());
        }
    }

    public void uploadFile(String filePath) {
        Path path = Paths.get(filePath);
        String key = path.getFileName().toString();
        try {
            s3.putObject(PutObjectRequest.builder()
                            .bucket(bucket).key(key).build(),
                    RequestBody.fromFile(path));
            System.out.println("Uppladdad: " + key);
        } catch (S3Exception e) {
            System.err.println("Fel vid uppladdning: " + e.awsErrorDetails().errorMessage());
        }
    }

    public void downloadFile(String key, String destination) {
        try {
            s3.getObject(GetObjectRequest.builder()
                            .bucket(bucket).key(key).build(),
                    Paths.get(destination));
            System.out.println("Nerladdad: " + key);
        } catch (S3Exception e) {
            System.err.println("Fel vid nedladdning: " + e.awsErrorDetails().errorMessage());
        }
    }

    public void deleteFile(String key) {
        try {
            s3.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket).key(key).build());
            System.out.println("Borttagen: " + key);
        } catch (S3Exception e) {
            System.err.println("Fel vid radering: " + e.awsErrorDetails().errorMessage());
        }
    }

    public void searchFiles(String term) {
        try {
            ListObjectsV2Response res = s3.listObjectsV2(
                    ListObjectsV2Request.builder().bucket(bucket).build());
            res.contents().stream()
                    .filter(o -> o.key().contains(term))
                    .forEach(o -> System.out.println(o.key()));
        } catch (S3Exception e) {
            System.err.println("Fel vid sökning: " + e.awsErrorDetails().errorMessage());
        }
    }

    public void uploadFolderAsZip(String folderPath) throws Exception {
        String zipPath = Utils.zipFolder(folderPath);
        uploadFile(zipPath);
        Files.deleteIfExists(Paths.get(zipPath));
    }
}