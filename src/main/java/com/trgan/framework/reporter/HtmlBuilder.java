package com.trgan.framework.reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HtmlBuilder {
	private final List<ResultStatus> statusRows = Collections.synchronizedList(new ArrayList<>());
	private final List<ResultData> resultRows = Collections.synchronizedList(new ArrayList<>());
	private final List<List<String>> reportLinks = Collections.synchronizedList(new ArrayList<>());

	private final Map<String, Integer> retriedTestCases = new ConcurrentHashMap<>();

	private final String templatePath = "src/main/resources/template/report.html";

	public void addIndividualReport(String testName, int attempt, String filePath, String message, String status) {
		reportLinks.add(Arrays.asList(testName, filePath, "" + attempt, status, message));
//		reportLinks.put(testName, Arrays.asList(testName, filePath, "" + attempt, status, message));
	}

	public void addStatus(ResultStatus sts) {
		boolean executedEarlier = statusRows.removeIf(r -> r.testName.equals(sts.testName));
		if (executedEarlier) {
			retriedTestCases.put(sts.testName, retriedTestCases.getOrDefault(sts.testName, 0) + 1);
		}
		statusRows.add(sts);
	}

	public void addData(ResultData data) {
		resultRows.add(data);
	}

	public void generate(String executor, String mode, String outputPath) {
		try {
			String template = Files.readString(Paths.get(templatePath));
			int total = 0, passed = 0, failed = 0, skipped = 0;
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			LocalTime earliest = null;
			LocalTime latest = null;

			StringBuilder statusHtml = new StringBuilder();
			for (ResultStatus r : statusRows) {
				total++;
				switch (r.result.toUpperCase()) {
				case "PASS" -> passed++;
				case "FAIL" -> failed++;
				case "SKIP" -> skipped++;
				}
				LocalTime start = LocalTime.parse(r.startTime, formatter);
				LocalTime end = LocalTime.parse(r.endTime, formatter);

				if (earliest == null || start.isBefore(earliest)) {
					earliest = start;
				}
				if (latest == null || end.isAfter(latest)) {
					latest = end;
				}

				statusHtml.append("<tr>").append("<td>" + r.testName + "</td>").append("<td>" + r.group + "</td>")
						.append("<td>" + r.startTime + "</td>").append("<td>" + r.endTime + "</td>")
						.append("<td>" + r.duration + "</td>")
						.append("<td class='" + r.result.toLowerCase() + "'>" + r.result + "</td>").append("</tr>\n");

			}

			Duration duration = Duration.between(earliest, latest);
			String overallDuration = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(),
					duration.toSecondsPart());

			String summaryHtml = """
					<div class="summary-panel">
					    <div class="summary-item total">Total Tests: <span>%d</span></div>
					    <div class="summary-item pass">✔ Passed: <span>%d</span></div>
					    <div class="summary-item fail">❌ Failed: <span>%d</span></div>
					    <div class="summary-item skip">⏭ Skipped: <span>%d</span></div>
					    <div class="summary-item duration">⏱ Duration: <span>%s</span></div>
					</div>
					""".formatted(total, passed, failed, skipped, overallDuration);

			String metaLineHtml = """
					<p class="meta-info">
					  🧑‍💻 Executed by: <strong>%s</strong> |
					  🛠️ Mode: <strong>%s</strong> |
					  📅 Date: <strong>%s</strong>
					</p>
					""".formatted(executor, mode, LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

			StringBuilder resultHtml = new StringBuilder();
			for (ResultData q : resultRows) {
				resultHtml.append("<tr>").append("<td>" + q.testCase + "</td>").append("<td>" + q.group + "</td>")
						.append("<td>" + q.quoteId + "</td>").append("<td>" + q.policyId + "</td>")
						.append("<td>" + q.premium + "</td>").append("<td>" + q.date + "</td>")
						.append("<td>" + q.time + "</td>").append("</tr>\n");
			}

			StringBuilder reportLinkHtml = new StringBuilder();
			for (List<String> list : reportLinks) {
				String testName = list.get(0);
				String filePath = list.get(1);
				String attempt = list.get(2);
				String result = list.get(3);
				String message = list.get(4);

				reportLinkHtml.append("<tr>")
						.append("<td><a href='." + filePath + "'title='Click to open report' target='_blank'>"
								+ testName + "</a></td>")
						.append("<td style='width:40px'>" + attempt + "</td>")
						.append("<td class='" + result.toLowerCase() + "'>" + result + "</td>")
						.append("<td style='text-align:left; max-width:200px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;' title='"
								+ message + "'>" + message + "</td>")
						.append("</tr>\n");
			}

			String finalHtml = template.replace("<!-- STATUS_START -->", statusHtml.toString())
					.replace("<!-- SUMMARY_PANEL_START -->", summaryHtml)
					.replace("<!-- META_INFO_LINE -->", metaLineHtml)
					.replace("<!-- DATA_START -->", resultHtml.toString().replaceAll("null", "-"))
					.replace("<!-- PATH_START -->", reportLinkHtml.toString());

			Files.writeString(Paths.get(outputPath), finalHtml, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
