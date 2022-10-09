package top.chen.fansback.common.spider.csdn.dto.home;


import lombok.Data;
import top.chen.fansback.common.spider.csdn.dto.message.Identity;

import java.util.List;

@Data
public class PrivateMessage {

	private String avatar;
	private String content;
	private Long createTime;
	private boolean digitalShow;
	private boolean hasReplied;
	private List<Identity> identity;
	private Integer messageType;
	private String nickname;
	private boolean official;
	private Integer relation;
	private boolean setTop;
	private Integer unReadCount;
	private Long updateTime;
	private String username;

}
