package com.osidocker.open.micro.utils;

import cn.hutool.core.text.StrFormatter;
import com.google.common.base.Splitter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SQLFileParser {

	static String source = "";
	static String basePath = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql";
	static Map<String,List<String>> sqllines = new HashMap<>();
//    public static void main(String[] args) throws IOException {
////		generate();
//		String configdbInMysql = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/mysql/banben192configdb";
//		String configdbInOracle = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/oracle/APOLLOCONFIGDB";
//		diff(configdbInMysql, configdbInOracle);
//		String portalInMysql = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/mysql/banben192portaldb";
//		String portalInOracle = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/oracle/APOLLOPORTALDB";
//		diff(portalInMysql, portalInOracle);
//		String authDbInMysql = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/mysql/banben_authdb";
//		String authDbInOracle = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/oracle/ESB_AUTHDB";
//		diff(authDbInMysql, authDbInOracle);
//		String jobDbInMysql = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/mysql/banben_jobdb";
//		String jobDbInOracle = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/oracle/ESCXXLJOB_T";
//		diff(jobDbInMysql, jobDbInOracle);
//		String mngDbInMysql = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/mysql/banben_mngdb";
//		String mngDbInOracle = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/oracle/ESB";
//		diff(mngDbInMysql, mngDbInOracle);
//		String reportInMysql = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/mysql/banben_reportdb";
//		String reportInOracle = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/oracle/ESCREPORTDB";
//		diff(reportInMysql, reportInOracle);
//		String sgDbInMysql = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/mysql/banben_sgdb";
//		String sgDbInOracle = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/oracle/C##ESCSG";
//		diff(sgDbInMysql, sgDbInOracle);
//		String bladeInMysql = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/mysql/blade_app";
//		String bladeInOracle = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/oracle/ESB";
//		diff(bladeInMysql, bladeInOracle);
//    }

	private static void generate() throws IOException {
		List<String> searchIn = new ArrayList<>();
		searchIn.add("/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/mysql");
		searchIn.add("/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/oracle");
		List<String> files = getFilePathBy(basePath, searchIn, ".sql");
		for( String sql : files ) {
			analysis(sql);
		}
	}

	private static void diff(String baseMysqlPath, String baseOraclePath) throws IOException {
		String diffBasePath = "/storage/software/gitea/njcb-worker-space/smart-station-service/smart-open-services/smart-open-architecture/src/main/resources/sql/diff";
		String diffAppendPath = new File(baseMysqlPath).getName();
		List<String> searchIn = new ArrayList<>();
		searchIn.add(baseMysqlPath);
		searchIn.add(baseOraclePath);
		List<String> files = getFilePathBy(basePath, searchIn, ".txt");
		Map<String, List<String>> sameNamePaths = new HashMap<>();
		List<String> compareTable = Arrays.asList(new File(baseMysqlPath).list()).stream().filter(line->line.endsWith(".txt")).collect(Collectors.toList());
		for( String fileString : files ) {
			for(String tabletxt : compareTable) {
				if (fileString.replaceAll("_0","").endsWith("/"+tabletxt)) {
					sameNamePaths.computeIfAbsent(tabletxt,t->new ArrayList<>()).add(fileString);
				}
			}
		}
		StringBuffer buffer = new StringBuffer();
		sameNamePaths.forEach((k,v)->{
			if( v.size()==2 ) {
				try {
					diffTable(diffBasePath+File.separator+diffAppendPath, k,v.get(0), v.get(1));
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			} else {
				buffer.append(StrFormatter.format("{}->[\n{}\n]", k , sameNamePaths.get(k).stream().collect(Collectors.joining("\n"))));
			}
		});
		if( StringUtils.isNotBlank(buffer.toString() ) ) {
			writeSQLFile(diffBasePath+File.separator+diffAppendPath, "notfound", buffer.toString(), true);
		}
	}

	private static void diffTable(String diffAppendPath, String tableName, String table1, String table2) throws IOException {
		List<String> table1Fields = Files.readAllLines(new File(table1).toPath()).stream().map(field->field.trim()).filter(field->field.startsWith("\"")||field.startsWith("`")).collect(Collectors.toList());
		List<String> table2Fields = Files.readAllLines(new File(table2).toPath()).stream().map(field->field.trim()).filter(field->field.startsWith("\"")||field.startsWith("`")).collect(Collectors.toList());
		List<TableField> table1s = convertTableField(table1Fields);
		List<TableField> table2s = convertTableField(table2Fields);
		new TableEntity(diffAppendPath,tableName,table1s,table2s).compare();
	}

	private static List<TableField> convertTableField(List<String> tableFields) {
		return tableFields.stream().map(line->{
			TableField field = new TableField();
			field.setLine(line);
			List<String> infos = Splitter.on(" ").trimResults().omitEmptyStrings().splitToList(line);
			if( infos.get(0).contains("`") ) {
				field.setDbType("mysql");
			} else {
				field.setDbType("oracle");
			}
			field.setFieldName(infos.get(0).replaceAll("`","").replaceAll("\"","").toLowerCase());
			if( infos.get(1).contains("(") && !infos.get(1).contains(")") ) {
				field.setType(infos.get(1)+infos.get(2));
			} else {
				field.setType(infos.get(1));
			}
			if( field.getType().contains("(") ) {
				Pattern pattern = Pattern.compile("\\d+");
				Matcher matcher = pattern.matcher(field.getType().substring(field.getType().indexOf("(")));
				while (matcher.find()) {
					String number = matcher.group(0); // 获取括号中的数字
					field.setSize(number);
				}
			} else {
				field.setSize(Integer.MAX_VALUE+"");
			}
			return field;
		}).collect(Collectors.toList());
	}

	@Data
	private static class TableField {
		private String dbType;
		private String fieldName;
		private String type;
		private String line;
		private String size;
	}

	@Data
	private static class DiffEntity {
		private String tableName;
		private boolean columnEquals;
		private String onlyInT1;
		private String onlyInT2;
		private String allIn;
		private String sizeChange;
		private String fieldOnlyIn;

		@Override
		public String toString() {
			if( StringUtils.isNotBlank(onlyInT2) || StringUtils.isNotBlank(sizeChange) || !columnEquals ) {
				return StrFormatter.format( "diff table : {}\n",tableName)
//					+ StrFormatter.format("===========field only in oralce script====\n\n{}\n======= end =======\n",onlyInT1)
//					+ StrFormatter.format("===========field both script all has====\n\n{}\n======= end =======\n",allIn)
					+ "===============================================需要调整sql的内容===========================================================\n"
					+ StrFormatter.format("===========field only in mysql script====\n\n{}\n======= end =======\n",onlyInT2)
					+ StrFormatter.format("===========field only in mysql script====\n\n{}\n======= end =======\n",fieldOnlyIn)
					+ StrFormatter.format("===========field changes ==========\n\n{}\n========= end ============\n",sizeChange)
					;
			} else {
				return "";
			}
		}
	}
	@Data
	@AllArgsConstructor
	private static class TableEntity {
		private String basePath;
		private String tableName;
		private List<TableField> table1;
		private List<TableField> table2;

		public void compare() {
			Map<String, TableField> t1m = new HashMap<>();
			Map<String, TableField> t2m = new HashMap<>();
			for (TableField tableField : table1) {
				if (tableField.getDbType().equalsIgnoreCase("oracle")) {
					t1m.put(tableField.fieldName, tableField);
				} else {
					t2m.put(tableField.fieldName, tableField);
				}
			}
			for (TableField tableField : table2) {
				if (tableField.getDbType().equalsIgnoreCase("oracle")) {
					t1m.put(tableField.fieldName, tableField);
				} else {
					t2m.put(tableField.fieldName, tableField);
				}
			}
			DiffEntity diff = new DiffEntity();
			diff.setTableName(tableName.replace("_fields.txt",""));
			diff.setColumnEquals(t1m.keySet().equals(t2m.keySet()));
			Set<String> commonElements = new HashSet<>(t1m.keySet());
			commonElements.retainAll(t2m.keySet()); // 获取两个Set的交集
			diff.setAllIn(commonElements.stream().collect(Collectors.joining("\n")));
			Set<String> uniqueElementsInSet1 = new HashSet<>(t1m.keySet());
			uniqueElementsInSet1.removeAll(t2m.keySet()); // 获取set1独有的元素
			diff.setOnlyInT1(uniqueElementsInSet1.stream().collect(Collectors.joining("\n")));
			Set<String> uniqueElementsInSet2 = new HashSet<>(t2m.keySet());
			uniqueElementsInSet2.removeAll(t1m.keySet()); // 获取set2独有的元素
			diff.setOnlyInT2(uniqueElementsInSet2.stream().collect(Collectors.joining("\n")));
			StringBuffer sizeChanges = new StringBuffer();
			StringBuffer fieldOnlyInMysql = new StringBuffer();
			for(String key : t2m.keySet()) {
				if( !t1m.containsKey(key) ) {
					fieldOnlyInMysql.append(t2m.get(key).getLine()).append("\n");
					continue;
				}
				String t2Size = t1m.get(key).getSize();
				String t1Size = t1m.get(key).getSize();
				if( Integer.valueOf(t1Size) >= Integer.valueOf(t2Size) ) {
					continue;
				}
				if( !t1m.get(key).getSize().equalsIgnoreCase(t2m.get(key).getSize()) ) {
					sizeChanges.append(StrFormatter.format("field: {}, mysql length => oracle length : [{} => {}]\n",key, t2Size, t1Size));
				}
			}
			diff.setSizeChange(sizeChanges.toString());
			if( StringUtils.isNotBlank(diff.toString()) ){
				writeSQLFile(basePath, tableName.replace("_fields.txt",""), diff.toString(), true);
			}
		}
	}

	private static List<String> getFilePathBy(String basePath,List<String> searchIns, String affix) throws IOException {
		List<String> files = new ArrayList<>();
		Path startingDir = FileSystems.getDefault().getPath(basePath);
		Files.walkFileTree(startingDir, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				if (file.getFileName().toString().toLowerCase().endsWith(affix)) {
					String affixPath = file.toFile().getAbsolutePath();
					searchIns.forEach(search->{
						if( affixPath.startsWith(search) ) {
							files.add(affixPath);
						}
					});
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) {
				return FileVisitResult.CONTINUE;
			}
		});
		return files;
	}

	private static void analysis(String sqlPath) {
		List<String> sources = Splitter.on("/").trimResults().omitEmptyStrings().splitToList(sqlPath);
		source = sources.get(sources.size()-1).replace(".sql","");
		String basePath = new File(sqlPath).getParent();
		try (BufferedReader br = new BufferedReader(new FileReader(sqlPath))) {
			String line;
			StringBuilder sqlStatement = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if( line.startsWith("--") ) {
					continue;
				}
				sqlStatement.append(line).append("\n");
				if(sqlStatement.toString().trim().endsWith(";")) {
					processSQLStatement(basePath, sqlStatement.toString());
					sqlStatement.setLength(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		sqllines.forEach((tableName,sqls)->{
			writeSQLFile(basePath, tableName, sqls.stream().collect(Collectors.joining("\n")),false);
		});
		sqllines.clear();
	}

    public static void processSQLStatement(String basePath, String sql) {
		String tableName = extractTableName(sql);
        if (isCreateTableStatement(sql)) {
			sqllines.computeIfAbsent(tableName,t->new ArrayList<>()).add(sql);
			extractTableField(basePath, tableName, sql);
        } else if(isInsert(sql)) {
			sqllines.computeIfAbsent(tableName,t->new ArrayList<>()).add(sql);
		} else {
			System.err.println(sql);
		}
    }

	private static void extractTableField(String basePath, String tableName, String sql) {
		StringBuffer fields = new StringBuffer("");
		Splitter.on("\n").trimResults().omitEmptyStrings().splitToList(sql).stream().filter(line->line.startsWith("\"")||line.startsWith("`")).forEach(fieldLine->{
			fields.append(fieldLine).append("\n");
		});
		writeSQLFile(basePath, tableName+"_fields", fields.toString(), true);
	}

	private static boolean isInsert(String sql) {
		return sql.replaceAll("\n","").startsWith("INSERT INTO ");
	}

    public static boolean isCreateTableStatement(String sql) {
        return sql.toUpperCase().contains("CREATE TABLE");
    }

    public static String extractTableName(String sql) {
        // 从sql语句中提取表名
		String tables="";
		try{
			tables = sql.split(" ")[2].replaceAll("`","").replaceAll("\"","");
			if( tables.contains(".") ) {
				return tables.split("\\.")[1].toLowerCase(Locale.ROOT);
			}
		} catch(Exception e){
			System.err.println(sql);
		} finally {
		}
        return tables.toLowerCase(Locale.ROOT);
    }

    public static void writeSQLFile(String basePath, String tableName, String writeStringValue, boolean isTxt) {
		File file;
		if(StringUtils.isEmpty(source)) {
			 file = new File(basePath);
		} else {
			 file = new File(basePath+File.separator+source);
		}
		if( !file.getAbsoluteFile().exists() ) {
			file.mkdirs();
		}
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(basePath+(StringUtils.isEmpty(source)?"":File.separator+source)+File.separator+tableName + (isTxt ? ".txt" : ".sql")))) {
            bw.write(writeStringValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
