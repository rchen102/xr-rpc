package com.rchen.xrrpc.client.proxy;

import com.rchen.xrrpc.exception.RpcFailureException;
import com.rchen.xrrpc.exception.RpcTimeoutException;
import com.rchen.xrrpc.protocol.response.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
    public Object get() throws InterruptedException {
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
    public Object get(long timeout, TimeUnit unit) {
        boolean awaitSuccess = false;
        try {
            awaitSuccess = countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!awaitSuccess) {
            log.error("服务器未响应，请稍后再试!");
            throw new RpcTimeoutException("服务器未响应，请稍后再试!");
        }
        if (callback != null) { // 异步调用
            if (response.isSuccess()) {
                callback.success(response.getResult());
            } else {
                callback.fail(response.getException());
            }
            return null;
        }
        else { // 同步调用
            if (response.isSuccess()) {
                return response.getResult();
            } else {
                log.error("RPC 调用失败!");
                throw new RpcFailureException(response.getException().getCause());
            }
        }
    }
}
