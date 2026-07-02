# 10. コンテンツ管理用MCPサーバー

対応フェーズ: 補助ツール(本編フェーズ0〜8とは別枠)

## 概要
Claudeから『忘却の取引』のコンテンツデータ(章・区画・NPC・記憶断片など)を直接操作できる
MCPサーバーを `mcp-server/` に構築する。MySQL(`wakyaku_dev`)に直接接続し、フェーズ1
(`docs/02_jpa-entities.md`)のエンティティ設計と揃えたテーブルを起動時に自動作成する。

## Todo
- [〇] TypeScript/Node.jsプロジェクトの雛形作成(`mcp-server/package.json`, `tsconfig.json`)
- [〇] `db.ts`: MySQL接続プールと `CREATE TABLE IF NOT EXISTS` によるスキーマ初期化
- [〇] 章(Chapter) CRUDツール(`list_chapters` / `upsert_chapter` / `delete_chapter`)
- [〇] NPC CRUDツール(`list_npcs` / `upsert_npc` / `delete_npc`)
- [〇] NPC反応テキストCRUDツール(`list_npc_reaction_lines` / `add_npc_reaction_line` / `delete_npc_reaction_line`)
- [〇] 区画(District) CRUDツール(`list_districts` / `upsert_district` / `delete_district`)
- [〇] 記憶断片(MemoryFragment) CRUDツール(`list_memory_fragments` / `upsert_memory_fragment` / `delete_memory_fragment`)
- [〇] 読み取り専用ツール(`list_users` / `list_game_progress` / `list_completed_encounters`)
- [〇] ビルド確認・stdio経由での初期化/ツール一覧/CRUD呼び出しの動作確認
- [〇] README作成(セットアップ手順、Claude Codeへの登録方法、スコープ注意事項)
- [〇] `claude mcp add` で実際にClaude Codeへ登録し、対話から操作できることを確認(ユーザー作業)
- [〇] フェーズ1でJPAエンティティを実装する際、`app_user` テーブル名との整合を確認

## 参照
- `docs/02_jpa-entities.md`(テーブル設計の一致元)
- `mcp-server/README.md`

## メモ
- パスワードはリポジトリの追跡対象ファイルに一切書き込まない方針を踏襲し、`DB_PASSWORD`
  等は `claude mcp add --env` 経由(ローカルスコープ)で渡す運用とした。
- `user` はMySQL 8の予約語のため、Userエンティティは `app_user` テーブルにマッピングする
  前提で設計している。フェーズ1で`@Table(name = "app_user")`を付与すること。
