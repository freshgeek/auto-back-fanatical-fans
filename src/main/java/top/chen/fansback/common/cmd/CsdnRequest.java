package top.chen.fansback.common.cmd;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import top.chen.fansback.common.spider.csdn.db.UniqDatasource;
import top.chen.fansback.common.spider.csdn.dto.fav.FavAddBody;
import top.chen.fansback.common.spider.csdn.dto.home.ArticleList;
import top.chen.fansback.common.spider.csdn.dto.home.ResponseData;
import top.chen.fansback.common.spider.csdn.dto.home.Root;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author chenchao
 */
@Slf4j
public class CsdnRequest {
	private static   String LIKE_LIMIT = null;
	private static String ARTICLE_COMMENT_TODAY_LIMIT;


	// 点赞评论
//	curl 'https://blog.csdn.net/phoenix/web/v1/comment/digg' \
//			-H 'authority: blog.csdn.net' \
//			-H 'accept: application/json, text/javascript, */*; q=0.01' \
//			-H 'accept-language: zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,zh-HK;q=0.6' \
//			-H 'cache-control: no-cache' \
//			-H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
//			-H $'cookie: uuid_tt_dd=10_30804625640-1653311447359-308057; __gads=ID=263e444cd04fc7e7-22e7f7a45fd3000f:T=1653311474:RT=1653311474:S=ALNI_MbFIo9UFiJuUFstn3KuUlRQ1a44fg; ssxmod_itna=QqUxcDgDnAG=DQ6x0dPYIE6OWDO81DBetnEpOhmx0vp2eGzDAxn40iDt=rLe/Qcgr5qN4r7Pr3UKn7aAK3F1gxRGLA/qqGLDmKDy3aO4GG0xBYDQxAYDGDDPDogPD1D3qDkD7h6CMy1qGWDm4kDGeDe2IODY5DhxDCR0PDwx0Cjo2qj1YHdFymu=0hF7xkD7HpDlpxEfgwfRSqM1AAm3B3Ix0kg40Oya5sz=oDUlFsBoYirch4TlwYtsPixhGoNG0DWm15p72qPGxKOgQDig=K=khDD=; ssxmod_itna2=QqUxcDgDnAG=DQ6x0dPYIE6OWDO81DBetnEpOhDnIfphPDs1NeDL7P8WLinxLWqid=UcDIpt467kg253cExK2GYUrBA0iy4xd7RGDkKF7WwqP3/jUGqBSc+PpHZ9ogut3lblg7gxpu4NGgEXU2Gjz3eZ/TtMmEIvQGDjmMEx8YT77dEICuiCbCv7DQRkK7Deq8dlYp6D23Y1Sd9631jlQcXOdRdFEdYkSC=1wcbGPM6kNjUkAME/tu=8Z+HV3eFXIUHqXZjwkSU+04Q5cTCee1G7DXhnrf7O9fuU3O5IM0fju=k=ncYxPNBr6CP6wePS7Pq4tROfxlD7j=EDePZq1=Gwnt7lL0PXbZmKed/0=HYmtOQkgKEDYkp=DatpOw/A3LpUGEdaPifPK26bWObUpNN/xN+qGURF4j6ZYA1YZiI/Grqc5snIVxvgD4Yc5Y4TmjrhAbD/5282kcif1jNGY3Rj99pzSqo+9+A4DQKe4xe6isi0FBLtRMzqmsX2q+RGBk=F0+1woFiKHQiiGkFDDFqD+ZDxD===; UserName=qq_35530042; UserInfo=b8c7fd241df24874b573f4ae0e51c610; UserToken=b8c7fd241df24874b573f4ae0e51c610; UserNick=%E6%9C%A8%E7%A7%80%E6%9E%97; AU=E5E; UN=qq_35530042; BT=1654262840276; p_uid=U010000; Hm_up_6bcd52f51e9b3dce32bec4a3997715ac=%7B%22islogin%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22isonline%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22isvip%22%3A%7B%22value%22%3A%220%22%2C%22scope%22%3A1%7D%2C%22uid_%22%3A%7B%22value%22%3A%22qq_35530042%22%2C%22scope%22%3A1%7D%7D; Hm_ct_6bcd52f51e9b3dce32bec4a3997715ac=6525*1*10_30804625640-1653311447359-308057\u00215744*1*qq_35530042; _ga=GA1.2.1831406639.1654312193; c_segment=9; dc_sid=61502bb849855a0f67fe969248a67a32; management_ques=1657027151552; Hm_lvt_6bcd52f51e9b3dce32bec4a3997715ac=1657539745; csrfToken=Ys1gzpyUPWEPmUuieef869Gc; dc_session_id=10_1658022342863.407849; c_hasSub=true; _gid=GA1.2.1864936445.1658022344; __gpi=UID=000005aee2c90bb8:T=1653311474:RT=1658022345:S=ALNI_Mbut5zcHoKVTrQBRx4TMWZJ87pmFA; FCNEC=[["AKsRol-5r2O8M7rXAaz5YdTDx3g_iIymkYsPWfr4V6BGYb07Cl67azZwuFGtp2aRMQD7PVSfC_Jk9klONTkbRRnmZKBjch3zwZv3DpPctgiA93LNjJSV8OgyB6Tf0mX1zBvNO7dLQnxLnnplXO18z5HQIllRlFheVA=="],null,[]]; c_pref=default; c_first_ref=default; c_first_page=https%3A//blog.csdn.net/weixin_45735355; c_dsid=11_1658024343771.074552; c_ref=https%3A//blog.csdn.net/weixin_45735355; c_page_id=default; dc_tos=rf57zy; log_Id_pv=571; Hm_lpvt_6bcd52f51e9b3dce32bec4a3997715ac=1658024638; log_Id_view=1036; log_Id_click=313' \
//			-H 'origin: https://blog.csdn.net' \
//			-H 'pragma: no-cache' \
//			-H 'referer: https://blog.csdn.net/weixin_45735355/article/details/125406241?spm=1001.2014.3001.5501' \
//			-H 'sec-ch-ua: ".Not/A)Brand";v="99", "Google Chrome";v="103", "Chromium";v="103"' \
//			-H 'sec-ch-ua-mobile: ?0' \
//			-H 'sec-ch-ua-platform: "Windows"' \
//			-H 'sec-fetch-dest: empty' \
//			-H 'sec-fetch-mode: cors' \
//			-H 'sec-fetch-site: same-origin' \
//			-H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36' \
//			-H 'x-requested-with: XMLHttpRequest' \
//			-H 'x-tingyun-id: im-pGljNfnc;r=24753726' \
//			--data-raw 'articleId=125406241&commentId=22399007' \
//			--compressed
	// 点赞文章操作
//	curl "https://blog.csdn.net//phoenix/web/v1/article/like" ^
//			-H "authority: blog.csdn.net" ^
//			-H "accept: application/json, text/javascript, */*; q=0.01" ^
//			-H "accept-language: zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,zh-HK;q=0.6" ^
//			-H "cache-control: no-cache" ^
//			-H "content-type: application/x-www-form-urlencoded; charset=UTF-8" ^
//			-H "cookie: uuid_tt_dd=10_30804625640-1653311447359-308057; __gads=ID=263e444cd04fc7e7-22e7f7a45fd3000f:T=1653311474:RT=1653311474:S=ALNI_MbFIo9UFiJuUFstn3KuUlRQ1a44fg; ssxmod_itna=QqUxcDgDnAG=DQ6x0dPYIE6OWDO81DBetnEpOhmx0vp2eGzDAxn40iDt=rLe/Qcgr5qN4r7Pr3UKn7aAK3F1gxRGLA/qqGLDmKDy3aO4GG0xBYDQxAYDGDDPDogPD1D3qDkD7h6CMy1qGWDm4kDGeDe2IODY5DhxDCR0PDwx0Cjo2qj1YHdFymu=0hF7xkD7HpDlpxEfgwfRSqM1AAm3B3Ix0kg40Oya5sz=oDUlFsBoYirch4TlwYtsPixhGoNG0DWm15p72qPGxKOgQDig=K=khDD=; ssxmod_itna2=QqUxcDgDnAG=DQ6x0dPYIE6OWDO81DBetnEpOhDnIfphPDs1NeDL7P8WLinxLWqid=UcDIpt467kg253cExK2GYUrBA0iy4xd7RGDkKF7WwqP3/jUGqBSc+PpHZ9ogut3lblg7gxpu4NGgEXU2Gjz3eZ/TtMmEIvQGDjmMEx8YT77dEICuiCbCv7DQRkK7Deq8dlYp6D23Y1Sd9631jlQcXOdRdFEdYkSC=1wcbGPM6kNjUkAME/tu=8Z+HV3eFXIUHqXZjwkSU+04Q5cTCee1G7DXhnrf7O9fuU3O5IM0fju=k=ncYxPNBr6CP6wePS7Pq4tROfxlD7j=EDePZq1=Gwnt7lL0PXbZmKed/0=HYmtOQkgKEDYkp=DatpOw/A3LpUGEdaPifPK26bWObUpNN/xN+qGURF4j6ZYA1YZiI/Grqc5snIVxvgD4Yc5Y4TmjrhAbD/5282kcif1jNGY3Rj99pzSqo+9+A4DQKe4xe6isi0FBLtRMzqmsX2q+RGBk=F0+1woFiKHQiiGkFDDFqD+ZDxD===; UserName=qq_35530042; UserInfo=b8c7fd241df24874b573f4ae0e51c610; UserToken=b8c7fd241df24874b573f4ae0e51c610; UserNick=^%^E6^%^9C^%^A8^%^E7^%^A7^%^80^%^E6^%^9E^%^97; AU=E5E; UN=qq_35530042; BT=1654262840276; p_uid=U010000; Hm_up_6bcd52f51e9b3dce32bec4a3997715ac=^%^7B^%^22islogin^%^22^%^3A^%^7B^%^22value^%^22^%^3A^%^221^%^22^%^2C^%^22scope^%^22^%^3A1^%^7D^%^2C^%^22isonline^%^22^%^3A^%^7B^%^22value^%^22^%^3A^%^221^%^22^%^2C^%^22scope^%^22^%^3A1^%^7D^%^2C^%^22isvip^%^22^%^3A^%^7B^%^22value^%^22^%^3A^%^220^%^22^%^2C^%^22scope^%^22^%^3A1^%^7D^%^2C^%^22uid_^%^22^%^3A^%^7B^%^22value^%^22^%^3A^%^22qq_35530042^%^22^%^2C^%^22scope^%^22^%^3A1^%^7D^%^7D; Hm_ct_6bcd52f51e9b3dce32bec4a3997715ac=6525*1*10_30804625640-1653311447359-308057^!5744*1*qq_35530042; _ga=GA1.2.1831406639.1654312193; c_segment=9; dc_sid=61502bb849855a0f67fe969248a67a32; management_ques=1657027151552; Hm_lvt_6bcd52f51e9b3dce32bec4a3997715ac=1657539745; csrfToken=Ys1gzpyUPWEPmUuieef869Gc; dc_session_id=10_1658022342863.407849; c_hasSub=true; _gid=GA1.2.1864936445.1658022344; __gpi=UID=000005aee2c90bb8:T=1653311474:RT=1658022345:S=ALNI_Mbut5zcHoKVTrQBRx4TMWZJ87pmFA; FCNEC=^[^[^\^"AKsRol-5r2O8M7rXAaz5YdTDx3g_iIymkYsPWfr4V6BGYb07Cl67azZwuFGtp2aRMQD7PVSfC_Jk9klONTkbRRnmZKBjch3zwZv3DpPctgiA93LNjJSV8OgyB6Tf0mX1zBvNO7dLQnxLnnplXO18z5HQIllRlFheVA==^\^"^],null,^[^]^]; c_pref=default; c_first_ref=default; c_first_page=https^%^3A//blog.csdn.net/weixin_45735355; c_dsid=11_1658024343771.074552; c_ref=https^%^3A//blog.csdn.net/weixin_45735355; log_Id_click=309; c_page_id=default; dc_tos=rf57zy; log_Id_pv=571; Hm_lpvt_6bcd52f51e9b3dce32bec4a3997715ac=1658024638; log_Id_view=1033" ^
//			-H "origin: https://blog.csdn.net" ^
//			-H "pragma: no-cache" ^
//			-H "referer: https://blog.csdn.net/weixin_45735355/article/details/125406241?spm=1001.2014.3001.5501" ^
//			-H "sec-ch-ua: ^\^".Not/A)Brand^\^";v=^\^"99^\^", ^\^"Google Chrome^\^";v=^\^"103^\^", ^\^"Chromium^\^";v=^\^"103^\^"" ^
//			-H "sec-ch-ua-mobile: ?0" ^
//			-H "sec-ch-ua-platform: ^\^"Windows^\^"" ^
//			-H "sec-fetch-dest: empty" ^
//			-H "sec-fetch-mode: cors" ^
//			-H "sec-fetch-site: same-origin" ^
//			-H "user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36" ^
//			-H "x-requested-with: XMLHttpRequest" ^
//			-H "x-tingyun-id: im-pGljNfnc;r=24644259" ^
//			--data-raw "articleId=125406241" ^
//			--compressed
	// 关注操作
//	curl 'https://mp-action.csdn.net/interact/wrapper/pc/fans/v1/api/follow' \
//			-H 'authority: mp-action.csdn.net' \
//			-H 'accept: application/json, text/plain, */*' \
//			-H 'accept-language: zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,zh-HK;q=0.6' \
//			-H 'cache-control: no-cache' \
//			-H 'content-type: application/json;charset=UTF-8' \
//			-H $'cookie: uuid_tt_dd=10_30804625640-1653311447359-308057; __gads=ID=263e444cd04fc7e7-22e7f7a45fd3000f:T=1653311474:RT=1653311474:S=ALNI_MbFIo9UFiJuUFstn3KuUlRQ1a44fg; ssxmod_itna=QqUxcDgDnAG=DQ6x0dPYIE6OWDO81DBetnEpOhmx0vp2eGzDAxn40iDt=rLe/Qcgr5qN4r7Pr3UKn7aAK3F1gxRGLA/qqGLDmKDy3aO4GG0xBYDQxAYDGDDPDogPD1D3qDkD7h6CMy1qGWDm4kDGeDe2IODY5DhxDCR0PDwx0Cjo2qj1YHdFymu=0hF7xkD7HpDlpxEfgwfRSqM1AAm3B3Ix0kg40Oya5sz=oDUlFsBoYirch4TlwYtsPixhGoNG0DWm15p72qPGxKOgQDig=K=khDD=; ssxmod_itna2=QqUxcDgDnAG=DQ6x0dPYIE6OWDO81DBetnEpOhDnIfphPDs1NeDL7P8WLinxLWqid=UcDIpt467kg253cExK2GYUrBA0iy4xd7RGDkKF7WwqP3/jUGqBSc+PpHZ9ogut3lblg7gxpu4NGgEXU2Gjz3eZ/TtMmEIvQGDjmMEx8YT77dEICuiCbCv7DQRkK7Deq8dlYp6D23Y1Sd9631jlQcXOdRdFEdYkSC=1wcbGPM6kNjUkAME/tu=8Z+HV3eFXIUHqXZjwkSU+04Q5cTCee1G7DXhnrf7O9fuU3O5IM0fju=k=ncYxPNBr6CP6wePS7Pq4tROfxlD7j=EDePZq1=Gwnt7lL0PXbZmKed/0=HYmtOQkgKEDYkp=DatpOw/A3LpUGEdaPifPK26bWObUpNN/xN+qGURF4j6ZYA1YZiI/Grqc5snIVxvgD4Yc5Y4TmjrhAbD/5282kcif1jNGY3Rj99pzSqo+9+A4DQKe4xe6isi0FBLtRMzqmsX2q+RGBk=F0+1woFiKHQiiGkFDDFqD+ZDxD===; UserName=qq_35530042; UserInfo=b8c7fd241df24874b573f4ae0e51c610; UserToken=b8c7fd241df24874b573f4ae0e51c610; UserNick=%E6%9C%A8%E7%A7%80%E6%9E%97; AU=E5E; UN=qq_35530042; BT=1654262840276; p_uid=U010000; Hm_up_6bcd52f51e9b3dce32bec4a3997715ac=%7B%22islogin%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22isonline%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22isvip%22%3A%7B%22value%22%3A%220%22%2C%22scope%22%3A1%7D%2C%22uid_%22%3A%7B%22value%22%3A%22qq_35530042%22%2C%22scope%22%3A1%7D%7D; Hm_ct_6bcd52f51e9b3dce32bec4a3997715ac=6525*1*10_30804625640-1653311447359-308057\u00215744*1*qq_35530042; _ga=GA1.2.1831406639.1654312193; c_segment=9; dc_sid=61502bb849855a0f67fe969248a67a32; management_ques=1657027151552; Hm_lvt_6bcd52f51e9b3dce32bec4a3997715ac=1657539745; dc_session_id=10_1658022342863.407849; c_hasSub=true; _gid=GA1.2.1864936445.1658022344; __gpi=UID=000005aee2c90bb8:T=1653311474:RT=1658022345:S=ALNI_Mbut5zcHoKVTrQBRx4TMWZJ87pmFA; FCNEC=[["AKsRol-5r2O8M7rXAaz5YdTDx3g_iIymkYsPWfr4V6BGYb07Cl67azZwuFGtp2aRMQD7PVSfC_Jk9klONTkbRRnmZKBjch3zwZv3DpPctgiA93LNjJSV8OgyB6Tf0mX1zBvNO7dLQnxLnnplXO18z5HQIllRlFheVA=="],null,[]]; c_pref=default; c_first_ref=default; c_first_page=https%3A//blog.csdn.net/weixin_45735355; c_dsid=11_1658024343771.074552; c_ref=https%3A//blog.csdn.net/weixin_45735355; c_page_id=default; dc_tos=rf57x1; log_Id_pv=570; Hm_lpvt_6bcd52f51e9b3dce32bec4a3997715ac=1658024534; log_Id_view=1032; log_Id_click=308' \
//			-H 'origin: https://blog.csdn.net' \
//			-H 'pragma: no-cache' \
//			-H 'referer: https://blog.csdn.net/weixin_45735355' \
//			-H 'sec-ch-ua: ".Not/A)Brand";v="99", "Google Chrome";v="103", "Chromium";v="103"' \
//			-H 'sec-ch-ua-mobile: ?0' \
//			-H 'sec-ch-ua-platform: "Windows"' \
//			-H 'sec-fetch-dest: empty' \
//			-H 'sec-fetch-mode: cors' \
//			-H 'sec-fetch-site: same-site' \
//			-H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36' \
//			--data-raw '{"username":"qq_35530042","follow":"weixin_45735355","source":"ME","fromType":"pc","detailSourceName":"个人主页"}' \
//			--compressed
	// 评论操作

	/**
	 * 自己
	 */
	public static String OWNER = "";
	public static boolean ARTICLE_COMMENT_LIMIT = false;
	public static int MESSAGE_PAGE_SIZE = 15;
	//	curl 'https://blog.csdn.net/phoenix/web/v1/comment/list/125729335?page=1&size=10&fold=unfold&commentId=22410830' \
//			-X 'POST' \
//			-H 'authority: blog.csdn.net' \
//			-H 'accept: */*' \
//			-H 'accept-language: zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,zh-HK;q=0.6' \
//			-H 'cache-control: no-cache' \
//			-H 'content-length: 0' \
//			-H $'cookie: uuid_tt_dd=10_30804625640-1653311447359-308057; __gads=ID=263e444cd04fc7e7-22e7f7a45fd3000f:T=1653311474:RT=1653311474:S=ALNI_MbFIo9UFiJuUFstn3KuUlRQ1a44fg; ssxmod_itna=QqUxcDgDnAG=DQ6x0dPYIE6OWDO81DBetnEpOhmx0vp2eGzDAxn40iDt=rLe/Qcgr5qN4r7Pr3UKn7aAK3F1gxRGLA/qqGLDmKDy3aO4GG0xBYDQxAYDGDDPDogPD1D3qDkD7h6CMy1qGWDm4kDGeDe2IODY5DhxDCR0PDwx0Cjo2qj1YHdFymu=0hF7xkD7HpDlpxEfgwfRSqM1AAm3B3Ix0kg40Oya5sz=oDUlFsBoYirch4TlwYtsPixhGoNG0DWm15p72qPGxKOgQDig=K=khDD=; ssxmod_itna2=QqUxcDgDnAG=DQ6x0dPYIE6OWDO81DBetnEpOhDnIfphPDs1NeDL7P8WLinxLWqid=UcDIpt467kg253cExK2GYUrBA0iy4xd7RGDkKF7WwqP3/jUGqBSc+PpHZ9ogut3lblg7gxpu4NGgEXU2Gjz3eZ/TtMmEIvQGDjmMEx8YT77dEICuiCbCv7DQRkK7Deq8dlYp6D23Y1Sd9631jlQcXOdRdFEdYkSC=1wcbGPM6kNjUkAME/tu=8Z+HV3eFXIUHqXZjwkSU+04Q5cTCee1G7DXhnrf7O9fuU3O5IM0fju=k=ncYxPNBr6CP6wePS7Pq4tROfxlD7j=EDePZq1=Gwnt7lL0PXbZmKed/0=HYmtOQkgKEDYkp=DatpOw/A3LpUGEdaPifPK26bWObUpNN/xN+qGURF4j6ZYA1YZiI/Grqc5snIVxvgD4Yc5Y4TmjrhAbD/5282kcif1jNGY3Rj99pzSqo+9+A4DQKe4xe6isi0FBLtRMzqmsX2q+RGBk=F0+1woFiKHQiiGkFDDFqD+ZDxD===; UserName=qq_35530042; UserInfo=b8c7fd241df24874b573f4ae0e51c610; UserToken=b8c7fd241df24874b573f4ae0e51c610; UserNick=%E6%9C%A8%E7%A7%80%E6%9E%97; AU=E5E; UN=qq_35530042; BT=1654262840276; p_uid=U010000; Hm_up_6bcd52f51e9b3dce32bec4a3997715ac=%7B%22islogin%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22isonline%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22isvip%22%3A%7B%22value%22%3A%220%22%2C%22scope%22%3A1%7D%2C%22uid_%22%3A%7B%22value%22%3A%22qq_35530042%22%2C%22scope%22%3A1%7D%7D; Hm_ct_6bcd52f51e9b3dce32bec4a3997715ac=6525*1*10_30804625640-1653311447359-308057\u00215744*1*qq_35530042; _ga=GA1.2.1831406639.1654312193; c_segment=9; dc_sid=61502bb849855a0f67fe969248a67a32; c_dl_prid=-; c_dl_rid=1655389958716_886324; c_dl_fref=https://blog.csdn.net/weixin_39715926/article/details/114102544; c_dl_fpage=/download/u011075492/10863393; c_dl_um=-; management_ques=1657027151552; Hm_lvt_6bcd52f51e9b3dce32bec4a3997715ac=1657539745; c_first_ref=default; csrfToken=Ys1gzpyUPWEPmUuieef869Gc; c_hasSub=true; c_utm_source=originalprotection; dc_session_id=11_1657978034130.568881; __gpi=UID=000005aee2c90bb8:T=1653311474:RT=1657978051:S=ALNI_Mbut5zcHoKVTrQBRx4TMWZJ87pmFA; log_Id_click=286; c_pref=default; c_ref=default; c_first_page=https%3A//blog.csdn.net/qq_35530042/article/details/125729335%23comments_22410830; log_Id_view=994; FCNEC=[["AKsRol_JQmONirEPLaPmyLPXD-zPU7zt4RrhwQXWbF-JeEwbs85dAt-XjSBiWY5-Or88qRWz2scJH79ba0aCGft07De_0SK8WUp7HRzz5qo_sllISCBVjEkyMmqe12U25PhmyY46yeFAgeWB96nNIvpKfvw_1hUJzw=="],null,[]]; c_dsid=11_1657980830472.453173; c_page_id=default; dc_tos=rf4a72; log_Id_pv=541; Hm_lpvt_6bcd52f51e9b3dce32bec4a3997715ac=1657980835' \
//			-H 'origin: https://blog.csdn.net' \
//			-H 'pragma: no-cache' \
//			-H 'referer: https://blog.csdn.net/qq_35530042/article/details/125729335' \
//			-H 'sec-ch-ua: ".Not/A)Brand";v="99", "Google Chrome";v="103", "Chromium";v="103"' \
//			-H 'sec-ch-ua-mobile: ?0' \
//			-H 'sec-ch-ua-platform: "Windows"' \
//			-H 'sec-fetch-dest: empty' \
//			-H 'sec-fetch-mode: cors' \
//			-H 'sec-fetch-site: same-origin' \
//			-H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36' \
//			-H 'x-requested-with: XMLHttpRequest' \
//			-H 'x-tingyun-id: im-pGljNfnc;r=980836300' \
//			--compressed
	static String CSDN_COOKIE = "";
	/**
	 * 评论限流 21s，每分钟只能评论三条
	 */
	static int COMMENT_SLEEP = 21;
	static long defaultFavFoldId = 0L;
	// 我的评论列表
	String aritle_comment_list = "https://blog.csdn.net/phoenix/web/v1/comment/list/125729335?page=1&size=10&fold=unfold&commentId=22410830";

	//	Request MY_LIKE_FAV_MESSAGE_REQUEST = new Request(CsdnRequest.MY_LIKE_FAV_MESSAGE).setMethod("POST");
	//	curl '' \
//
//			--data-raw 'commentId=&content=+%E5%8D%9A%E4%B8%BB%E8%AE%B2%E8%A7%A3%E7%9A%84%E4%B9%9F%E5%A4%AA%E7%BB%86%E4%BA%86%E5%90%A7%EF%BC%8C%E6%8E%92%E7%89%88%E4%B9%9F%E6%B8%85%E6%A5%9A%E7%9A%84%E4%B8%80%E6%89%B9%EF%BC%8C%E6%AC%A2%E8%BF%8E%E6%9D%A5%E7%9C%8B%E7%9C%8B%E6%88%91%E6%95%B4%E7%90%86%E7%9A%84%E3%80%90%E9%9D%A2%E8%AF%95%E5%85%AB%E8%82%A1%E6%96%87%E4%B8%93%E6%A0%8F%E3%80%91&articleId=125406241' \
//			--compressed
	// 收藏操作
	public static boolean postAddFav(String url, String author, String title, String desc) {
		//	curl 'https://mp-action.csdn.net/interact/wrapper/pc/favorite/v1/api/addFavorite' \
//			-H 'authority: mp-action.csdn.net' \
//			-H 'accept: */*' \
//			-H 'accept-language: zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,zh-HK;q=0.6' \
//			-H 'cache-control: no-cache' \
//			-H 'content-type: application/json' \
//			-H $'cookie: uuid_tt_dd=10_30804625640-1653311447359-308057; __gads=ID=263e444cd04fc7e7-22e7f7a45fd3000f:T=1653311474:RT=1653311474:S=ALNI_MbFIo9UFiJuUFstn3KuUlRQ1a44fg; ssxmod_itna=QqUxcDgDnAG=DQ6x0dPYIE6OWDO81DBetnEpOhmx0vp2eGzDAxn40iDt=rLe/Qcgr5qN4r7Pr3UKn7aAK3F1gxRGLA/qqGLDmKDy3aO4GG0xBYDQxAYDGDDPDogPD1D3qDkD7h6CMy1qGWDm4kDGeDe2IODY5DhxDCR0PDwx0Cjo2qj1YHdFymu=0hF7xkD7HpDlpxEfgwfRSqM1AAm3B3Ix0kg40Oya5sz=oDUlFsBoYirch4TlwYtsPixhGoNG0DWm15p72qPGxKOgQDig=K=khDD=; ssxmod_itna2=QqUxcDgDnAG=DQ6x0dPYIE6OWDO81DBetnEpOhDnIfphPDs1NeDL7P8WLinxLWqid=UcDIpt467kg253cExK2GYUrBA0iy4xd7RGDkKF7WwqP3/jUGqBSc+PpHZ9ogut3lblg7gxpu4NGgEXU2Gjz3eZ/TtMmEIvQGDjmMEx8YT77dEICuiCbCv7DQRkK7Deq8dlYp6D23Y1Sd9631jlQcXOdRdFEdYkSC=1wcbGPM6kNjUkAME/tu=8Z+HV3eFXIUHqXZjwkSU+04Q5cTCee1G7DXhnrf7O9fuU3O5IM0fju=k=ncYxPNBr6CP6wePS7Pq4tROfxlD7j=EDePZq1=Gwnt7lL0PXbZmKed/0=HYmtOQkgKEDYkp=DatpOw/A3LpUGEdaPifPK26bWObUpNN/xN+qGURF4j6ZYA1YZiI/Grqc5snIVxvgD4Yc5Y4TmjrhAbD/5282kcif1jNGY3Rj99pzSqo+9+A4DQKe4xe6isi0FBLtRMzqmsX2q+RGBk=F0+1woFiKHQiiGkFDDFqD+ZDxD===; UserName=qq_35530042; UserInfo=b8c7fd241df24874b573f4ae0e51c610; UserToken=b8c7fd241df24874b573f4ae0e51c610; UserNick=%E6%9C%A8%E7%A7%80%E6%9E%97; AU=E5E; UN=qq_35530042; BT=1654262840276; p_uid=U010000; Hm_up_6bcd52f51e9b3dce32bec4a3997715ac=%7B%22islogin%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22isonline%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22isvip%22%3A%7B%22value%22%3A%220%22%2C%22scope%22%3A1%7D%2C%22uid_%22%3A%7B%22value%22%3A%22qq_35530042%22%2C%22scope%22%3A1%7D%7D; Hm_ct_6bcd52f51e9b3dce32bec4a3997715ac=6525*1*10_30804625640-1653311447359-308057\u00215744*1*qq_35530042; _ga=GA1.2.1831406639.1654312193; c_segment=9; dc_sid=61502bb849855a0f67fe969248a67a32; management_ques=1657027151552; Hm_lvt_6bcd52f51e9b3dce32bec4a3997715ac=1657539745; dc_session_id=10_1658022342863.407849; c_hasSub=true; _gid=GA1.2.1864936445.1658022344; __gpi=UID=000005aee2c90bb8:T=1653311474:RT=1658022345:S=ALNI_Mbut5zcHoKVTrQBRx4TMWZJ87pmFA; FCNEC=[["AKsRol-5r2O8M7rXAaz5YdTDx3g_iIymkYsPWfr4V6BGYb07Cl67azZwuFGtp2aRMQD7PVSfC_Jk9klONTkbRRnmZKBjch3zwZv3DpPctgiA93LNjJSV8OgyB6Tf0mX1zBvNO7dLQnxLnnplXO18z5HQIllRlFheVA=="],null,[]]; c_pref=default; c_first_ref=default; c_first_page=https%3A//blog.csdn.net/weixin_45735355; c_dsid=11_1658024343771.074552; c_ref=https%3A//blog.csdn.net/weixin_45735355; c_page_id=default; dc_tos=rf57zy; log_Id_pv=571; Hm_lpvt_6bcd52f51e9b3dce32bec4a3997715ac=1658024638; log_Id_view=1036; log_Id_click=315' \
//			-H 'origin: https://blog.csdn.net' \
//			-H 'pragma: no-cache' \
//			-H 'referer: https://blog.csdn.net/weixin_45735355/article/details/125406241?spm=1001.2014.3001.5501' \
//			-H 'sec-ch-ua: ".Not/A)Brand";v="99", "Google Chrome";v="103", "Chromium";v="103"' \
//			-H 'sec-ch-ua-mobile: ?0' \
//			-H 'sec-ch-ua-platform: "Windows"' \
//			-H 'sec-fetch-dest: empty' \
//			-H 'sec-fetch-mode: cors' \
//			-H 'sec-fetch-site: same-site' \
//			-H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36' \
//			--data-raw '{"url":"https://baidaguo.blog.csdn.net/article/details/125406241","source":"blog","sourceId":125406241,"author":"weixin_45735355","title":"设计模式系列详解 -- 责任链模式","description":"责任链模式详解 Java面试专栏持续更新 面试跳槽的小伙伴们看过来","fromType":"PC","username":"qq_35530042","folderId":20076997,"newFolderName":""}' \
//			--compressed
		FavAddBody addBody = new FavAddBody();
		addBody.setUrl(url);
		addBody.setSource("blog");
		addBody.setSourceId(Long.parseLong(getArticleId(url)));
		addBody.setAuthor(author);
		addBody.setTitle(title);
		addBody.setDescription(desc);
		addBody.setFromType("PC");
		addBody.setUsername(OWNER);
		addBody.setFolderId(defaultFavFoldId);
		addBody.setNewFolderName("");
		HttpRequest request = HttpUtil.createPost("https://mp-action.csdn.net/interact/wrapper/pc/favorite/v1/api/addFavorite")
				.body(JSON.toJSONString(addBody));
		addHead(request, "accept: */*\n" +
				"accept-encoding: gzip, deflate, br\n" +
				"accept-language: zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,zh-HK;q=0.6\n" +
				"cache-control: no-cache\n" +
				"content-length: 418\n" +
				"content-type: application/json\n" +
				"origin: https://blog.csdn.net\n" +
				"pragma: no-cache\n" +
				"referer: " + url + "\n" +
				"sec-ch-ua: \".Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"103\", \"Chromium\";v=\"103\"\n" +
				"sec-ch-ua-mobile: ?0\n" +
				"sec-ch-ua-platform: \"Windows\"\n" +
				"sec-fetch-dest: empty\n" +
				"sec-fetch-mode: cors\n" +
				"sec-fetch-site: same-site\n" +
				"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
		HttpResponse response = request.execute();
		log.info("收藏操作：{},{},{}", url, author, response.body());
		return response.isOk() && response.body().contains("success");
	}

	public static ArticleList extraDetail(String url) {
		HttpRequest request = HttpUtil.createGet(url);
		addHead(request, "accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
				"accept-encoding: gzip, deflate, br\n" +
				"accept-language: zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,zh-HK;q=0.6\n" +
				"cache-control: no-cache\n" +
				"pragma: no-cache\n" +
				"sec-ch-ua: \".Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"103\", \"Chromium\";v=\"103\"\n" +
				"sec-ch-ua-mobile: ?0\n" +
				"sec-ch-ua-platform: \"Windows\"\n" +
				"sec-fetch-dest: document\n" +
				"sec-fetch-mode: navigate\n" +
				"sec-fetch-site: none\n" +
				"sec-fetch-user: ?1\n" +
				"upgrade-insecure-requests: 1\n" +
				"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
		Optional<Document> parse = Optional.ofNullable(Jsoup.parse(request.execute().body()));
		ArticleList list = new ArticleList();
		list.setUrl(url);
		list.setAuthor(parse.map(s -> s.select("#uid > span")).map(s -> s.get(0)).map(s -> s.attr("username")).orElse(null));
		list.setTitle(parse.map(s -> s.select("head > meta:nth-child(17)")).map(s -> s.get(0)).map(s -> s.attr("content")).orElse(null));
		list.setDescription(parse.map(s -> s.select("head > meta:nth-child(19)")).map(s -> s.get(0)).map(s -> s.attr("content")).orElse(null));
		return list;
	}

	public static boolean toDayCommentLimit() {
		return StrUtil.equals(ARTICLE_COMMENT_TODAY_LIMIT, DateUtil.today());
	}
	public static boolean toDayLikeLimit() {
		return StrUtil.equals(LIKE_LIMIT, DateUtil.today());
	}
	/**
	 * 给文章评论
	 */
	@SneakyThrows
	public static boolean postComment(String comment, String url) {
		HttpRequest post = HttpUtil.createPost("https://blog.csdn.net/phoenix/web/v1/comment/submit");
		if (StrUtil.isEmpty(url)) {
			log.warn("url 为空 ");
			return false;
		}
		if (toDayCommentLimit()){
			return false;
		}
		if (UniqDatasource.exists(url)) {
			log.debug("[{}]已经评论过了，就跳过", url);
			return false;
		}
		String article = getArticleId(url);
		post.form("content", comment);
		post.form("articleId", article);
		addHead(post, "accept: application/json, text/javascript, */*; q=0.01\n" +
				"accept-encoding: gzip, deflate, br\n" +
				"accept-language: zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,zh-HK;q=0.6\n" +
				"cache-control: no-cache\n" +
				"content-length: 211\n" +
				"content-type: application/x-www-form-urlencoded; charset=UTF-8\n" +
				"origin: https://blog.csdn.net\n" +
				"pragma: no-cache\n" +
				"referer: " + url + "\n" +
				"sec-ch-ua: \".Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"103\", \"Chromium\";v=\"103\"\n" +
				"sec-ch-ua-mobile: ?0\n" +
				"sec-ch-ua-platform: \"Windows\"\n" +
				"sec-fetch-dest: empty\n" +
				"sec-fetch-mode: cors\n" +
				"sec-fetch-site: same-origin\n" +
				"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36\n" +
				"x-requested-with: XMLHttpRequest\n" +
				"x-tingyun-id: im-pGljNfnc;r=27412521");
		HttpResponse httpResponse = post.executeAsync();
		log.info("[{}]评论[{}]返回：{}", comment, url, httpResponse.body());
		if (httpResponse.isOk() && httpResponse.body().contains("success")) {
			UniqDatasource.save(url);
		}
		if (httpResponse.body().contains("您的账号已被禁言")) {
			ARTICLE_COMMENT_LIMIT = true;
			return false;
		}
		if (httpResponse.body().contains("您已达到当日发送上限")) {
			ARTICLE_COMMENT_TODAY_LIMIT = DateUtil.today();
			return false;
		}
		TimeUnit.SECONDS.sleep(COMMENT_SLEEP);
		return httpResponse.isOk() && httpResponse.body().contains("success");
	}

	private static String getArticleId(String url) {
		String[] split = url.split("\\?")[0].split("/");
		return split[split.length - 1];
	}

	static void addHead(HttpRequest request, String defaultString) {
		try {
			String[] split = defaultString.split("\n");
			for (String s1 : split) {
				String[] split1 = s1.split(": ");
				request.header(split1[0], split1[1]);
			}
			request.header("cookie", CSDN_COOKIE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static cn.hutool.http.HttpRequest getRequestCommentMessageList(int index) {
		return getRequestMessageList(0, index);
	}

	public static cn.hutool.http.HttpRequest getRequestFollowMessageList(int index) {
		return getRequestMessageList(1, index);
	}

	public static cn.hutool.http.HttpRequest getRequestLikeFavMessageList(int index) {
		return getRequestMessageList(2, index);
	}

	static cn.hutool.http.HttpRequest getRequestMessageList(int type, int index) {
		index = Math.max(index, 1);
		// 我的消息 type = 0 评论回复
		// 1 关注粉丝
		// 2 收藏点赞
		String myLikeFavMessage = "https://msg.csdn.net/v1/web/message/view/message?%s";
		return HttpUtil.createPost(String.format(myLikeFavMessage, index))
				.header("content-type", "application/json;charset=UTF-8")
				.header("cookie", CSDN_COOKIE)
				.body("{\"type\":" + type + ",\"pageIndex\":" + index + ",\"pageSize\":" + MESSAGE_PAGE_SIZE + "}");

	}

	public static List<ArticleList> getUserArticleList(String name, boolean popular, int size) {
		HttpRequest request = HttpUtil.createGet("https://blog.csdn.net/community/home-api/v1/get-business-list")
				.form("page", 1)
				.form("size", size)
				.form("businessType", "blog")
				.form("noMore", false)
				.form("username", name);
		if (popular) {
			request.form("orderby", "ViewCount");
		}
		addHead(request, "accept: application/json, text/plain, */*\n" +
				"accept-encoding: gzip, deflate, br\n" +
				"accept-language: zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,zh-HK;q=0.6\n" +
				"cache-control: no-cache\n" +
				"pragma: no-cache\n" +
				"referer: https://blog.csdn.net/" + name + "\n" +
				"sec-ch-ua: \".Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"103\", \"Chromium\";v=\"103\"\n" +
				"sec-ch-ua-mobile: ?0\n" +
				"sec-ch-ua-platform: \"Windows\"\n" +
				"sec-fetch-dest: empty\n" +
				"sec-fetch-mode: cors\n" +
				"sec-fetch-site: same-origin\n" +
				"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
		String body = request.execute().body();
		Root root = JSON.parseObject(body, Root.class);
		return Optional.ofNullable(root).map(Root::getData).map(ResponseData::getList).orElse(Collections.emptyList());
	}

	/**
	 * 点赞文章
	 */
	public static boolean postLikeArticle(String url) {
		//	curl 'https://blog.csdn.net//phoenix/web/v1/article/like' \
		//			-H 'authority: blog.csdn.net' \
		//			-H 'accept: application/json, text/javascript, */*; q=0.01' \
		//			-H 'accept-language: zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,zh-HK;q=0.6' \
		//			-H 'cache-control: no-cache' \
		//			-H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
		//			-H $'cookie: uuid_tt_dd=10_30804625640-1653311447359-308057; __gads=ID=263e444cd04fc7e7-22e7f7a45fd3000f:T=1653311474:RT=1653311474:S=ALNI_MbFIo9UFiJuUFstn3KuUlRQ1a44fg; ssxmod_itna=QqUxcDgDnAG=DQ6x0dPYIE6OWDO81DBetnEpOhmx0vp2eGzDAxn40iDt=rLe/Qcgr5qN4r7Pr3UKn7aAK3F1gxRGLA/qqGLDmKDy3aO4GG0xBYDQxAYDGDDPDogPD1D3qDkD7h6CMy1qGWDm4kDGeDe2IODY5DhxDCR0PDwx0Cjo2qj1YHdFymu=0hF7xkD7HpDlpxEfgwfRSqM1AAm3B3Ix0kg40Oya5sz=oDUlFsBoYirch4TlwYtsPixhGoNG0DWm15p72qPGxKOgQDig=K=khDD=; ssxmod_itna2=QqUxcDgDnAG=DQ6x0dPYIE6OWDO81DBetnEpOhDnIfphPDs1NeDL7P8WLinxLWqid=UcDIpt467kg253cExK2GYUrBA0iy4xd7RGDkKF7WwqP3/jUGqBSc+PpHZ9ogut3lblg7gxpu4NGgEXU2Gjz3eZ/TtMmEIvQGDjmMEx8YT77dEICuiCbCv7DQRkK7Deq8dlYp6D23Y1Sd9631jlQcXOdRdFEdYkSC=1wcbGPM6kNjUkAME/tu=8Z+HV3eFXIUHqXZjwkSU+04Q5cTCee1G7DXhnrf7O9fuU3O5IM0fju=k=ncYxPNBr6CP6wePS7Pq4tROfxlD7j=EDePZq1=Gwnt7lL0PXbZmKed/0=HYmtOQkgKEDYkp=DatpOw/A3LpUGEdaPifPK26bWObUpNN/xN+qGURF4j6ZYA1YZiI/Grqc5snIVxvgD4Yc5Y4TmjrhAbD/5282kcif1jNGY3Rj99pzSqo+9+A4DQKe4xe6isi0FBLtRMzqmsX2q+RGBk=F0+1woFiKHQiiGkFDDFqD+ZDxD===; UserName=qq_35530042; UserInfo=b8c7fd241df24874b573f4ae0e51c610; UserToken=b8c7fd241df24874b573f4ae0e51c610; UserNick=%E6%9C%A8%E7%A7%80%E6%9E%97; AU=E5E; UN=qq_35530042; BT=1654262840276; p_uid=U010000; Hm_up_6bcd52f51e9b3dce32bec4a3997715ac=%7B%22islogin%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22isonline%22%3A%7B%22value%22%3A%221%22%2C%22scope%22%3A1%7D%2C%22isvip%22%3A%7B%22value%22%3A%220%22%2C%22scope%22%3A1%7D%2C%22uid_%22%3A%7B%22value%22%3A%22qq_35530042%22%2C%22scope%22%3A1%7D%7D; Hm_ct_6bcd52f51e9b3dce32bec4a3997715ac=6525*1*10_30804625640-1653311447359-308057\u00215744*1*qq_35530042; _ga=GA1.2.1831406639.1654312193; c_segment=9; dc_sid=61502bb849855a0f67fe969248a67a32; management_ques=1657027151552; Hm_lvt_6bcd52f51e9b3dce32bec4a3997715ac=1657539745; csrfToken=Ys1gzpyUPWEPmUuieef869Gc; c_hasSub=true; _gid=GA1.2.1864936445.1658022344; __gpi=UID=000005aee2c90bb8:T=1653311474:RT=1658022345:S=ALNI_Mbut5zcHoKVTrQBRx4TMWZJ87pmFA; has-vote-msg=1; c_first_ref=default; c_first_page=https%3A//blog.csdn.net/m0_52040370/article/details/125585723; dc_session_id=10_1658041056780.279236; c_dsid=11_1658041058022.073436; c_pref=https%3A//i.csdn.net/; c_ref=https%3A//blog.csdn.net/m0_58618795%3Ftype%3Dblog; c_page_id=default; dc_tos=rf5lck; log_Id_pv=663; Hm_lpvt_6bcd52f51e9b3dce32bec4a3997715ac=1658041941; FCNEC=[["AKsRol8VgnW58i1YdsNcDbcZAiIIePCnmRp-S0zisP9ZyV_ctbFhmqNm5Rcf-3u4HqlWk7p2kKzAo4aHYeRGl33qZG35ScsnfRWGMGWdLRctlYR3o3hx_z1GoGxeVgaWz6WqWw11yttOPfxvtlXqIC7uqoTPIFABZg=="],null,[]]; log_Id_click=388; log_Id_view=1159' \
		//			-H 'origin: https://ylqb196.blog.csdn.net' \
		//			-H 'pragma: no-cache' \
		//			-H 'referer: https://ylqb196.blog.csdn.net/article/details/125813793?spm=1001.2014.3001.5502' \
		//			-H 'sec-ch-ua: ".Not/A)Brand";v="99", "Google Chrome";v="103", "Chromium";v="103"' \
		//			-H 'sec-ch-ua-mobile: ?0' \
		//			-H 'sec-ch-ua-platform: "Windows"' \
		//			-H 'sec-fetch-dest: empty' \
		//			-H 'sec-fetch-mode: cors' \
		//			-H 'sec-fetch-site: same-site' \
		//			-H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36' \
		//			--data-raw 'articleId=125813793' \
		//			--compressed
		String articleId = getArticleId(url);
		URL toUrlForHttp = URLUtil.toUrlForHttp(url);
		HttpRequest request = HttpUtil.createPost("https://blog.csdn.net//phoenix/web/v1/article/like")
				.form("articleId", articleId);
		addHead(request, "accept: application/json, text/javascript, */*; q=0.01\n" +
				"accept-encoding: gzip, deflate, br\n" +
				"accept-language: zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7,zh-HK;q=0.6\n" +
				"cache-control: no-cache\n" +
				"content-length: 19\n" +
				"content-type: application/x-www-form-urlencoded; charset=UTF-8\n" +
				"origin: " + String.format("%s://%s", toUrlForHttp.getProtocol(), toUrlForHttp.getHost()) + "\n" +
				"pragma: no-cache\n" +
				"referer: " + url + "\n" +
				"sec-ch-ua: \".Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"103\", \"Chromium\";v=\"103\"\n" +
				"sec-ch-ua-mobile: ?0\n" +
				"sec-ch-ua-platform: \"Windows\"\n" +
				"sec-fetch-dest: empty\n" +
				"sec-fetch-mode: cors\n" +
				"sec-fetch-site: same-site\n" +
				"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
		HttpResponse execute = request.execute();
		log.info("点赞操作 {} , {}", url, execute.body());
		if (execute.body().contains("已达上限")) {
			LIKE_LIMIT = DateUtil.today();
		}
		return execute.isOk() && execute.body().contains("success") && execute.body().contains("status\":true");

	}

}
