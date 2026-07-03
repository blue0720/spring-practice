# 08. AWS EC2デプロイ

対応フェーズ: フェーズ7

## 概要
AWS EC2へ手動デプロイし、動作確認を行う。

## Todo
- [〇] EC2インスタンスの構成検討(インスタンスタイプ・セキュリティグループ)
- [ ] ドメイン取得の要否を検討(現状はElastic IPなしのパブリックIP直アクセスで運用)
- [〇] EC2上にMySQLを構築(または将来的なRDS移行を見据えた構成にする)
- [〇] `./mvnw.cmd clean package` でjarをビルド
- [〇] jarをEC2に配置し `java -jar` で起動確認
- [〇] 必要に応じてsystemd化(再起動時の自動起動)
- [〇] 本番相当の`DB_PASSWORD`等の環境変数・シークレット管理方針を決定
- [ ] 本番環境での動作確認(会員登録・ログイン・ゲームプレイ・管理画面) — ログイン画面の表示確認まで完了。会員登録・ゲームプレイ・管理画面の通し確認は未実施

## 参照
- `02_要件定義書.md` セクション6「非機能要件(デプロイ)」

## メモ
- インフラ詳細(セキュリティグループ等)は必要になった段階で個別に詰める方針(要件定義書の未決事項3)。

### デプロイ構成(2026-07-03実施)
- インスタンス: `wakyaku-app-server`(i-062070a7662808458)、`t3.micro`、Amazon Linux 2023、リージョン`ap-northeast-1`
- Elastic IP未使用。現在の公開IPは `3.112.13.122`(再起動すると変わる可能性あり)
- セキュリティグループ(`launch-wizard-1`): 22(SSH、自宅IPのみ)/ 80・443(0.0.0.0/0)を開放。8080は未開放のためアプリを直接公開せず、nginxのリバースプロキシ経由で80番から`127.0.0.1:8080`へ転送する構成にした
- MySQL Community Server 8.0.46をOracle公式リポジトリから導入(Amazon Linux 2023標準リポジトリにはMariaDBしかなく、ローカル開発環境のMySQL80と方言を揃えるため公式リポジトリを追加)
  - DB: `wakyaku_prod` / アプリ専用ユーザー: `wakyaku_app`(rootを直接使わずアプリ用に権限を絞ったユーザーを作成)
  - 本番用設定は `application-prod.properties` を新規作成し、`SPRING_PROFILES_ACTIVE=prod` で切り替え
- 秘密情報(`DB_PASSWORD`等)は `/etc/wakyaku-app.env`(root:root, 600権限)に分離し、systemdの`EnvironmentFile`から読み込む方式
- systemdユニット: `/etc/systemd/system/wakyaku-app.service`(jar本体は`/opt/wakyaku-app/app.jar`)
- **メモリ不足に注意**: `t3.micro`(メモリ1GB)でMySQL+Javaアプリ+nginxを同時稼働させるとほぼ余裕がなく、一度SSH自体が応答不能になるOOM状態を起こした。対策として1GBのスワップファイル(`/swapfile`、`/etc/fstab`に永続化登録済み)を追加したが、稼働時の空きメモリは常時60〜100MiB程度とタイト。今後アクセスや管理画面操作が増える場合は `t3.small`(メモリ2GB、無料利用枠対象外につき課金発生)への変更を検討する
