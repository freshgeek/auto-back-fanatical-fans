package top.chen.fansback.common.spider.csdn.dto.home;


import lombok.Data;

import java.util.Date;

@Data
public class ArticleList {

	private long articleId;
	private String title;
	private String description;
	private String url;
	private int type;
	private boolean top;
	private boolean forcePlan;
	private int viewCount;
	private int commentCount;
	private String editUrl;
	private String postTime;
	private int diggCount;
	private String formatTime;
}
