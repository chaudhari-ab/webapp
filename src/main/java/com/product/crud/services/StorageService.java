package com.product.crud.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.product.crud.model.Image;
import com.product.crud.repo.ImageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StorageService {


    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3client;

    @Autowired
    ImageRepo imageRepo;

    public Image uploadImage(MultipartFile file,Integer product_id){
        String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
        File file2put = mpf2f(file);
        String bucketPath=null;
        s3client.putObject(new PutObjectRequest(bucketName,fileName,file2put));
        file2put.delete();
        
        return saveImageinDB(product_id,fileName,bucketPath);
//        return "ImageUploaded and Saved in DB";
    }

    private Image saveImageinDB(Integer product_id, String fileName, String bucketPath) {

        Image imagetoSave = new Image();
        imagetoSave.setProduct_id(product_id);
        imagetoSave.setFile_name(fileName);
        imagetoSave.setDate_created(LocalDate.now());
        imagetoSave.setS3_bucket_path("My Bucket");

        return imageRepo.save(imagetoSave);

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

    public String deleteImage(Integer imageid){
        String fileName;
        Image image = imageRepo.getImagebyId(imageid);
        fileName=image.getFile_name();
        System.out.println(fileName);
        System.out.println(image.getS3_bucket_path());

        s3client.deleteObject(bucketName,fileName);
        imageRepo.deleteById(imageid);
        return fileName+" deleted successfully";
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

    public Image getImage(Integer image_Id){
        return imageRepo.getImagebyId(image_Id);
    }

    public List<Image> getAllImages(Integer product_id){
        List<Image> list1= imageRepo.getAllImages(product_id);
        return list1;
    }
}
