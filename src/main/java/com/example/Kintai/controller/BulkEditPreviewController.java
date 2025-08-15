package com.example.Kintai.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.example.Kintai.constant.MappingPathNameConstant;
import com.example.Kintai.constant.ViewNameConstant;
import com.example.Kintai.repository.AttendanceRepository;
import com.example.Kintai.service.BulkEditService;
import org.springframework.ui.Model;

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

	/**
	 * 月の勤怠一覧を表示するメソッド
	 * 
	 * @param userId メールアドレス
	 * @param model Spring MVC のモデルオブジェクト
	 * @return 表示するビュー名
	 */
	@PostMapping(MappingPathNameConstant.BULKEDITPREVIEW_PATH)
	public String attendancePreview(String userId, Model model) {

		// dbからテーブルの値を取得
		bulkEditService.bulkEditAndPreview(userId, attendanceRepository, model);
		return ViewNameConstant.BULKEDITPREVIEW_HTML_PATH;
	}

}
