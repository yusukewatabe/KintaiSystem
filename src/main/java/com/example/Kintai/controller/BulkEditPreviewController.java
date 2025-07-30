package com.example.Kintai.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import com.example.Kintai.repository.AttendanceRepository;
import com.example.Kintai.service.BulkEditService;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
public class BulkEditPreviewController {

	private final AttendanceRepository attendanceRepository;

	/** bulkEditPreviewのMappingPath */
	private static final String BULKEDITPREVIEW_PATH = "/bulkEditPreview";

	/** bulkEditPreview.htmlへの遷移Path */
	private static final String BULKEDITPREVIEW_HTML_PATH = "html/bulkEditPreview";

	@Autowired
	private BulkEditService bulkEditService;

	/**
	 * 月の勤怠一覧を表示するメソッド
	 * 
	 * @param userId メールアドレス
	 * @param model  モデル
	 * @return bulkEditPreview.htmlを表示
	 */
	@PostMapping(BULKEDITPREVIEW_PATH)
	public String handleClockAction(String userId, Model model) {

		// dbからテーブルの値を取得
		bulkEditService.bulkEditAndPreview(userId, attendanceRepository, model);
		return BULKEDITPREVIEW_HTML_PATH;
	}

}
