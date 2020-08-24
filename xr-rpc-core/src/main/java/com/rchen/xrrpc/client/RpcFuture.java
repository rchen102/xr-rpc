package com.rchen.xrrpc.client;

import com.rchen.xrrpc.protocol.response.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author : crz
 * @Date: 2020/8/22
 */
@Slf4j
public class RpcFuture implements Future<Object> {

    /**
     * 计数器
     */
    private CountDownLatch countDownLatch;

    /**
     * Optional 等待执行的 callback
     */
    private AsyncRpcCallback callback;

    private String requestId;
    private RpcResponse response;
    private long startTime;

    public RpcFuture(String requestId) {
        this.requestId = requestId;
        countDownLatch = new CountDownLatch(1);
        this.startTime = System.currentTimeMillis();
    }

    public RpcFuture(String requestId, AsyncRpcCallback callback) {
        this(requestId);
        this.callback = callback;
    }

    public void done(RpcResponse res) {
        response = res;
        countDownLatch.countDown();
        if (callback != null) {
            if (response.isSuccess()) {
                callback.success(response.getResult());
            } else {
                // TODO 自定义调用失败的 Exception
                callback.fail(response.getException());
            }
        }
        long useTime = System.currentTimeMillis() - startTime;
        log.info("Request[id={}] is done, use time [{} ms]", response.getRequestId(), useTime);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return response != null;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        countDownLatch.await();
        return response.getResult();
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
