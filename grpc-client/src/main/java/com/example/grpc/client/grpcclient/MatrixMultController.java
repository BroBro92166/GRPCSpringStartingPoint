package com.example.grpc.client.grpcclient;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class MatrixMultController {
    @RequestMapping(value = "/doUpload", method = RequestMethod.POST, consumes = {"multipart/form-data"})

    public String upload(@RequestParam MultipartFile[] file, int deadline) {
        GRPCClientService grpcClientService = new GRPCClientService();
        grpcClientService.uploadFile(file);
        grpcClientService.checkFiles();

        return grpcClientService.matrixmult();
    }

    @ExceptionHandler(FileStorageException.class)
    public String handleStorageFileNotFound(FileStorageException e) {
        return "redirect:/failure.html";
    }
}
