package top.chen.fansback.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenchao
 * @date 2022-08-03 17:42:32
 */
public class LinkUtil {

    public static String getLongerLink(String sortLink) {
        HttpResponse execute = HttpUtil.createGet(sortLink)
                .setMaxRedirectCount(1).execute();
        if (execute.getStatus() == HttpStatus.HTTP_MOVED_TEMP) {
            return execute.header(Header.LOCATION);
        }
        return sortLink;
    }

    public static List<String> getLongerLink(Collection<String> sortLink) {
        if (CollUtil.isEmpty(sortLink)) {
            return Collections.emptyList();
        }
        return sortLink.stream().parallel()
                .map(LinkUtil::getLongerLink)
                .collect(Collectors.toList());
    }

    public static List<String> getURILinks(List<String> urlWithParam) {
        if (CollUtil.isEmpty(urlWithParam)) {
            return urlWithParam;
        }
        return urlWithParam.stream().map(LinkUtil::getURILink).collect(Collectors.toList());
    }

    public static String getURILink(String s) {
        URL url = URLUtil.toUrlForHttp(s);
        if (url.getPort() > 0) {
            return String.format("%s://%s:%s%s", url.getProtocol(), url.getHost(), url.getPort(), url.getPath());
        }
        return String.format("%s://%s%s", url.getProtocol(), url.getHost(), url.getPath());
    }


}
