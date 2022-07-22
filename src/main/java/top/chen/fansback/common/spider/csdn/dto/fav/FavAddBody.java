package top.chen.fansback.common.spider.csdn.dto.fav;

import lombok.Data;

@Data
public class FavAddBody {

	private String url;
	private String source;
	private long sourceId;
	private String author;
	private String title;
	private String description;
	private String fromType;
	private String username;
	private long folderId;
	private String newFolderName;
}
