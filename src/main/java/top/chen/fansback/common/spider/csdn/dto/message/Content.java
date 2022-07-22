package top.chen.fansback.common.spider.csdn.dto.message;

/**
 * Copyright 2022 json.cn
 */

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import top.chen.fansback.common.cmd.CsdnRequest;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Auto-generated: 2022-07-17 10:41:31
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class Content {

	private String tt;
	private String avatarUrl;
	private boolean isFans;
	private String title;
	private String url;
	private String tc;
	private String pd;
	private String reviewContent;
	private List<Identity> identity;
	private String nickname;
	private long commentId;
	private String id;
	private long taskId;
	private String username;
	private String usernames;

	public Set<String> getUniqUserName() {
		Optional<String> name = Optional.ofNullable(getUsername()).filter(StrUtil::isNotEmpty);
		Set<String> all = Optional.ofNullable(getUsernames()).filter(StrUtil::isNotEmpty).map(s ->
						Arrays.stream(s.split(StrUtil.COMMA)).collect(Collectors.toSet()))
				.orElse(new HashSet<>());
		name.ifPresent(all::add);
		return all.stream().filter(s -> !Objects.equals(CsdnRequest.OWNER, s)).collect(Collectors.toSet());
	}


}