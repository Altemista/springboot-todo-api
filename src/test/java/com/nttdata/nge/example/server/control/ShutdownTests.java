package com.nttdata.nge.example.server.control;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

import com.nttdata.nge.example.server.NgeExampleServer;

@SpringBootTest(classes = NgeExampleServer.class)
public class ShutdownTests extends AbstractTestNGSpringContextTests {

	@Autowired
	private ShutdownHook shutdownHook;

	@Autowired
	private ShutdownHttpFilter httpFilter;

	@Mock
	private ConfigurableApplicationContext applicationContext;

	@Mock
	private ServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain chain;

	@BeforeClass
	public void init() {
		MockitoAnnotations.initMocks(this);
		shutdownHook.init(applicationContext);
	}

	@Test
	public void shutdownFilter() throws IOException, ServletException {

		httpFilter.doFilter(request, response, chain);
		verify(chain, atLeast(1)).doFilter(request, response);
		shutdownHook.run();

		httpFilter.doFilter(request, response, chain);
		verify(response, atLeast(1)).sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "SpringBoot shutting down");
	}

}
