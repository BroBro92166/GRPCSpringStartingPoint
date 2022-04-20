package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.MatrixRequest;
import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import com.example.grpc.client.grpcclient.FileStorageException;

import java.io.*;
import java.io.IOException;
import java.io.InputStream;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.*;
import java.util.ArrayList;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;


@Service
public class GRPCClientService {

    @Value("$(upload.path")
    private String path;

    private ArrayList<String> MatrixA = new ArrayList<String>();

    private ArrayList<String> MatrixB = new ArrayList<String>();
    
    private double deadline = 0;

    private ArrayList<String> Filename = new ArrayList<String>();

    
    public String matrixmult(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);

        ManagedChannel channel1 = ManagedChannelBuilder.forAddress("34.69.115.14", 9090).usePlaintext().build();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub1 = MatrixServiceGrpc.newBlockingStub(channel1);

        ManagedChannel channel2 = ManagedChannelBuilder.forAddress("35.239.25.79", 9090).usePlaintext().build();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub2 = MatrixServiceGrpc.newBlockingStub(channel2);

        ManagedChannel channel3 = ManagedChannelBuilder.forAddress("34.72.111.78", 9090).usePlaintext().build();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub3 = MatrixServiceGrpc.newBlockingStub(channel3);

        ManagedChannel channel4 = ManagedChannelBuilder.forAddress("35.225.78.141", 9090).usePlaintext().build();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub4 = MatrixServiceGrpc.newBlockingStub(channel4);

        ManagedChannel channel5 = ManagedChannelBuilder.forAddress("35.238.63.82", 9090).usePlaintext().build();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub5 = MatrixServiceGrpc.newBlockingStub(channel5);

        ManagedChannel channel6 = ManagedChannelBuilder.forAddress("130.211.121.89", 9090).usePlaintext().build();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub6 = MatrixServiceGrpc.newBlockingStub(channel6);

        ManagedChannel channel7 = ManagedChannelBuilder.forAddress("34.71.104.143", 9090).usePlaintext().build();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub7 = MatrixServiceGrpc.newBlockingStub(channel7);

        ManagedChannel channel8 = ManagedChannelBuilder.forAddress("35.232.22.243", 9090).usePlaintext().build();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub8 = MatrixServiceGrpc.newBlockingStub(channel8);

        String sMatrixA = "";
        for(int i=0; i<MatrixA.size(); i++){
            sMatrixA += MatrixA.get(i) + " ";
        }

        String sMatrixB = "";
        for(int i=0; i<MatrixB.size(); i++){
            sMatrixB += MatrixB.get(i) + " ";
        }

        List<String> splitStringColumnA;
        List<String> splitStringColumnB;

        int columnSize= 0;
        boolean equalColumns = false;

        if(MatrixA.size() == MatrixB.size()){
            for(int i=0; i<MatrixA.size(); i++){
			    for(int j=0; j<MatrixB.size();j++){
					splitStringColumnA = Arrays.asList((MatrixA.get(i)).split(" "));
					splitStringColumnB = Arrays.asList((MatrixB.get(j)).split(" "));

					if(splitStringColumnA.size()==splitStringColumnB.size()){
						columnSize = splitStringColumnA.size();
					}
					else{
						return "Error! These columns of the matrices A and B are not equal.";
					}
				}           
            }

			int rows = ((MatrixA.size() + MatrixB.size())/2);
			int columns = columnSize;
			int MAX = rows;
			int A[][]= new int[MAX][MAX];
			int B[][]= new int[MAX][MAX];
            
			
			List<String> splitStringA = Arrays.asList(sMatrixA.split(" "));	
			List<String> splitStringB = Arrays.asList(sMatrixB.split(" "));

            int count = 0;
                        
            for (int i = 0; i<A.length; i++){
                for(int j=0; j<A[i].length; j++){
                    try{
                        A[i][j] = Integer.parseInt(splitStringA.get(count));
                        B[i][j] = Integer.parseInt(splitStringB.get(count));
                        count+=1;
                    } catch(NumberFormatException e){
                        System.out.print("Unable to parse strings into int.");
                        }
                }
            }

            long startTime = System.nanoTime();
            long endTime = System.nanoTime();
            long footprint = endTime - startTime;

            MatrixReply footprintMultiply = stub.multiplyBlock(MatrixRequest.newBuilder().setA(100).setB(100).build());
            MatrixReply footprintAdd = stub.addBlock(MatrixRequest.newBuilder().setA(100).setB(footprintMultiply.getC()).build());

            int footprintC = footprintAdd.getC();
            channel.shutdown();

            int calls = 0;
			int C[][]= new int[MAX][MAX];
			for(int i=0;i<MAX;i++){
				for(int j=0;j<MAX;j++){
					for(int k=0;k<MAX;k++){
						calls++;
					}
				}
			}

            int requiredNoOfServers = 1+(int)(Math.round((footprint*calls)/(deadline*1000000000.0)));
            int NoOfcallsPerServer = calls/requiredNoOfServers;

            int currentCallNo = 0;

            if(requiredNoOfServers == 1){
				for(int i=0;i<MAX;i++){
					for(int j=0;j<MAX;j++){
						C[i][j]=0;
						for(int k=0;k<MAX;k++){
							MatrixReply multiplyM = stub1.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
							MatrixReply addM = stub1.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
							C[i][j]= addM.getC();
						}
					}
				}
			}

			else if(requiredNoOfServers==2){
				for(int i=0;i<MAX;i++){
					for(int j=0;j<MAX;j++){
						C[i][j]=0;
						for(int k=0;k<MAX;k++){
							MatrixReply multiplyM;
							MatrixReply addM;
							if(currentCallNo<=(NoOfcallsPerServer*1)){//server 1
								multiplyM = stub1.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub1.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();

							}
							else if((currentCallNo>(NoOfcallsPerServer*1)) && (currentCallNo<=(NoOfcallsPerServer*2))){//server 2
								multiplyM = stub2.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub2.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							currentCallNo++;
						}
					}
				}
			}

            
			else if(requiredNoOfServers==3){
				for(int i=0;i<MAX;i++){
					for(int j=0;j<MAX;j++){
						C[i][j]=0;
						for(int k=0;k<MAX;k++){
							MatrixReply multiplyM;
							MatrixReply addM;
							if(currentCallNo<=(NoOfcallsPerServer*1)){//server 1
								multiplyM = stub1.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub1.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*1)) && (currentCallNo<=(NoOfcallsPerServer*2))){
								multiplyM = stub2.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub2.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*2)) && (currentCallNo<=(NoOfcallsPerServer*3))){
								multiplyM = stub3.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub3.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							currentCallNo++;
						}
					}
				}
			}

			else if(requiredNoOfServers==4){
				for(int i=0;i<MAX;i++){
					for(int j=0;j<MAX;j++){
						C[i][j]=0;
						for(int k=0;k<MAX;k++){
							MatrixReply multiplyM;
							MatrixReply addM;
							if(currentCallNo<=(NoOfcallsPerServer*1)){
								multiplyM = stub1.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub1.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*1)) && (currentCallNo<=(NoOfcallsPerServer*2))){
								multiplyM = stub2.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub2.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*2)) && (currentCallNo<=(NoOfcallsPerServer*3))){
								multiplyM = stub3.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub3.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*3)) && (currentCallNo<=(NoOfcallsPerServer*4))){
								multiplyM = stub4.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub4.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							currentCallNo++;
						}
					}
				}
			}

			else if(requiredNoOfServers==5){
				for(int i=0;i<MAX;i++){
					for(int j=0;j<MAX;j++){
						C[i][j]=0;
						for(int k=0;k<MAX;k++){
							MatrixReply multiplyM;
							MatrixReply addM;
							if(currentCallNo<=(NoOfcallsPerServer*1)){
								multiplyM = stub1.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub1.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*1)) && (currentCallNo<=(NoOfcallsPerServer*2))){
								multiplyM = stub2.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub2.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*2)) && (currentCallNo<=(NoOfcallsPerServer*3))){
								multiplyM = stub3.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub3.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*3)) && (currentCallNo<=(NoOfcallsPerServer*4))){
								multiplyM = stub4.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub4.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*4)) && (currentCallNo<=(NoOfcallsPerServer*5))){
								multiplyM = stub5.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub5.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							currentCallNo++;
						}
					}
				}
			}
			else if(requiredNoOfServers==6){
				for(int i=0;i<MAX;i++){
					for(int j=0;j<MAX;j++){
						C[i][j]=0;
						for(int k=0;k<MAX;k++){
							MatrixReply multiplyM;
							MatrixReply addM;
							if(currentCallNo<=(NoOfcallsPerServer*1)){
								multiplyM = stub1.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub1.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*1)) && (currentCallNo<=(NoOfcallsPerServer*2))){
								multiplyM = stub2.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub2.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*2)) && (currentCallNo<=(NoOfcallsPerServer*3))){
								multiplyM = stub3.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub3.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*3)) && (currentCallNo<=(NoOfcallsPerServer*4))){
								multiplyM = stub4.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub4.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*4)) && (currentCallNo<=(NoOfcallsPerServer*5))){
								multiplyM = stub5.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub5.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*5)) && (currentCallNo<=(NoOfcallsPerServer*6))){
								multiplyM = stub6.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub6.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							currentCallNo++;
						}
					}
				}
			}    
            
			else if(requiredNoOfServers==7){
				for(int i=0;i<MAX;i++){
					for(int j=0;j<MAX;j++){
						C[i][j]=0;
						for(int k=0;k<MAX;k++){
							MatrixReply multiplyM;
							MatrixReply addM;
							if(currentCallNo<=(NoOfcallsPerServer*1)){
								multiplyM = stub1.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub1.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*1)) && (currentCallNo<=(NoOfcallsPerServer*2))){
								multiplyM = stub2.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub2.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*2)) && (currentCallNo<=(NoOfcallsPerServer*3))){
								multiplyM = stub3.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub3.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*3)) && (currentCallNo<=(NoOfcallsPerServer*4))){
								multiplyM = stub4.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub4.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*4)) && (currentCallNo<=(NoOfcallsPerServer*5))){
								multiplyM = stub5.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub5.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*5)) && (currentCallNo<=(NoOfcallsPerServer*6))){
								multiplyM = stub6.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub6.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*6)) && (currentCallNo<=(NoOfcallsPerServer*7))){
								multiplyM = stub7.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub7.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							currentCallNo++;
						}
					}
				}
			}

			else if(requiredNoOfServers>=8){
				for(int i=0;i<MAX;i++){
					for(int j=0;j<MAX;j++){
						C[i][j]=0;
						for(int k=0;k<MAX;k++){
							MatrixReply multiplyM;
							MatrixReply addM;
							if(currentCallNo<=(NoOfcallsPerServer*1)){
								multiplyM = stub1.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub1.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*1)) && (currentCallNo<=(NoOfcallsPerServer*2))){
								multiplyM = stub2.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub2.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*2)) && (currentCallNo<=(NoOfcallsPerServer*3))){
								multiplyM = stub3.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub3.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*3)) && (currentCallNo<=(NoOfcallsPerServer*4))){
								multiplyM = stub4.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub4.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*4)) && (currentCallNo<=(NoOfcallsPerServer*5))){
								multiplyM = stub5.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub5.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*5)) && (currentCallNo<=(NoOfcallsPerServer*6))){
								multiplyM = stub6.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub6.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else if((currentCallNo>(NoOfcallsPerServer*6)) && (currentCallNo<=(NoOfcallsPerServer*7))){
								multiplyM = stub7.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub7.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							else{
								multiplyM = stub8.multiplyBlock(MatrixRequest.newBuilder().setA(A[i][k]).setB(B[k][j]).build());
								addM = stub8.addBlock(MatrixRequest.newBuilder().setA(C[i][j]).setB(multiplyM.getC()).build());
								C[i][j]= addM.getC();
							}
							currentCallNo++;
						}
					}
				}
			} 

            else{
                return "Error! Unexpected Error!";
            }

            channel1.shutdown();
            channel2.shutdown();
            channel3.shutdown();
            channel4.shutdown();
            channel5.shutdown();
            channel6.shutdown();
            channel7.shutdown();
            channel8.shutdown();

            String sData = "Matrix Result: " + Arrays.deepToString(C) + ", ";
            sData += "The required number of servers: " + requiredNoOfServers + " and ";

            sData += "The number of calls per server: " + NoOfcallsPerServer;

            return sData;
        }

        else{
            return "Error! The number of rows for matrices A and B are unequal.";
        }

    }

    public void uploadFile(MultipartFile[] file){
        for(MultipartFile f : file){
            if (f.isEmpty()){
                throw new FileStorageException("Failed to upload due to empty file");
            }
            else{
                try{
                    String filename = f.getOriginalFilename();
                    Filename.add(filename);

                    InputStream i = f.getInputStream();

                    Files.copy(i, Paths.get(path + filename), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e){
                    String message = String.format("Failed to upload file %f", f.getName());

                    throw new FileStorageException(message, e);
                }
            }
        }
    }

    public void checkFiles(){
        for (int i=0; i<Filename.size(); i++){
            readFile(Filename.get(i));
        }
    }

    public ArrayList<String> getMatrixA(){
        return MatrixA;
    }

    public ArrayList<String> getMatrixB(){
        return MatrixB;
    }

    public void setDeadline(int Deadline){
        deadline = Deadline;
    }

    //method to read files
    public void readFile(String filename){
        String filePath = (Paths.get(path + filename)).toString();

        File f = null;
        Scanner sc = null;
        try {
            f = new File(filePath);
            sc = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }

        if(MatrixA.size()==0){
            while(sc.hasNextLine()){
                MatrixA.add(sc.nextLine());
            }
        }
        else{
            while(sc.hasNextLine()){
                MatrixB.add(sc.nextLine());
            }
        }
    }
}
