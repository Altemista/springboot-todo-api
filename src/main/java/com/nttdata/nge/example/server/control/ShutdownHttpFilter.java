package com.nttdata.nge.example.server.control;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Counts the active HTTP threads. Returns 503 if shutdown is enabled
 */
@Component
@Lazy
public class ShutdownHttpFilter implements Filter {
    
    private static final AtomicLongFieldUpdater<ShutdownHttpFilter> activeRequestsUpdater = AtomicLongFieldUpdater
            .newUpdater(ShutdownHttpFilter.class, "activeRequests");
    @SuppressWarnings("unused")
    private volatile long activeRequests;
    private volatile boolean shutdown;
    private CountDownLatch latch;
    
    protected final Log log = LogFactory.getLog(getClass());
    
    private int shutdownTimeout = 60000;
    
    /**
     * Processes the requests. Return HTTP 503 if shutdown enabled, or count the
     * requests and process.
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (shutdown) {
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "SpringBoot shutting down");
        } else {
            // Service running, count request and process
            activeRequestsUpdater.incrementAndGet(this);
            try {
                chain.doFilter(request, response);
            } finally {
                if (shutdown) {
                    getCountDownLatch().countDown();
                }
                activeRequestsUpdater.decrementAndGet(this);
            }
        }
    }
    
    /**
     * Creates and/or returns the CountDownLatch.
     * 
     * @return the latch
     */
    private synchronized CountDownLatch getCountDownLatch() {
        if (latch == null) {
            try {
                latch = new CountDownLatch(
                        (int) activeRequestsUpdater.get(this));
            } catch (IllegalArgumentException e) {
                log.debug(e.getMessage());
                latch = new CountDownLatch(0);
            }
        }
        return latch;
    }
    
    /**
     * Enables the shutdown logic. All upcoming requests will be rejected with
     * HTTP 503 / Waits until all running requests are finished
     * 
     * @throws InterruptedException
     *             throws an interrupted exception
     */
    public void shutdown() throws InterruptedException {
        this.shutdown = true;
        boolean allFinished = getCountDownLatch().await(shutdownTimeout,
                TimeUnit.MILLISECONDS);
        if (!allFinished) {
            // no further proccessing or hard canceling. The threads could still
            // finish during shutdown.
            log.info(
                    "Shutdown - Not all http threads finished during timeout. They will be canceled during shutdown.");
        }
        
    }
    
    /**
     * Register the Filter with SpringBoot FilterRegistrationBean
     * 
     * @return the filter
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Bean
    public FilterRegistrationBean myFilterBean() {
        final FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
        filterRegBean.setFilter(this);
        filterRegBean.addUrlPatterns("/*");
        filterRegBean.setEnabled(Boolean.TRUE);
        filterRegBean.setName("Graceful HTTP Filter");
        filterRegBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegBean.setAsyncSupported(Boolean.TRUE);
        return filterRegBean;
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing
    }
    
    @Override
    public void destroy() {
        // nothing
    }
    
}