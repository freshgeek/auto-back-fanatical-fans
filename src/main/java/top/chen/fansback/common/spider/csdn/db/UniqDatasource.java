package top.chen.fansback.common.spider.csdn.db;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import top.chen.fansback.common.BackProperties;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

/**
 * @author chenchao
 */
public final class UniqDatasource {

	private final static Set<String> HAS_EXISTS = new ConcurrentHashSet<>();
	private final static File LOCAL_FILE;

	static {
		LOCAL_FILE = FileUtil.newFile(BackProperties.data_dir);
		if (!LOCAL_FILE.exists()) {
			try {
				if (!LOCAL_FILE.getParentFile().exists()) {
					Assert.isTrue(LOCAL_FILE.getParentFile().mkdirs());
				}
				Assert.isTrue(LOCAL_FILE.createNewFile());
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		HAS_EXISTS.addAll(FileUtil.readLines(LOCAL_FILE, StandardCharsets.UTF_8));
	}

	public synchronized static void save(String save) {
		if (HAS_EXISTS.add(save)) {
			FileUtil.writeLines(Collections.singletonList(save), LOCAL_FILE, StandardCharsets.UTF_8, true);
		}
	}

	public static boolean exists(String query) {
		return HAS_EXISTS.contains(query);
	}

	public static boolean existsFav(String taskId, String url) {
		return false;
	}
}
