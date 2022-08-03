package top.chen.fansback.common.spider.csdn;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import top.chen.fansback.common.BackProperties;
import top.chen.fansback.common.cmd.CsdnRequest;
import top.chen.fansback.common.util.LinkUtil;
import top.chen.fansback.common.util.WechatSolitaireParser;

import java.util.List;
import java.util.Optional;

/**
 * @author chenchao
 * @date 2022-08-03 17:12:51
 */
@Slf4j
public class ActiveSolitaireSpider extends BackFansSpider {

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

        if (BACK_COMMENT){
            for (String url : articleUrl) {
                CsdnRequest.postComment(RandomUtil.randomEle(BackProperties.replayCommentArr, BackProperties.replayCommentArr.length - 1), url);
                if (CsdnRequest.ARTICLE_COMMENT_LIMIT) {
                    break;
                }
            }
        }
        if (BACK_LIKE_ARTICLE){

        }
    }

}
