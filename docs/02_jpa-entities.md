# 02. JPAエンティティ設計・実装

対応フェーズ: フェーズ1

## 概要
要件定義書のデータベース設計案に基づき、エンティティを実装し、MySQLへのテーブル作成を確認する。

## Todo
- [〇] `User` エンティティ(id, username, email, password(hash), role, createdAt)
- [〇] `GameProgress` エンティティ(id, userId(FK), currentChapter, updatedAt) — `User` と1:1
- [〇] `Chapter` エンティティ(id, number, title, description)
- [〇] `District` エンティティ(id, name, role, chapterId(FK), npcId(FK)) — `Chapter` とN:1、`Npc` と1:1
- [〇] `Npc` エンティティ(id, name, role, portraitLabel, weakTag, hateTag, introText, chapterId(FK))
- [〇] `NpcReactionLine` エンティティ(id, npcId(FK), type(GOOD/BAD/NEUTRAL), text)
- [〇] `MemoryFragment` エンティティ(id, tag, title) — 全章共通プールの単独マスタ
- [〇] `CompletedEncounter` エンティティ(id, gameProgressId(FK), npcId(FK), outcome, gauge, playedAt)
- [〇] 感情タグ(8種: 喜び/悲しみ, 安心/恐怖, 誇り/恥, 愛情/怒り)をEnumとして実装し、`weakTag`/`hateTag` に使用
- [〇] Repositoryインターフェース(Spring Data JPA)を各エンティティ分作成
- [〇] `spring.jpa.hibernate.ddl-auto=update` でMySQLにテーブルが作成されることを確認

## 参照
- `02_要件定義書.md` セクション4「データベース設計(エンティティ案)」
- `01_企画書.md` セクション「相性表(感情タグ×NPCタイプ)」

## メモ
- `CompletedEncounter` は必須ではないが、実績ログとして将来的な拡張・面談アピール材料になる想定。
