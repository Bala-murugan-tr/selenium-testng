package com.trgan.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HtmlBuilder {
	private final List<ResultStatus> statusRows = new ArrayList<>();
	private final List<ResultData> resultRows = new ArrayList<>();
	private final Map<String, String> reportLinks = new LinkedHashMap<>();
	private final String templatePath = "src/main/resources/template/report.html";

	public void addIndividualReport(String testName, String filePath) {
		reportLinks.put(testName, filePath);
	}

	public void addStatus(ResultStatus sts) {
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

				statusHtml.append("<tr>").append("<td>").append(r.testName).append("</td>").append("<td>")
						.append(r.group).append("</td>").append("<td>").append(r.startTime).append("</td>")
						.append("<td>").append(r.endTime).append("</td>").append("<td>").append(r.duration)
						.append("</td>").append("<td class='").append(r.result.toLowerCase()).append("'>")
						.append(r.result).append("</td>").append("</tr>\n");

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

			String metaLine = """
					<p class="meta-info">
					  🧑‍💻 Executed by: <strong>Automation Tester</strong> |
					  🛠️ Mode: <strong>LOCAL</strong> |
					  📅 Date: <strong>21 Jul 2025</strong>
					</p>
					""".formatted(executor, mode, LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

			StringBuilder resultHtml = new StringBuilder();
			for (ResultData q : resultRows) {
				resultHtml.append("<tr>").append("<td>").append(q.testCase).append("</td>").append("<td>")
						.append(q.quoteId).append("</td>").append("<td>").append(q.policyId).append("</td>")
						.append("<td>").append(q.premium).append("</td>").append("<td>").append(q.date).append("</td>")
						.append("<td>").append(q.time).append("</td>").append("</tr>\n");
			}

			StringBuilder linkRows = new StringBuilder();
			for (Map.Entry<String, String> entry : reportLinks.entrySet()) {
				linkRows.append("<tr>").append("<td><a href='.").append(entry.getValue()).append(" ' target='_blank'>")
						.append(entry.getKey()).append("</a></td>").append("</tr>\n");
			}
			String finalHtml = template.replace("<!-- STATUS_START -->", statusHtml.toString())
					.replace("<!-- SUMMARY_PANEL_START -->", summaryHtml).replace("<!-- META_INFO_LINE -->", metaLine)
					.replace("<!-- DATA_START -->", resultHtml.toString().replaceAll("null", "-"))
					.replace("<!-- PATH_START -->", linkRows.toString());

			Files.writeString(Paths.get(outputPath), finalHtml, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
