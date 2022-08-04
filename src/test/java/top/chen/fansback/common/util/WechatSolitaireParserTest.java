package top.chen.fansback.common.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author chenchao
 * @date 2022-08-03 18:34:01
 */
public class WechatSolitaireParserTest {

    @Test
    public void parseArticleUrl_null() {
        List<String> strings =
                WechatSolitaireParser.parseArticleUrl(null);
        Assert.assertEquals(strings, Collections.emptyList());
    }

    @Test
    public void parseArticleUrl_EmptyStr() {
        List<String> strings =
                WechatSolitaireParser.parseArticleUrl("");
        Assert.assertEquals(strings, Collections.emptyList());
    }

    @Test
    public void parseArticleUrl() {
        List<String> strings =
                WechatSolitaireParser.parseArticleUrl("#接龙搞起来！！！\n" +
                        "\n" +
                        "1. Edison 高质量互访在线秒回\uD83C\uDF39http://t.csdn.cn/tABP2\n" +
                        "2. 编程爱好者-阿新 http://t.csdn.cn/K23Mt\n" +
                        "3. 叶落秋白 \n" +
                        "http://t.csdn.cn/XjFbY\n" +
                        "28. 移步 http://t.csdn.cn/OWJTx 三连必回\n");
        Assert.assertEquals(strings, Arrays.asList("http://t.csdn.cn/tABP2", "http://t.csdn.cn/K23Mt", "http://t.csdn.cn/XjFbY", "http://t.csdn.cn/OWJTx"));
    }

    @Test
    public void parseArticleUrl2() {
        List<String> strings =
                WechatSolitaireParser.parseArticleUrl("#接龙\n" +
                        "8月3号\n" +
                        "\n" +
                        "1. redamancy http://t.csdn.cn/QxaEF\n" +
                        "IDEA相关配置\n" +
                        "2. redamancy http://t.csdn.cn/e5gns\n" +
                        "IDEA创建模块\n" +
                        "3. redamancy http://t.csdn.cn/VxDAN\n" +
                        "在hadoop102中安装hadoop\n" +
                        "4. csdn-跳楼梯企鹅 \n" +
                        "https://blog.csdn.net/weixin_50481708/article/details/126132190?spm=1001.2014.3001.5501\n" +
                        "5. AK47 http://t.csdn.cn/3OAZi\n" +
                        "6. if \n" +
                        "http://t.csdn.cn/XjFbY\n" +
                        "Mysql视图索引事务，互奶\n" +
                        "7. 花神庙码农@CSDN \n" +
                        "http://t.csdn.cn/4GOar\n" +
                        "8. 小鹏linux \n" +
                        "\n" +
                        "vip+高等级原力。高权重回访http://t.csdn.cn/BNMM4\n" +
                        "9. Lanson http://t.csdn.cn/4puFR\n" +
                        "互三连，有来必有高质量回访\n" +
                        "10. CSDN—风铃听雨～ http://t.csdn.cn/hJ81w\n" +
                        "新文章，诚信互三\n" +
                        "11. @每天都要敲代码 \n" +
                        "http://t.csdn.cn/1T5G6\n" +
                        "12. \uD83D\uDCAF http://t.csdn.cn/aIG5T\n" +
                        "13. 移步 http://t.csdn.cn/OWJTx  三连必回\n" +
                        "14. promise https://blog.csdn.net/m0_71485750/article/details/125994087 互三有关必回\n" +
                        "15. 渟 http://t.csdn.cn/1eXqf 三连互动[玫瑰]\n" +
                        "16. Yeap http://t.csdn.cn/pumq4 评论区回\n" +
                        "17. 泡泡\n" +
                        "18. CSDN Flyme awei \n" +
                        "http://t.csdn.cn/xDRNx\n" +
                        "19. Redis面试 http://t.csdn.cn/3qfEc 三连必回！\n" +
                        "20. 青 http://t.csdn.cn/QpUnO互三互关在线回\n" +
                        "21. CodeMak1r. http://t.csdn.cn/AamJP互三\n" +
                        "22. CodeMak1r. http://t.csdn.cn/ncg32\n" +
                        "23. http://t.csdn.cn/eqidG");
        Assert.assertEquals(strings, Arrays.asList(
                "http://t.csdn.cn/QxaEF",
                "http://t.csdn.cn/e5gns",
                "http://t.csdn.cn/VxDAN",
                "https://blog.csdn.net/weixin_50481708/article/details/126132190?spm=1001.2014.3001.5501",
                "http://t.csdn.cn/3OAZi",
                "http://t.csdn.cn/XjFbY",
                "http://t.csdn.cn/4GOar",
                "http://t.csdn.cn/BNMM4",
                "http://t.csdn.cn/4puFR",
                "http://t.csdn.cn/hJ81w",
                "http://t.csdn.cn/1T5G6",
                "http://t.csdn.cn/aIG5T",
                "http://t.csdn.cn/OWJTx",
                "https://blog.csdn.net/m0_71485750/article/details/125994087",
                "http://t.csdn.cn/1eXqf",
                "http://t.csdn.cn/pumq4",
                "http://t.csdn.cn/xDRNx",
                "http://t.csdn.cn/3qfEc",
                "http://t.csdn.cn/QpUnO",
                "http://t.csdn.cn/AamJP",
                "http://t.csdn.cn/ncg32",
                "http://t.csdn.cn/eqidG"
        ));
    }
}