package top.chen.fansback.common.util;

import cn.hutool.core.util.StrUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author chenchao
 * @date 2022-08-03 18:33:38
 */
public class LinkUtilTest {

    @Test
    public void getLongerLink() {
        String longerLink = LinkUtil.getLongerLink("http://t.csdn.cn/eqidG");
        assertTrue(StrUtil.contains(longerLink, "https://blog.csdn.net/qq_45400861/article/details/126130772"));

    }

    @Test
    public void getLongerLink_NOT() {
        String longerLink = LinkUtil.getLongerLink("https://blog.csdn.net/qq_45400861/article/details/126130772?app_version=5.7.0&csdn_share_tail=%7B%22type%22%3A%22blog%22%2C%22rType%22%3A%22article%22%2C%22rId%22%3A%22126130772%22%2C%22source%22%3A%22qq_45400861%22%7D&ctrtid=WohoZ&utm_source=app");
        assertTrue(StrUtil.contains(longerLink, "https://blog.csdn.net/qq_45400861/article/details/126130772"));
    }

    @Test
    public void testGetLongerLink() {
        String list = LinkUtil.getURILink("https://blog.csdn.net/weixin_50481708/article/details/126132190?spm=1001.2014.3001.5501");

        assertEquals("https://blog.csdn.net/weixin_50481708/article/details/126132190", list);
    }
}