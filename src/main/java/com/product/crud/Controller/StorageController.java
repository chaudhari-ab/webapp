package com.product.crud.Controller;

import com.product.crud.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class StorageController {

    @Autowired
    private StorageService storageService;

    @RequestMapping(path = "/v1/product/image", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(@RequestParam(value="file")MultipartFile file){
        return storageService.uploadImage(file);
    }

    @RequestMapping(path = "/v1/product/download/{imageName}", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> downloadImage(@PathVariable String imageName){
    byte[] data = storageService.downloadImage(imageName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok().contentLength(data.length)
                .header("Content-type","application/octet-stream")
                .header("Content-disposition","attachment;filename=\""+imageName+"\"")
                .body(resource);
    }

    @RequestMapping(path = "/v1/product/delete/{imageName}", method = RequestMethod.DELETE)
    public String deleteImage(@PathVariable String imageName){
        return storageService.deleteImage(imageName);
    }
}
