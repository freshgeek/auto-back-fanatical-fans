package top.chen.fansback.common.spider.csdn.dto.home;

import lombok.Data;

import java.util.List;

@Data
public class ResponseData {

	private List<ArticleList> list;
	private int total;
}
