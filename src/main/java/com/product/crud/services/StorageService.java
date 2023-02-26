package com.product.crud.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class StorageService {


    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3client;

    public String uploadImage(MultipartFile file){
        String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
        File file2put = mpf2f(file);
        s3client.putObject(new PutObjectRequest(bucketName,fileName,file2put));
        file2put.delete();
        return "ImageUploaded";
    }

    public byte[] downloadImage(String imageName){
        S3Object s3Object = s3client.getObject(bucketName,imageName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try{
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String deleteImage(String imageName){
        s3client.deleteObject(bucketName,imageName);
        return imageName+" deleted successfully";
    }

    private File mpf2f(MultipartFile file){
        File convertedFile = new File(file.getOriginalFilename());
        try{
            FileOutputStream fos = new FileOutputStream(convertedFile);
            fos.write(file.getBytes());
        }catch(Exception e){
            System.out.println("Error Converting file - "+e.getMessage());
        }
        return convertedFile;
    }
}
