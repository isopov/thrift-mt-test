package com.sopovs.moradanen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.THttpClient;

import com.sopovs.moradanen.thrift.ThriftService;
import com.sopovs.moradanen.thrift.ThriftService.Client;

public class SingleHttpClientTest {
	public static void main(String[] args) throws Exception {
		JettyServer server = new JettyServer();
		server.start();

		THttpClient transport = new THttpClient("http://localhost:8080/", new DefaultHttpClient(
				new ThreadSafeClientConnManager()));
		TBinaryProtocol protocol = new TBinaryProtocol(transport);
		Client client = new ThriftService.Client(protocol);

		ExecutorService executor = Executors.newFixedThreadPool(Utils.NUM_THREADS);
		for (int i = 0; i < Utils.NUM_THREADS; i++) {
			executor.execute(new ServiceClientWorker(client));
		}
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);

		server.stop();
	}

	private static class ServiceClientWorker implements Runnable {

		private final Client client;

		public ServiceClientWorker(Client client) {
			this.client = client;
		}

		@Override
		public void run() {
			try {
				for (int i = 0; i < Utils.NUM_ITERATIONS; i++) {
					String hello = client.sayHello();
					if (!Utils.MESSAGE_STRING.equals(hello)) {
						System.out.println("Wrong message!");
					}
				}
			} catch (Exception e) {
				System.out.println("Exception during communication!");
			}

		}

	}

}
