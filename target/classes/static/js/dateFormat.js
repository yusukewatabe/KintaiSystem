import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
dayjs.extend(customParseFormat);

/** Moment.js を使ったフォーマッタ */
export function toHHMMWithMoment(input) {
	console.log('でバック')
	return dayjs(input, ['Hmm', 'HHmm']).format('HH:mm');
}

	export function initAllTimeInputs() {
		console.log('▶ initAllTimeInputs が呼ばれました');
		// 1) まず NodeList を取得
		const inputs = document.querySelectorAll('input[data-format]');

		// 2) ループごとにイベント登録
		inputs.forEach(el => {
				// ループ内でフォーマット文字列を取得しておく
				const fmt = el.dataset.format;

				// Blur イベント登録
				el.addEventListener('blur', () => {
				console.log('blur 発火:', el.value, '→ format=', fmt);

				if (fmt === 'HHmm') {
					el.value = toHHMMWithMoment(el.value);
				}
				// もし他のフォーマットも増やすなら
				// else if (fmt === 'YYYYMMDD') { ... }
			});
		});
	}

// ページ読み込み後に一括初期化
document.addEventListener('DOMContentLoaded', () => {
	initAllTimeInputs();
});