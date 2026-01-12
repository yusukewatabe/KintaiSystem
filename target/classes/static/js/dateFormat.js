dayjs.extend(window.dayjs_plugin_customParseFormat);

/**
 * 全角英数字・記号を半角に変換する関数
 * （全角「０～９」「：」も半角に変換される）
 */
export function toHalfWidth(str) {
	return String(str)
		// 全角英数字・記号（FF01〜FF5E）を半角（U+0021〜U+007E）に
		.replace(/[\uFF01-\uFF5E]/g, ch =>
			String.fromCharCode(ch.charCodeAt(0) - 0xFEE0)
		)
		// 全角スペース U+3000 → 半角スペース
		.replace(/\u3000/g, ' ');
}

/**
 * 許可文字チェック
 * 全角／半角の 0-9 と 全角／半角のコロンのみ許可
 */
export function isAllowedChars(str) {
	// 半角 0-9, 全角 ０-９, 半角 :, 全角 ： のみ
	if (str === '') return true;
	const re = /^[0-9\uFF10-\uFF19:\uFF1A]+$/;
	return re.test(str);
}

/** Day.js を使ったフォーマッタ */
export function toHHMMWithMoment(input) {
	// 「:」を除去して数字だけに
	let hhPart, mmPart;
	if (input.includes(':')){
		const [hhRaw, mmRaw] = input.split(':');
		hhPart = hhRaw;
		mmPart = mmRaw;
	} else {
		if (input.length <= 2) {
			hhPart = input;
			mmPart = '0';
		} else {
			hhPart = input.slice(0, input.length - 2);
			mmPart = input.slice(-2);
		}
	}
		// zeroPadding = input.replace(/:/g, '');
	// 4 桁になるよう左ゼロ埋め ("900"→"0900")

	// 数値にパース
	const hhNum = parseInt(hhPart, 10);
	const mmNum = parseInt(mmPart, 10);

	// 範囲チェック
	if (hhNum < 0 || hhNum > 23) {
		alert('時間は 00～23 の間で入力してください');
		return '';
	} else if(isNaN(hhNum)){
		return '';
	}
	if (mmNum < 0 || mmNum > 59) {
		alert('分は 00～59 の間で入力してください');
		return '';
	}

	// ２桁パディングして組み立て
	const hh = hhNum.toString().padStart(2, '0');
	const mm = mmNum.toString().padStart(2, '0');
	let hhMm = hh + mm;
	return dayjs(hhMm, ['Hmm', 'HHmm']).format('HH:mm');
}

export function initAllTimeInputs() {
	document.querySelectorAll('input[data-format="HHmm"]').forEach(el => {
		el.addEventListener('input', () => {
		const normalized = toHalfWidth(el.value).replace(/\s+/g, '');
			if (el.value !== normalized) {
				const pos = el.selectionStart;
				el.value = normalized;
				// 可能ならキャレット位置を維持
				try { el.setSelectionRange(pos, pos); } catch {}
			}
		});
		el.addEventListener('blur', () => {
			let raw = el.value;
			
			// 許可文字以外が含まれていないかチェック
			if (!isAllowedChars(raw)) {
				alert('数字とコロン(:)以外の文字は使えません');
				// el.value = '';
				return;
			}
			
			// 全角→半角に統一
			raw = toHalfWidth(raw);
			
			// strict モードでパース
			const d = toHHMMWithMoment(raw);
			
			// フォーマットして書き戻し
			el.value = d;
		});
	});
}

const DISABLED_AT_TYPES = new Set(['現場休', '有給', '欠勤', '代休']);

function updateTimeInputsForAtType(selectEl) {
	const row = selectEl.closest('tr');
	if (!row) return;

	const shouldDisable = DISABLED_AT_TYPES.has(selectEl.value);
	const inputs = row.querySelectorAll(
		'input[name="clockInTimeList"], input[name="clockOutTimeList"], input[name="breakStartList"], input[name="breakEndList"]'
	);

	inputs.forEach(input => {
		if (shouldDisable) {
			input.value = '';
			input.readOnly = true;
			input.classList.add('bg-gray-100', 'text-gray-500');
			input.setAttribute('aria-disabled', 'true');
		} else {
			input.readOnly = false;
			input.classList.remove('bg-gray-100', 'text-gray-500');
			input.removeAttribute('aria-disabled');
		}
	});
}

export function initAtTypeControls() {
	const selects = document.querySelectorAll('select[name="atTypeList"]');
	if (selects.length === 0) return;

	selects.forEach(selectEl => {
		updateTimeInputsForAtType(selectEl);
		selectEl.addEventListener('change', () => {
			updateTimeInputsForAtType(selectEl);
		});
	});
}

// ページ読み込み後に一括初期化
document.addEventListener('DOMContentLoaded', () => {
	initAllTimeInputs();
	initAtTypeControls();
});