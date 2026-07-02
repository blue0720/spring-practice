# 05. 進行状況の保存・読込API

対応フェーズ: フェーズ4

## 概要
ログインユーザーごとに1つの進行状況(セーブデータ)をDBに永続化するAPIを実装する。

## Todo
- [〇] `GET /api/progress`: 該当ユーザーの進行状況取得(`ROLE_PLAYER`以上)
- [〇] `POST /api/progress`: 進行状況の保存(Ajax/fetch経由、`ROLE_PLAYER`以上)
- [〇] 保存内容の設計: 現在の章、区画ごとの交渉済みフラグ、最終交渉の結果など
- [〇] `GET /game` アクセス時にDBから進行状況を読み込み、Thymeleafで初期状態として埋め込む(またはJS側からAPI取得)
- [〇] 交渉パズル1件完了 or マップ帰還タイミングで `POST /api/progress` を呼び出すフロント側実装
- [〇] `CompletedEncounter` への交渉履歴記録(誰といつどんな結果で交渉したか)
- [〇] 複数セーブスロットは非対応であることの確認(スコープ外)

## 参照
- `02_要件定義書.md` セクション3.2「ゲーム進行機能」、セクション5「画面・URL設計」

## メモ
- `CompletedEncounter` の履歴をUI上でどう見せるかは未決事項(チケット09または将来課題)。
- 実装メモ:
  - `GameProgress` に `@ElementCollection Set<String> clearedDistrictIds`(専用テーブル `game_progress_cleared_district`)を追加し、区画ごとの交渉済みフラグを表現。district/npcのマスタデータは未投入(チケット07)のため、フロントは既存の固定ID(`slum`/`guild`/`backalley`)をそのまま送信する設計とした。
  - `POST /api/progress` は `currentChapter`・`clearedDistrictIds`(差分をマージ)・任意の `lastEncounter`(districtId/npcName/outcome/gauge)を受け取り、`lastEncounter` があれば `CompletedEncounter` を1件追加する。`npcName` はベストエフォートで `Npc` を名前検索して紐付け、Npcマスタが未投入の間は `npc` が `null` のまま保存される(チケット07投入後に自動的にリンクされる)。
  - `GET /game` のThymeleaf側では進行状況を埋め込まず、`game.js` の `loadProgress()` がJS側からAPI取得する方式を採用(チケット記載の代替案)。
  - CSRF対策として `game.html` に `<meta name="_csrf">` / `<meta name="_csrf_header">` を追加し、`game.js` からfetchのヘッダーに設定(セッションCookie認証のためCSRF保護は無効化していない)。
  - 保存・取得ともcurlで手動疎通確認済み(登録→ログイン→POST/GETの一連のフロー、未認証時の302リダイレクト、バリデーションエラー時の400を確認)。
