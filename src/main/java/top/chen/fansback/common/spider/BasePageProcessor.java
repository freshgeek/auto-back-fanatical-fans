package top.chen.fansback.common.spider;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author chenchao
 */
public abstract class BasePageProcessor implements PageProcessor {
	private int retryTimes = 3;
	private int sleepTime = 5000;
	// 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	protected Site site = Site.me().setRetryTimes(retryTimes).setSleepTime(sleepTime);



	@Override
	public Site getSite() {
		return this.site;
	}

	public abstract void run();

}
