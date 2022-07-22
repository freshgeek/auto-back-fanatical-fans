package top.chen.fansback.common.spider.csdn.dto.message;

import lombok.Data;

@Data
public class ResultList {

	private long id;
	private String time;
	private Content content;
	private String username;
	private int status;
}
