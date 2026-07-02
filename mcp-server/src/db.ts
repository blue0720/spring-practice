import mysql from "mysql2/promise";

function requireEnv(name: string, fallback?: string): string {
  const value = process.env[name] ?? fallback;
  if (value === undefined) {
    throw new Error(`Missing required environment variable: ${name}`);
  }
  return value;
}

const pool = mysql.createPool({
  host: process.env.DB_HOST ?? "localhost",
  port: Number(process.env.DB_PORT ?? 3306),
  user: process.env.DB_USER ?? "root",
  password: requireEnv("DB_PASSWORD"),
  database: process.env.DB_NAME ?? "wakyaku_dev",
  waitForConnections: true,
  connectionLimit: 5,
  charset: "utf8mb4_general_ci",
});

// Table names mirror the physical naming strategy Spring Boot/Hibernate will use once the
// JPA entities from docs/02_jpa-entities.md are implemented, so this schema can be adopted
// as-is (`spring.jpa.hibernate.ddl-auto=update` will just find the tables already present).
// `user` is reserved in MySQL 8, so the User entity is expected to map to `app_user`.
const SCHEMA_STATEMENTS = [
  `CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_PLAYER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,

  `CREATE TABLE IF NOT EXISTS chapter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number INT NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,

  `CREATE TABLE IF NOT EXISTS npc (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(255),
    portrait_label VARCHAR(255),
    weak_tag VARCHAR(20),
    hate_tag VARCHAR(20),
    intro_text TEXT,
    chapter_id BIGINT,
    CONSTRAINT fk_npc_chapter FOREIGN KEY (chapter_id) REFERENCES chapter(id) ON DELETE SET NULL
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,

  `CREATE TABLE IF NOT EXISTS district (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(255),
    chapter_id BIGINT,
    npc_id BIGINT UNIQUE,
    CONSTRAINT fk_district_chapter FOREIGN KEY (chapter_id) REFERENCES chapter(id) ON DELETE SET NULL,
    CONSTRAINT fk_district_npc FOREIGN KEY (npc_id) REFERENCES npc(id) ON DELETE SET NULL
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,

  `CREATE TABLE IF NOT EXISTS npc_reaction_line (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    npc_id BIGINT NOT NULL,
    type ENUM('GOOD', 'BAD', 'NEUTRAL') NOT NULL,
    text TEXT NOT NULL,
    CONSTRAINT fk_reaction_npc FOREIGN KEY (npc_id) REFERENCES npc(id) ON DELETE CASCADE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,

  `CREATE TABLE IF NOT EXISTS memory_fragment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,

  `CREATE TABLE IF NOT EXISTS game_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    current_chapter INT NOT NULL DEFAULT 1,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_progress_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,

  `CREATE TABLE IF NOT EXISTS completed_encounter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_progress_id BIGINT NOT NULL,
    npc_id BIGINT,
    outcome VARCHAR(20),
    gauge INT,
    played_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_encounter_progress FOREIGN KEY (game_progress_id) REFERENCES game_progress(id) ON DELETE CASCADE,
    CONSTRAINT fk_encounter_npc FOREIGN KEY (npc_id) REFERENCES npc(id) ON DELETE SET NULL
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`,
];

export async function initSchema(): Promise<void> {
  const conn = await pool.getConnection();
  try {
    for (const statement of SCHEMA_STATEMENTS) {
      await conn.query(statement);
    }
  } finally {
    conn.release();
  }
}

export default pool;
