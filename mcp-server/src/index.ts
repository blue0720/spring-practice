#!/usr/bin/env node
import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { z } from "zod";
import pool, { initSchema } from "./db.js";

const EMOTION_TAGS = [
  "喜び", "悲しみ", "安心", "恐怖", "誇り", "恥", "愛情", "怒り",
] as const;

const server = new McpServer({
  name: "wakyaku-content",
  version: "0.1.0",
});

function textResult(data: unknown) {
  return { content: [{ type: "text" as const, text: JSON.stringify(data, null, 2) }] };
}

// ---- Chapter -------------------------------------------------------------

server.tool(
  "list_chapters",
  "章(Chapter)の一覧を取得する",
  {},
  async () => {
    const [rows] = await pool.query("SELECT * FROM chapter ORDER BY number");
    return textResult(rows);
  },
);

server.tool(
  "upsert_chapter",
  "章(Chapter)を新規作成、または id 指定時は更新する",
  {
    id: z.number().int().optional().describe("省略時は新規作成"),
    number: z.number().int().describe("章番号(1〜3)"),
    title: z.string().describe("章タイトル"),
    description: z.string().optional(),
  },
  async ({ id, number, title, description }) => {
    if (id) {
      await pool.query(
        "UPDATE chapter SET number = ?, title = ?, description = ? WHERE id = ?",
        [number, title, description ?? null, id],
      );
      return textResult({ id, updated: true });
    }
    const [result] = await pool.query(
      "INSERT INTO chapter (number, title, description) VALUES (?, ?, ?)",
      [number, title, description ?? null],
    );
    return textResult({ id: (result as any).insertId, created: true });
  },
);

server.tool(
  "delete_chapter",
  "章(Chapter)を削除する",
  { id: z.number().int() },
  async ({ id }) => {
    await pool.query("DELETE FROM chapter WHERE id = ?", [id]);
    return textResult({ id, deleted: true });
  },
);

// ---- Npc -------------------------------------------------------------

server.tool(
  "list_npcs",
  "NPCの一覧を取得する(章IDで絞り込み可)",
  { chapterId: z.number().int().optional() },
  async ({ chapterId }) => {
    const [rows] = chapterId
      ? await pool.query("SELECT * FROM npc WHERE chapter_id = ? ORDER BY id", [chapterId])
      : await pool.query("SELECT * FROM npc ORDER BY id");
    return textResult(rows);
  },
);

server.tool(
  "upsert_npc",
  "NPCを新規作成、または id 指定時は更新する。weakTag/hateTagは感情タグ8種から指定する",
  {
    id: z.number().int().optional(),
    name: z.string(),
    role: z.string().optional().describe("NPCタイプ(例: 孤独な貧困層, ギルド幹部・権力者)"),
    portraitLabel: z.string().optional(),
    weakTag: z.enum(EMOTION_TAGS).optional().describe("ゲージが上昇する感情タグ"),
    hateTag: z.enum(EMOTION_TAGS).optional().describe("ゲージが下降する感情タグ"),
    introText: z.string().optional(),
    chapterId: z.number().int().optional(),
  },
  async ({ id, name, role, portraitLabel, weakTag, hateTag, introText, chapterId }) => {
    if (id) {
      await pool.query(
        `UPDATE npc SET name = ?, role = ?, portrait_label = ?, weak_tag = ?, hate_tag = ?,
         intro_text = ?, chapter_id = ? WHERE id = ?`,
        [name, role ?? null, portraitLabel ?? null, weakTag ?? null, hateTag ?? null,
          introText ?? null, chapterId ?? null, id],
      );
      return textResult({ id, updated: true });
    }
    const [result] = await pool.query(
      `INSERT INTO npc (name, role, portrait_label, weak_tag, hate_tag, intro_text, chapter_id)
       VALUES (?, ?, ?, ?, ?, ?, ?)`,
      [name, role ?? null, portraitLabel ?? null, weakTag ?? null, hateTag ?? null,
        introText ?? null, chapterId ?? null],
    );
    return textResult({ id: (result as any).insertId, created: true });
  },
);

server.tool(
  "delete_npc",
  "NPCを削除する",
  { id: z.number().int() },
  async ({ id }) => {
    await pool.query("DELETE FROM npc WHERE id = ?", [id]);
    return textResult({ id, deleted: true });
  },
);

// ---- NpcReactionLine -------------------------------------------------------------

server.tool(
  "list_npc_reaction_lines",
  "指定NPCの反応テキスト(良い/悪い/普通)一覧を取得する",
  { npcId: z.number().int() },
  async ({ npcId }) => {
    const [rows] = await pool.query(
      "SELECT * FROM npc_reaction_line WHERE npc_id = ? ORDER BY id",
      [npcId],
    );
    return textResult(rows);
  },
);

server.tool(
  "add_npc_reaction_line",
  "NPCの反応テキストを追加する",
  {
    npcId: z.number().int(),
    type: z.enum(["GOOD", "BAD", "NEUTRAL"]),
    text: z.string(),
  },
  async ({ npcId, type, text }) => {
    const [result] = await pool.query(
      "INSERT INTO npc_reaction_line (npc_id, type, text) VALUES (?, ?, ?)",
      [npcId, type, text],
    );
    return textResult({ id: (result as any).insertId, created: true });
  },
);

server.tool(
  "delete_npc_reaction_line",
  "NPCの反応テキストを削除する",
  { id: z.number().int() },
  async ({ id }) => {
    await pool.query("DELETE FROM npc_reaction_line WHERE id = ?", [id]);
    return textResult({ id, deleted: true });
  },
);

// ---- District -------------------------------------------------------------

server.tool(
  "list_districts",
  "区画(District)の一覧を取得する",
  {},
  async () => {
    const [rows] = await pool.query("SELECT * FROM district ORDER BY id");
    return textResult(rows);
  },
);

server.tool(
  "upsert_district",
  "区画(District)を新規作成、または id 指定時は更新する",
  {
    id: z.number().int().optional(),
    name: z.string(),
    role: z.string().optional().describe("区画の役割・雰囲気"),
    chapterId: z.number().int().optional(),
    npcId: z.number().int().optional().describe("主要NPCのid(1区画1NPC)"),
  },
  async ({ id, name, role, chapterId, npcId }) => {
    if (id) {
      await pool.query(
        "UPDATE district SET name = ?, role = ?, chapter_id = ?, npc_id = ? WHERE id = ?",
        [name, role ?? null, chapterId ?? null, npcId ?? null, id],
      );
      return textResult({ id, updated: true });
    }
    const [result] = await pool.query(
      "INSERT INTO district (name, role, chapter_id, npc_id) VALUES (?, ?, ?, ?)",
      [name, role ?? null, chapterId ?? null, npcId ?? null],
    );
    return textResult({ id: (result as any).insertId, created: true });
  },
);

server.tool(
  "delete_district",
  "区画(District)を削除する",
  { id: z.number().int() },
  async ({ id }) => {
    await pool.query("DELETE FROM district WHERE id = ?", [id]);
    return textResult({ id, deleted: true });
  },
);

// ---- MemoryFragment -------------------------------------------------------------

server.tool(
  "list_memory_fragments",
  "記憶断片(MemoryFragment)の一覧を取得する",
  {},
  async () => {
    const [rows] = await pool.query("SELECT * FROM memory_fragment ORDER BY id");
    return textResult(rows);
  },
);

server.tool(
  "upsert_memory_fragment",
  "記憶断片を新規作成、または id 指定時は更新する",
  {
    id: z.number().int().optional(),
    tag: z.enum(EMOTION_TAGS),
    title: z.string(),
  },
  async ({ id, tag, title }) => {
    if (id) {
      await pool.query("UPDATE memory_fragment SET tag = ?, title = ? WHERE id = ?", [tag, title, id]);
      return textResult({ id, updated: true });
    }
    const [result] = await pool.query(
      "INSERT INTO memory_fragment (tag, title) VALUES (?, ?)",
      [tag, title],
    );
    return textResult({ id: (result as any).insertId, created: true });
  },
);

server.tool(
  "delete_memory_fragment",
  "記憶断片を削除する",
  { id: z.number().int() },
  async ({ id }) => {
    await pool.query("DELETE FROM memory_fragment WHERE id = ?", [id]);
    return textResult({ id, deleted: true });
  },
);

// ---- Read-only: users / progress / encounters -----------------------------

server.tool(
  "list_users",
  "登録ユーザーの一覧を取得する(パスワードは含まない、読み取り専用)",
  {},
  async () => {
    const [rows] = await pool.query(
      "SELECT id, username, email, role, created_at FROM app_user ORDER BY id",
    );
    return textResult(rows);
  },
);

server.tool(
  "list_game_progress",
  "ユーザーの進行状況一覧を取得する(読み取り専用)",
  { userId: z.number().int().optional() },
  async ({ userId }) => {
    const [rows] = userId
      ? await pool.query("SELECT * FROM game_progress WHERE user_id = ?", [userId])
      : await pool.query("SELECT * FROM game_progress ORDER BY id");
    return textResult(rows);
  },
);

server.tool(
  "list_completed_encounters",
  "交渉履歴(CompletedEncounter)の一覧を取得する(読み取り専用)",
  { gameProgressId: z.number().int().optional() },
  async ({ gameProgressId }) => {
    const [rows] = gameProgressId
      ? await pool.query(
        "SELECT * FROM completed_encounter WHERE game_progress_id = ? ORDER BY played_at",
        [gameProgressId],
      )
      : await pool.query("SELECT * FROM completed_encounter ORDER BY played_at DESC LIMIT 100");
    return textResult(rows);
  },
);

async function main() {
  await initSchema();
  const transport = new StdioServerTransport();
  await server.connect(transport);
}

main().catch((err) => {
  console.error("Fatal error starting wakyaku-content MCP server:", err);
  process.exit(1);
});
