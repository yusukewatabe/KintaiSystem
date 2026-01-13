package com.jp.Kintai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.jp.Kintai.constant.DateFormatConstant;
import com.jp.Kintai.constant.HomeConstant;
import com.jp.Kintai.constant.MappingPathNameConstant;
import com.jp.Kintai.constant.ViewNameConstant;
import com.jp.Kintai.repository.AttendanceRepository;
import com.jp.Kintai.service.BulkEditService;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FSFontUseCase;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * bulkEditPreview.htmlからリクエストされた処理をするクラス
 * 
 * @author Watabe Yusuke
 * @version 0.1
 */
@Controller
public class BulkEditPreviewController {

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private BulkEditService bulkEditService;

	@Autowired
	private SpringTemplateEngine templateEngine;

	/**
	 * 月の勤怠一覧を表示するメソッド
	 * 
	 * @param userId メールアドレス
	 * @param model  Spring MVC model
	 * @return 表示するビュー名
	 */
	@GetMapping(MappingPathNameConstant.BULKEDITPREVIEW_PATH)
	public String attendancePreview(String userId, Model model) {

		// dbからテーブルの値を取得
		bulkEditService.bulkEditAndPreview(userId, attendanceRepository, model);
		model.addAttribute(HomeConstant.MONTH_VIEW, true);
		return ViewNameConstant.BULKEDITPREVIEW_HTML_PATH;
	}

	/**
	 * 月の勤怠一覧をPDF出力するメソッド
	 * 
	 * @param userId メールアドレス
	 * @return 表示するビュー名
	 */
	@PostMapping("/printPdf")
	public ResponseEntity<byte[]> outputPdf(@RequestParam String userId) throws Exception {

		Model model = new ExtendedModelMap();
		bulkEditService.bulkEditAndPreview(userId, attendanceRepository, model);

		Context context = new Context(Locale.JAPANESE);
		context.setVariables(model.asMap());
		String html = templateEngine.process("html/printPdf", context);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		PdfRendererBuilder builder = new PdfRendererBuilder();
		builder.useFont(
				() -> Objects.requireNonNull(
						BulkEditPreviewController.class.getResourceAsStream("/NotoSansJP-Regular.ttf"),
						"NotoSansJP-Regular.ttf not found on classpath"),
				"NotoSansJP",
				400,
				FontStyle.NORMAL,
				true,
				EnumSet.of(FSFontUseCase.DOCUMENT, FSFontUseCase.FALLBACK_FINAL));
		builder.withHtmlContent(html, null);
		builder.toStream(baos);
		builder.run();

		HttpHeaders headers = new HttpHeaders();
		int month = YearMonth.now(ZoneId.of(DateFormatConstant.TIMEZONE_ASIA_TOKYO)).getMonthValue();
		String filename = month + "月_勤務表.pdf";

		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDisposition(ContentDisposition.builder("inline")
				.filename(filename, StandardCharsets.UTF_8)
				.build());

		return ResponseEntity.ok()
				.headers(headers)
				.body(baos.toByteArray());
	}

}
