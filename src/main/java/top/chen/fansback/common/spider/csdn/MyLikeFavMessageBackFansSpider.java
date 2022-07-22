package top.chen.fansback.common.spider.csdn;

import top.chen.fansback.common.cmd.CsdnRequest;
import top.chen.fansback.common.spider.BasePageProcessor;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.selector.JsonPathSelector;

/**
 * 点赞收藏消息-查询后返回三连
 * 1. 定时多久扫描我的消息栏（前提登录），看是否有点赞、关注的信息
 * 2. 有的话，则顺着用户id去查看用户的第一篇文章，给他返回一个关注、点赞、评论，记录一下到本地
 * 3. 如果已经返过了，那就不用返了
 * 4. 记录的逻辑是：关注：关注人-被关注人唯一；点赞人-文章id唯一、收藏人-文章id唯一；
 *
 * @author chenchao
 */
//public class MyLikeFavMessageBackFansSpider extends BasePageProcessor {

//	public static void main(String[] args) {
//
//
//		MyLikeFavMessageBackFansSpider spider = new MyLikeFavMessageBackFansSpider();
//		Spider.create(spider)
//				.addRequest(CsdnRequest.getMyLikeFavMessageRequest(1)).run();
//	}
//
//	@Override
//	public void process(Page page) {
//		System.out.println(page.getRawText());
//		if (page.getUrl().toString().startsWith(CsdnRequest.MY_LIKE_FAV_MESSAGE)) {
//			// 是不是翻页到最后了，没到最后再下一页
////			page.addTargetRequest(page.get);
//		}
//		// 记录一下到本地
//
//		page.putField("title", new JsonPathSelector("$.data.title").select(page.getRawText()));
//	}
//
//	@Override
//	public void run() {
//		Spider.create(new MyLikeFavMessageBackFansSpider())
//				.addUrl(CsdnRequest.MY_LIKE_FAV_MESSAGE).run();
//	}
//}
