package com.sopovs.moradanen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportFactory;

import com.sopovs.moradanen.thrift.ThriftService;
import com.sopovs.moradanen.thrift.ThriftService.Client;

public class FactoryHttpClientTest {
	public static void main(String[] args) throws Exception {
		JettyServer server = new JettyServer();
		server.start();

		TTransportFactory transportFactory = new THttpClient.Factory("http://localhost:8080/", new DefaultHttpClient(
				new ThreadSafeClientConnManager()));
		TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();

		ExecutorService executor = Executors.newFixedThreadPool(Utils.NUM_THREADS);
		for (int i = 0; i < Utils.NUM_THREADS; i++) {
			executor.execute(new ServiceClientWorker(protocolFactory, transportFactory));
		}
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);

		server.stop();
	}

	private static class ServiceClientWorker implements Runnable {

		private final TProtocolFactory protocolFactory;
		private final TTransportFactory transportFactory;

		public ServiceClientWorker(TProtocolFactory protocolFactory, TTransportFactory transportFactory) {
			this.protocolFactory = protocolFactory;
			this.transportFactory = transportFactory;
		}

		@Override
		public void run() {
			TProtocol protocol = protocolFactory.getProtocol(transportFactory.getTransport(null));
			Client client = new ThriftService.Client(protocol);
			try {
				for (int i = 0; i < Utils.NUM_ITERATIONS; i++) {
					String hello = client.sayHello();
					if (!Utils.MESSAGE_STRING.equals(hello)) {
						System.out.println("Wrong message!");
					} else {
						System.out.println(Utils.MESSAGE_STRING);
					}
				}
			} catch (Exception e) {
				System.out.println("Exception during communication!");
			}

		}
	}
}
