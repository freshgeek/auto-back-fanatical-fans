package top.chen.fansback.common.spider.csdn.dto.message;


import top.chen.fansback.common.spider.csdn.dto.home.PrivateMessage;

import java.util.List;

@lombok.Data
public class PrivateMessageResponse {

	private String code;
	private List<PrivateMessage> data;
	private String message;
	private boolean status;
}
