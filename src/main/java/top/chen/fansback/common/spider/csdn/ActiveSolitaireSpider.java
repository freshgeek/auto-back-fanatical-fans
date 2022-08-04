package top.chen.fansback.common.spider.csdn;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.chen.fansback.common.BackProperties;
import top.chen.fansback.common.cmd.CsdnRequest;
import top.chen.fansback.common.spider.csdn.dto.home.ArticleList;
import top.chen.fansback.common.util.LinkUtil;
import top.chen.fansback.common.util.WechatSolitaireParser;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author chenchao
 * @date 2022-08-03 17:12:51
 */
@Slf4j
public class ActiveSolitaireSpider extends BackFansSpider {

    @SneakyThrows
    public static void main(String[] args) {
        // 优先入参
        String text = Optional.ofNullable(args)
                .filter(a -> a.length > 0).map(s -> s[0]).orElse("");
        // 解析url
        List<String> parseArticleUrl = WechatSolitaireParser.parseArticleUrl(text);

        if (CollUtil.isEmpty(parseArticleUrl)) {
            return;
        }

        // 解析长链
        List<String> articleUrl = LinkUtil.getURILinks(LinkUtil.getLongerLink(parseArticleUrl));

        // 评论
        for (String url : articleUrl) {
            CsdnRequest.postComment(RandomUtil.randomEle(BackProperties.replayCommentArr, BackProperties.replayCommentArr.length - 1), url);
            if (CsdnRequest.toDayCommentLimit()) {
                break;
            }
            if (CsdnRequest.ARTICLE_COMMENT_LIMIT) {
                break;
            }
        }

        // 点赞
        for (String url : articleUrl) {
            if (!CsdnRequest.postLikeArticle(url)) {
                continue;
            }
            if (CsdnRequest.toDayLikeLimit()) {
                break;
            }
            TimeUnit.SECONDS.sleep(3);
        }

        // 收藏
        for (String url : articleUrl) {
            // task 是否处理，已处理完成
            // 未处理，查
            ArticleList articleList = CsdnRequest.extraDetail(url);
            if (articleList == null
                    || StrUtil.isEmpty(articleList.getAuthor())
                    || StrUtil.isEmpty(articleList.getTitle())
                    || StrUtil.isEmpty(articleList.getDescription())
            ) {
                log.error("注意extraDetail 抽取详情异常：{} , {}", url, articleList);
                continue;
            }
            if (!CsdnRequest.postAddFav(url, articleList.getAuthor(), articleList.getTitle(), articleList.getDescription())){
                continue;
            }
            TimeUnit.SECONDS.sleep(3);
        }

        // 关注 todo

    }

}
