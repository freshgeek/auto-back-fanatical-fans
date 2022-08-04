package top.chen.fansback.common.util;

import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 微信接龙解析工具
 *
 * @author chenchao
 * @date 2022-08-03 17:14:18
 */
public class WechatSolitaireParser {

    private static Pattern ARTICLE_PATTERN = Pattern.compile("http.*[0-9,a-z,A-Z]");

    public static List<String> parseArticleUrl(String text) {
        if (StrUtil.isEmpty(text)) {
            return Collections.emptyList();
        }
        Function<String, String> regExtra = s -> {
            Matcher matcher = ARTICLE_PATTERN.matcher(s);
            if (matcher.find()){
                return matcher.group();
            }
            return null;
        };
        return Arrays.asList(text.split("\n")).stream()
                .filter(StrUtil::isNotEmpty)
                .map(regExtra).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
