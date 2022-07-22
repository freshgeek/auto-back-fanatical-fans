package top.chen.fansback.common.spider.csdn.dto.message;

import java.util.List;

@lombok.Data
public class Data {


	private int countNum;
	private int unReadCount;
	private List<ResultList> resultList;
}
