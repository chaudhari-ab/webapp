package com.product.crud.services;

//import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.product.crud.Exception.DataNotFoundExeception;
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
import java.util.UUID;

@Service
public class StorageService {


    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3client;

    @Autowired
    ImageRepo imageRepo;

    public Image uploadImage(MultipartFile file,Integer product_id) throws Exception{
//        String path = String.format("%s/%s/", bucketName, product_id);
//        String fileName = String.format("%s/%s",String.valueOf(UUID.randomUUID()), file.getOriginalFilename());
//
//        //String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
//        File file2put = mpf2f(file);
        String bucketPath=null;
//        s3client.putObject(new PutObjectRequest(bucketName,path,file2put));
//        bucketPath=String.valueOf(s3client.getUrl(bucketName,fileName));
//        file2put.delete();

        if (file.isEmpty()) {
            throw new Exception("Cannot upload empty file");
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setCacheControl("public, max-age=31536000");
        String path = String.format("%s/%s", bucketName,  product_id);
        String fileName = String.format("%s/%s",String.valueOf(UUID.randomUUID()), file.getOriginalFilename());
        System.out.println(path+" "+fileName);

        try {
            System.out.println("create Image "+59);
            s3client.putObject(path, fileName,  file.getInputStream(), objectMetadata);
            System.out.println("create Image "+60);
        } catch (Exception e) {

            throw new IllegalStateException("Failed to upload file", e);
        }

        bucketPath=String.valueOf(s3client.getUrl(bucketName,fileName));
        return saveImageinDB(product_id,fileName,bucketPath);

    }

    private Image saveImageinDB(Integer product_id, String fileName, String bucketPath) {

        Image imagetoSave = new Image();
        imagetoSave.setProduct_id(product_id);
        imagetoSave.setFile_name(fileName);
        imagetoSave.setDate_created(LocalDate.now());
        imagetoSave.setS3_bucket_path(bucketPath);

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
        if(image==null) return "Image Not Found";
        fileName=image.getFile_name();

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
        if(list1!=null && !list1.isEmpty()) {
            return list1;
        }else {
            return null;
        }
    }

    public boolean isAuthorisedWithProduct(Integer product_id,Integer image_id) {
        Optional<Image> image = imageRepo.findById(image_id);
        if(image.isPresent()){
            if(image.get().getProduct_id()==product_id)
                return true;
            return false;
        }
        return false;
    }
}
