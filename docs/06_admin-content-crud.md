# 06. 管理者機能(コンテンツCRUD)

対応フェーズ: フェーズ5

## 概要
`ROLE_ADMIN` 専用の管理画面を実装し、ゲームコンテンツ(NPC・記憶断片・区画・章)をCRUD管理できるようにする。

## Todo
- [ ] `GET /admin` 管理画面トップ(`ROLE_ADMIN`のみ)
- [ ] NPC管理: `GET/POST /admin/npcs`, `/admin/npcs/{id}` の一覧・新規作成・編集・削除
- [ ] 記憶断片管理: `/admin/memories` のCRUD
- [ ] 区画(District)管理: `/admin/districts` のCRUD
- [ ] 章(Chapter)管理: `/admin/chapters` のCRUD
- [ ] NPCの「弱点タグ」「苦手タグ」「反応テキスト(良い/悪い/普通、複数)」を編集できるフォーム設計
- [ ] 各CRUDフォームに `spring-boot-starter-validation` でサーバーサイドバリデーション
- [ ] `/admin/**` へのアクセス制御(`ROLE_ADMIN`)がチケット03のSecurityConfigと連動していることを確認

## 参照
- `02_要件定義書.md` セクション3.3「コンテンツ管理機能」、セクション5「画面・URL設計」
