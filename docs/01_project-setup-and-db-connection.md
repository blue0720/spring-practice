# 01. プロジェクト初期化・DB接続確認

対応フェーズ: フェーズ0

## 概要
Spring Initializrで生成したプロジェクトに、DB接続とHello World的な画面表示までを整える。

## Todo
- [〇] `pom.xml` に `spring-boot-starter-security` / `thymeleaf-extras-springsecurity6` / `spring-boot-devtools` / `spring-security-test` を追加
- [〇] `application.properties` にMySQLデータソース設定を追加(パスワードは `${DB_PASSWORD:}` で環境変数参照、直書きしない)
- [〇] `wakyaku_dev` データベースへの接続確認(`createDatabaseIfNotExist=true`)
- [〇] `HomeController`(`GET /`) と `templates/index.html` でHello World画面を作成
- [〇] 暫定 `SecurityConfig`(全許可・TODOコメント付き)を追加 — フェーズ2で本実装に置き換え
- [〇] `./mvnw.cmd test` で `DemoApplicationTests#contextLoads` が成功することを確認
- [〇] `./mvnw.cmd spring-boot:run` を実行し、ブラウザで `http://localhost:8080/` が表示されることを目視確認(ユーザー作業)

## メモ
- MySQL80サービスはローカルにインストール済み・稼働確認済み。
- `DB_PASSWORD` はローカル環境変数として都度設定する運用とし、ファイルには保存しない方針で確定。
- Windows環境でMaven Wrapper実行時、`powershell.exe` がPATHに無いと `mvnw.cmd` が失敗する場合がある
  (`PATH="/c/Windows/System32/WindowsPowerShell/v1.0:$PATH"` を付与すれば回避可)。
