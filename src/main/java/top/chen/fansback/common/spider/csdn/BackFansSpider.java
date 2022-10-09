package top.chen.fansback.common.spider.csdn;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.chen.fansback.common.BackProperties;
import top.chen.fansback.common.cmd.CsdnRequest;
import top.chen.fansback.common.spider.csdn.db.UniqDatasource;
import top.chen.fansback.common.spider.csdn.dto.home.ArticleList;
import top.chen.fansback.common.spider.csdn.dto.message.Content;
import top.chen.fansback.common.spider.csdn.dto.message.CsdnResponse;
import top.chen.fansback.common.spider.csdn.dto.message.ResultList;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import top.chen.fansback.common.spider.csdn.dto.home.PrivateMessage;

/**
 * 1. 定时多久扫描我的消息栏（前提登录），看是否有点赞、关注的信息
 * 2. 有的话，则顺着用户id去查看用户的第一篇文章，给他返回一个关注、点赞、评论，记录一下到本地
 * 3. 如果已经返过了，那就不用返了
 * 4. 记录的逻辑是：关注：关注人-被关注人唯一；点赞人-文章id唯一、收藏人-文章id唯一；
 *
 * @author chenchao
 */
@Slf4j
public class BackFansSpider {

	static boolean BACK_COMMENT = true;
	static boolean BACK_FOLLOW = false;
	static boolean BACK_LIKE_ARTICLE = false;
	static boolean LIKE_COMMENT = false;
	static boolean FLAG = true;
	static {
		UniqDatasource.exists("");
	}

	static void runCommentBack() {
		if (!BACK_COMMENT) {
			return;
		}
		HttpResponse execute = CsdnRequest.getRequestCommentMessageList(1).execute();
		log.debug("获取评论消息：{}", execute.body());
		CsdnResponse csdnResponse = JSON.parseObject(execute.body(), CsdnResponse.class);
		for (ResultList list : csdnResponse.getData().getResultList()) {
			parseComment(list);
		}
	}

	static void runFollowBack() {
		if (!BACK_FOLLOW) {
			return;
		}

	}

	static void run() {
		runCommentBack();
		runLikeFavBack();
		
		/**
		 * 私信消息处理
		 * 有未读私信 给此人最新一条消息三联支持 且私信告知
		 */
		queryPrivateMessage();
	}

	private static void runLikeFavBack() {
		int index = 1;
//		for (int index = 1; ; index++) {
		HttpResponse execute1 = CsdnRequest.getRequestLikeFavMessageList(index).execute();
		CsdnResponse csdnResponse = JSON.parseObject(execute1.body(), CsdnResponse.class);
//			if (csdnResponse.getData().getCountNum() <= index * CsdnRequest.MESSAGE_PAGE_SIZE) {
//				break;
//			}
		for (ResultList list : csdnResponse.getData().getResultList()) {
			parseLikeFav(list);
		}

//		}
	}

	@SneakyThrows
	private static void parseLikeFav(ResultList list) {
		Content content = list.getContent();
		for (String name : content.getUniqUserName()) {
			String format = String.format("%s_%s", list.getId(), name);
			if (UniqDatasource.exists(format)) {
				log.debug("当前消息{} ， {} 已处理，跳过", list.getId(), name);
				continue;
			}
			log.debug("当前消息{}，返回粉丝中", list);
			//   找文章主页前n个文章
			List<ArticleList> lastList = CsdnRequest.getUserArticleList(name, false, 10);
			if (CollUtil.isEmpty(lastList)) {
				log.debug("当前用户没有文章：{}", name);
			}
			if (content.getTt().contains("收藏了")) {
				for (ArticleList url : lastList) {
					// task 是否处理，已处理完成
					// 未处理，查
					boolean success = CsdnRequest.postAddFav(url.getUrl(), name, url.getTitle(), url.getDescription());
					if (success) {
						UniqDatasource.save(format);
						break;
					}
				}
				TimeUnit.SECONDS.sleep(3);
			} else if (content.getTt().contains("点赞了")) {
				if (CsdnRequest.toDayLikeLimit()) {
					continue;
				}
				for (ArticleList articleList : lastList) {
					if (CsdnRequest.toDayLikeLimit()) {
						break;
					}
					if (CsdnRequest.postLikeArticle(articleList.getUrl())) {
						UniqDatasource.save(format);
						break;
					}

				}
				TimeUnit.SECONDS.sleep(3);
			} else {
				log.info("parseLikeFav出现了新的类型,{}", list);
			}
		}
	}


	private static void parseComment(ResultList list) {
		Content content = list.getContent();
		for (String name : content.getUniqUserName()) {
			String format = String.format("%s_%s", list.getId(), name);
			if (UniqDatasource.exists(format)) {
				log.info("当前消息{} ， {} 已处理，跳过", list.getId(), name);
				continue;
			}
			if (CsdnRequest.ARTICLE_COMMENT_LIMIT) {
				log.warn("评论被限制，先跳过，限制解除后再继续回馈");
				continue;
			}
			//   找文章主页前n个文章
			List<ArticleList> lastList = CsdnRequest.getUserArticleList(name, false, 1);
			List<ArticleList> popularList = CsdnRequest.getUserArticleList(name, true, 3);
			Set<String> articleUrl = Stream.of(lastList, popularList).flatMap(Collection::stream)
					.map(ArticleList::getUrl)
					.collect(Collectors.toSet());
			for (String url : articleUrl) {
				if (CsdnRequest.postComment(RandomUtil.randomEle(BackProperties.replayCommentArr, BackProperties.replayCommentArr.length - 1), url)) {
					UniqDatasource.save(format);
					break;
				}
				if (CsdnRequest.toDayCommentLimit()){
					break;
				}
				if (CsdnRequest.ARTICLE_COMMENT_LIMIT) {
					break;
				}
			}
		}
	}

	
	/**
	 * 私信消息处理
	 * 有未读私信 给此人最新一条消息三联支持 且私信告知
	 */
	@SneakyThrows
	static void queryPrivateMessage(){
		List<PrivateMessage> privateMessageList =CsdnRequest.getRequestPrivateMessageList(1,10000);
		for (PrivateMessage privateMessage : privateMessageList) {
			if (privateMessage.getUnReadCount() >0) {
				log.debug("未读私信消息处理：{} ， 消息内容{}", privateMessage.getNickname(),privateMessage.getContent());
				// 点赞 收藏
				// 找文章主页前n个文章
				String name = privateMessage.getUsername();
				List<ArticleList> lastList = CsdnRequest.getUserArticleList(name, false, 10);
				if (CollUtil.isEmpty(lastList)) {
					log.debug("当前用户没有文章：{}", name);
				}else{
					//收藏 点赞
					String format = "";
					for (ArticleList url : lastList) {
						format = String.format("%s_%s", url.getArticleId(), name);
						if (UniqDatasource.exists(format)) {
							log.debug("当前消息{} ， {} 已处理，跳过",url.getArticleId(), name);
							continue;
						}
						// task 是否处理，已处理完成
						// 未处理，查
						boolean success = CsdnRequest.postAddFav(url.getUrl(), name, url.getTitle(), url.getDescription());
						boolean b = CsdnRequest.postLikeArticle(url.getUrl());
						log.debug("点赞收藏已处理完成！！！用户为：{}", privateMessage.getNickname());
						//评论
						if (CsdnRequest.ARTICLE_COMMENT_LIMIT) {
							log.warn("评论被限制，先跳过，限制解除后再继续回馈");
							continue;
						}
						if (CsdnRequest.postComment(url.getTitle(), url.getUrl())) {
							log.debug("文章评论已处理完成 ！！！用户为：{}，文章为:{}", privateMessage.getNickname(),url);
							UniqDatasource.save(format);
							break;
						}
					}
					TimeUnit.SECONDS.sleep(3);
					//私信内容
					String message = "已经优质三联支持,期待您的回访";
					//每个账号唯一的,需要发消息时抓包获取
					String fromDeviceId= "";
					boolean sendPrivateMessageRes = CsdnRequest.sendPrivateMessage(name, "0", message, "WEB", fromDeviceId, "CSDN-PC");
					if (sendPrivateMessageRes) {
						log.debug("私信已处理完成 ！！！用户为：{}，私信内容为:{}", privateMessage.getNickname(),message);
					}
				}
			}else{
				log.debug("用户昵称：{} ， 没有未读私信消息!!!", privateMessage.getNickname());
			}
		}
		log.debug("-------------------------私信消息处理结束-------------------------");
		FLAG = false;
	}
	
	@SneakyThrows
	public static void main(String[] args) {
		while (true) {
			try {
				if (!FLAG) {
					log.debug("------------------------------------------程序结束--------------------------------------------------------");
					return;
				}
				run();
			} catch (Exception e) {
				log.error("发送异常：", e);
				TimeUnit.MINUTES.sleep(4);
			}
			TimeUnit.MINUTES.sleep(1);
		}
	}
}
