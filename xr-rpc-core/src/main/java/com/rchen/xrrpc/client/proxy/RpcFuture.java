package com.rchen.xrrpc.client.proxy;

import com.rchen.xrrpc.exception.RpcFailureException;
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
        long useTime = System.currentTimeMillis() - startTime;
        log.info("RPC 请求[id={}] 执行完成，共计耗时 [{} ms]", res.getRequestId(), useTime);
        log.debug("具体结果: {}", res.toString());

        response = res;
        countDownLatch.countDown();

        // 异步回调
        if (callback != null) {
            if (response.isSuccess()) {
                callback.success(response.getResult());
            } else {
                callback.fail(response.getException());
            }
        }
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
        if (response.isSuccess()) {
            /**
             * 调用成功
             */
            return response.getResult();
        } else {
            /**
             * 调用失败
             */
            throw new RpcFailureException(response.getException().getCause());
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
