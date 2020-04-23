package cn.ddlover.im.business;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/23 21:59
 * 重连策略的接口定义
 */
public interface ReconnectPolicy {

  /**
   * Called when an operation has failed for some reason. This method should return
   * true to make another attempt.
   *
   * @param retryCount the number of times retried so far (0 the first time)
   * @return true/false
   */
  boolean allowRetry(int retryCount);

  /**
   * get sleep time in ms of current retry count.
   *
   * @param retryCount current retry count
   * @return the time to sleep
   */
  long getSleepTimeMs(int retryCount);
}
