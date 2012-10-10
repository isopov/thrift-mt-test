package com.sopovs.moradanen;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sopovs.moradanen.thrift.ThriftService;
import com.sopovs.moradanen.thrift.ThriftService.Iface;
import com.sopovs.moradanen.thrift.ThriftService.Processor;

public class JettyServer {

	private Server server;

	public void start() throws Exception {
		server = new Server(8080);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);

		Processor<Iface> processor = new ThriftService.Processor<Iface>(new ThriftServiceImpl());
		TServlet servlet = new TServlet(processor, new TBinaryProtocol.Factory());

		context.addServlet(new ServletHolder(servlet), "/*");

		server.start();
//		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}

	public static class ThriftServiceImpl implements ThriftService.Iface {

		@Override
		public String sayHello() throws TException {
			return Utils.MESSAGE_STRING;
		}
	}

}
