package com.example.grpc.server.grpcserver;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.*; 

@GrpcService
public class MatrixServiceImpl extends MatrixServiceGrpc.MatrixServiceImplBase
{
	
	@Override
	public void addBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
	{
		System.out.println("Request received from client:\n" + request);
		int C = request.getA()+request.getB();
		
		MatrixReply response = MatrixReply.newBuilder().setC(C).build();
		reply.onNext(response);
		reply.onCompleted();
	}
	
	@Override
        public void multiplyBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
        {
			System.out.println("Request received from client:\n" + request);
			int C = request.getA()* request.getB();
			
			MatrixReply response = MatrixReply.newBuilder().setC(C).build();
			reply.onNext(response);
			reply.onCompleted();
        }


}