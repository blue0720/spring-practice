# wakyaku-content MCP server

『忘却の取引』のコンテンツデータ(章・区画・NPC・NPC反応テキスト・記憶断片)をClaudeから
CRUD操作するためのMCPサーバー。MySQL(`wakyaku_dev`)に直接接続する。

ユーザー・進行状況・交渉履歴(`app_user` / `game_progress` / `completed_encounter`)は
読み取り専用ツールのみを提供する(書き込みはSpring Boot側の会員機能・進行状況APIが担当)。

## テーブル設計について

起動時に `CREATE TABLE IF NOT EXISTS` でテーブルを自動作成する(`src/db.ts`)。
テーブル名・カラム名は `docs/02_jpa-entities.md` のエンティティ設計と、Spring Bootの
デフォルト物理命名戦略(キャメルケース→スネークケース)に合わせてある。
`User` エンティティはMySQL 8で `user` が予約語のため `app_user` テーブルにマッピングする
想定。フェーズ1でJPAエンティティを実装する際は `@Table(name = "app_user")` を指定すること。

## セットアップ

```cmd
cd mcp-server
npm install
npm run build
```

## Claude Codeへの登録

パスワードはこのリポジトリのどのファイルにも書き込まない。登録時に `--env` で渡す
(値はClaude Codeのローカル設定に保存される。リポジトリにはコミットされない)。

```cmd
claude mcp add wakyaku-content ^
  --env DB_PASSWORD=実際のパスワード ^
  -- node C:\Users\k_tai\OneDrive\Desktop\Claud_Code_practice\mcp-server\dist\index.js
```

必要に応じて `DB_HOST` / `DB_PORT` / `DB_USER` / `DB_NAME` も `--env` で上書き可能
(デフォルトはそれぞれ `localhost` / `3306` / `root` / `wakyaku_dev`)。

**スコープに注意**: 上記コマンドはデフォルトで `-s local`(自分のローカル設定にのみ保存、
Gitには含まれない)。`-s project` を使うと `.mcp.json` がリポジトリ直下に作られ、
Git管理下に入る可能性があるため、パスワードを渡す場合は `local` スコープのままにすること
(念のため `.mcp.json` は `.gitignore` 済み)。

## 提供ツール

- 章(Chapter): `list_chapters` / `upsert_chapter` / `delete_chapter`
- NPC: `list_npcs` / `upsert_npc` / `delete_npc`
- NPC反応テキスト: `list_npc_reaction_lines` / `add_npc_reaction_line` / `delete_npc_reaction_line`
- 区画(District): `list_districts` / `upsert_district` / `delete_district`
- 記憶断片(MemoryFragment): `list_memory_fragments` / `upsert_memory_fragment` / `delete_memory_fragment`
- 読み取り専用: `list_users` / `list_game_progress` / `list_completed_encounters`

## 開発

```cmd
npm run dev    # ビルドしてそのまま起動(stdio)
```
