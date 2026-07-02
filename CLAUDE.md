# CLAUDE.md

このファイルは、このリポジトリで作業するClaude Code (claude.ai/code) 向けのガイドです。

## プロジェクト概要

このリポジトリでは、ダークファンタジー×心理をテーマにした短編アドベンチャーRPG
**『忘却の取引』**を、Spring Bootバックエンドで実装する。SES面談等で技術的な深さを
語れるポートフォリオとして開発する位置づけであり、既存の vanilla-JS MVP のゲームロジック
(交渉パズルの信頼度ゲージ・相性判定)を移植しつつ、Spring Boot側は会員機能・進行状況の
永続化・管理者向けコンテンツ管理を担当する。

原典資料(このリポジトリ外・デスクトップ直下):
- `C:\Users\k_tai\OneDrive\Desktop\01_企画書.md` — ゲームデザイン(物語・キャラクター・世界観・交渉パズルのルール・相性表・UI/配色方針)
- `C:\Users\k_tai\OneDrive\Desktop\02_要件定義書.md` — 技術要件(下記はこの内容の要約。詳細は原典を参照)

開発タスクは `docs/` 配下にフェーズ単位のチケット(`docs/01_...md` 〜 `docs/09_...md`)として
分割管理している。各チケットのTodoは `- [ ]`(未着手)→ `- [〇]`(完了) で更新する。
作業を始める際は `docs/README.md` を確認すること。

### ゲームデザイン概要(詳細は01_企画書.mdを参照)
- 主人公ヴィオラは記憶を売買する商人。相棒セレーネは、実はヴィオラがかつて売った
  「妹を守るための記憶」が意識を持つに至った存在(終盤で判明)。舞台は都市「灰塵市」の
  3区画(貧困街・ギルド本部・裏路地)。全3章構成の一本道シナリオ。
- コアゲームプレイは交渉パズル: 手持ちの記憶断片(4ペア・8種の感情タグ: 喜び/悲しみ、
  安心/恐怖、誇り/恥、愛情/怒り)をインベントリから選んで出し、NPCの信頼度ゲージを
  上下させる。相性表は全依頼共通の1枚のルール表(NPCタイプは4種: 孤独な貧困層/
  ギルド幹部・権力者/裏市場の人間/喪失を抱えた市民)。記憶の加工・組み合わせ要素はなし。
- 画面構成は7画面: タイトル/マップ(区画選択)/フィールド(自由歩行)/会話/交渉パズル/
  結果/エンディング。

### アーキテクチャ方針(詳細は02_要件定義書.mdを参照)
- Controller → Service → Repository(JPA) の3層構成を徹底する。責務分離は意図的な
  設計判断であり、面談で説明できるようレイヤーを崩さないこと。
- **交渉パズルのコアロジック(ゲージ計算・相性判定)はクライアントJS側に残す**
  (既存MVPから移植)。サーバー側はNPC/記憶/章などのマスタデータ提供と、
  章・依頼完了などの区切りタイミングでの進行状況永続化(`POST /api/progress`)に専念する。
  ゲージ/相性計算をサーバー側に移さないこと。
- 認証はSpring Securityのセッションベース・フォームログイン(JWTは採用しない)。
  ロールは `ROLE_PLAYER` / `ROLE_ADMIN`。セーブスロットはユーザーごとに1つのみ。
- 管理者用CRUD(`/admin/**`、`ROLE_ADMIN`限定)でNPC/記憶断片/区画/章をThymeleafフォームから管理する。

### 主要エンティティ(実装予定・未実装)
`User` 1:1 `GameProgress` 1:N `CompletedEncounter`、`Chapter` 1:N `District` 1:1 `Npc`
1:N `NpcReactionLine`、`MemoryFragment` は独立の共通プール。NPCの `weakTag`/`hateTag` は
8種の感情タグのEnum。`CompletedEncounter` は各交渉のNPC・結果・ゲージ値を記録する
実績ログ(必須ではないが設計力アピールの一環)。

### URL設計(実装予定)
`GET /`(認証状態でリダイレクト)、`/login`、`/register`、`GET /game`(ゲーム本編)、
`POST|GET /api/progress`、`/admin`、`/admin/npcs|memories|districts|chapters`(CRUD、`ROLE_ADMIN`)。

### 開発フェーズ
0 プロジェクト初期化・DB接続確認 → 1 JPAエンティティ実装 → 2 Spring Security(会員登録・
ログイン・ログアウト) → 3 MVPゲームロジックの`/game`への移植 → 4 進行状況の保存・読込API
→ 5 管理者CRUD → 6 全3章コンテンツのシード投入 → 7 AWS EC2への手動デプロイ(jar +
必要に応じてsystemd化) → 8 通し確認・面談用の設計意図ドキュメント化。

未決事項: 第3章セレーネとの最終交渉を通常の相性表ロジックで扱うか専用画面にするか、
`CompletedEncounter` 履歴のUI表示要否、Flyway/Liquibase導入の要否。

## プロジェクト構成

Mavenプロジェクトはリポジトリ直下ではなく `spring-practice/demo/` にある。Mavenコマンドは
すべてこのディレクトリから実行すること。

- メインパッケージ: `spring-practice/demo/src/main/java/com/example/demo/` 配下の `com.example.demo`
- エントリポイント: `spring-practice/demo/src/main/java/com/example/demo/DemoApplication.java`
- 設定: `spring-practice/demo/src/main/resources/application.properties`
- テンプレート: `spring-practice/demo/src/main/resources/templates/`(Thymeleaf)
- 静的アセット: `spring-practice/demo/src/main/resources/static/`
- テスト: `spring-practice/demo/src/test/java/com/example/demo/`

start.spring.ioで生成したスケルトンから、Hello World画面(`HomeController`/`index.html`)と
暫定のSecurityConfigを追加した段階(`docs/01_...md`参照)。エンティティ・本格的な会員機能
・ゲーム画面はこれから実装する(上記フェーズ計画の通り)。

## 技術スタック

- Java 21、Spring Boot 3.5.0(Maven、`spring-boot-starter-parent`)
- 導入済みの依存関係: Spring Web、Spring Data JPA、Thymeleaf、Validation、Actuator、
  MySQL Connector(runtime)、Lombok、Spring Security、thymeleaf-extras-springsecurity6、
  spring-boot-devtools(開発時のみ)
- ビルドツール: Maven Wrapper(`mvnw`/`mvnw.cmd`) — システムインストール済みのMavenではなく
  Wrapperを使用すること

## コマンド

`spring-practice/demo/` から実行する。

```cmd
./mvnw.cmd spring-boot:run          # ローカルで起動
./mvnw.cmd test                     # 全テスト実行
./mvnw.cmd test -Dtest=ClassName    # 単一テストクラスを実行
./mvnw.cmd test -Dtest=ClassName#methodName   # 単一テストメソッドを実行
./mvnw.cmd clean package            # jarをビルド(target/)
```

Windows以外のシェルでは `./mvnw.cmd` の代わりに `./mvnw` を使用する。

## メモ

- MySQLのデータソース設定は `application.properties` に `spring.datasource.*` として定義済み。
  パスワードは `${DB_PASSWORD:}` で環境変数から読み込む方式とし、ファイルには直書きしない。
- ローカルではMySQL80サービスを使用し、DB名は `wakyaku_dev`(`createDatabaseIfNotExist=true` で自動作成)。
- Lombokはオプション依存として `spring-boot-maven-plugin` の設定で実行用jarから除外されている。
  アノテーション処理は `maven-compiler-plugin` の実行設定で明示的に組み込み済み。
- 開発時のDBは `spring.jpa.hibernate.ddl-auto=update` で簡易運用中。Flyway/Liquibase等の
  マイグレーションツール導入は未決定(`docs/09_...md`参照)。

## テスト

- JUnit 5を使用する
- 明示的に必要とされない限りMockitoは使用しない
- テストは `src/test/java/com/example/demo` 配下に置く
