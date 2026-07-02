# 06. 管理者機能(コンテンツCRUD)

対応フェーズ: フェーズ5

## 概要
`ROLE_ADMIN` 専用の管理画面を実装し、ゲームコンテンツ(NPC・記憶断片・区画・章)をCRUD管理できるようにする。

## Todo
- [〇] `GET /admin` 管理画面トップ(`ROLE_ADMIN`のみ)
- [〇] NPC管理: `GET/POST /admin/npcs`, `/admin/npcs/{id}` の一覧・新規作成・編集・削除
- [〇] 記憶断片管理: `/admin/memories` のCRUD
- [〇] 区画(District)管理: `/admin/districts` のCRUD
- [〇] 章(Chapter)管理: `/admin/chapters` のCRUD
- [〇] NPCの「弱点タグ」「苦手タグ」「反応テキスト(良い/悪い/普通、複数)」を編集できるフォーム設計
- [〇] 各CRUDフォームに `spring-boot-starter-validation` でサーバーサイドバリデーション
- [〇] `/admin/**` へのアクセス制御(`ROLE_ADMIN`)がチケット03のSecurityConfigと連動していることを確認

## 参照
- `02_要件定義書.md` セクション3.3「コンテンツ管理機能」、セクション5「画面・URL設計」

## 実装メモ

### 構成
Controller → Service → Repository の3層をNPC/記憶断片/区画/章の4リソースそれぞれで踏襲。
- `AdminController`: `GET /admin` のトップページ(各管理画面へのリンクのみ)。
- `Admin{Npc,MemoryFragment,District,Chapter}Controller`: 一覧・新規作成フォーム・編集フォーム・作成・更新・削除の6アクション構成。
  URLパターンは `GET /admin/{resource}`(一覧)、`GET .../new`(新規作成フォーム)、`GET .../{id}/edit`(編集フォーム)、
  `POST /admin/{resource}`(作成)、`POST .../{id}`(更新)、`POST .../{id}/delete`(削除)に統一。
- `Admin{Npc,MemoryFragment,District,Chapter}Service`: フォームDTO⇔エンティティの変換と永続化を担当。`@Transactional` で
  読み取り専用/更新を明示的に分離。

### フォームDTOと一覧行DTO
- `NpcForm`/`MemoryFragmentForm`/`DistrictForm`/`ChapterForm` に `spring-boot-starter-validation` の
  `@NotBlank`/`@NotNull`/`@Min` を付与し、Controllerで `@Valid` + `BindingResult` により入力エラーをフォームへ差し戻す
  (チケット03の `AuthController`/`register.html` と同じ Thymeleaf `#fields.hasErrors` パターン)。
- `application.properties` の `spring.jpa.open-in-view=false` により、Thymeleafのレンダリングはコントローラの
  トランザクション終了後に行われるため、遅延ロードの関連(`Npc.chapter`、`District.chapter`/`npc`)をテンプレートで
  直接参照すると `LazyInitializationException` になる(チケット05の `ProgressService` で踏んだのと同じ問題)。
  一覧表示は `NpcRow`/`DistrictRow` という表示専用DTOを新設し、Service内のトランザクション内で
  関連先の名称(`chapterTitle`/`npcName`)まで解決してから返すことで回避した。編集フォームのプルダウン用データ
  (`chapters`/`npcs` 一覧)はスカラー値(id/name)のみ参照するため、エンティティをそのままModelに詰めても安全。

### NPCの反応テキスト(複数行)フォーム
- `NpcReactionLine` は「良い/悪い/普通」×複数件をNPCに紐づけるコレクション。動的な行追加をJSなしで実現するため、
  編集フォームでは既存の行に加えて末尾に空行を3行追加した状態で表示し(`AdminNpcService` の `BLANK_REACTION_ROWS`)、
  Thymeleafの `th:each="line, stat : *{reactionLines}"` + `*{reactionLines[__${stat.index}__].type}` という
  インデックス束縛パターンでリストごと送信・受信する。保存時は既存行を全削除してフォームの内容で作り直す
  (delete-then-recreate)。空欄(text未入力)の行は無視するため、余分な空行を送信しても不要なレコードは作られない。
  行数が足りない場合は一度保存すれば編集画面に再度空行が追加されるため、単純な仕組みのまま実用上困らない。

### アクセス制御の確認方法
チケット03で設定済みの `SecurityConfig`(`.requestMatchers("/admin/**").hasRole("ADMIN")`)を変更せずに
そのまま利用し、`AdminAccessControlTest` で「未認証→ログインへリダイレクト」「`ROLE_PLAYER`→403」
「`ROLE_ADMIN`→200」の3パターンを確認した。

### テスト
`spring-security-test` の `@WithMockUser` を用いて以下を追加(全て `./mvnw test` で確認済み、14件全て成功):
- `AdminAccessControlTest`: `/admin` に対するロールベースのアクセス制御。
- `AdminChapterControllerTest`: 章の作成→編集→削除のCRUDフローとバリデーションエラー時のフォーム差し戻し。
- `AdminNpcControllerTest`: 反応テキストの空欄行が保存されないこと、編集フォームが正しく表示されること。
- `AdminViewsSmokeTest`: NPC/記憶断片/区画/章の一覧・新規作成フォームが(データが空の状態でも)正しくレンダリングされること。
