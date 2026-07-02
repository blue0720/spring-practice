// 忘却の取引 - クライアント側ゲームデータ
// 章・NPC・記憶の実コンテンツはチケット07でDBへ投入し、将来的にはAPI経由で取得する想定。
// ここでは画面遷移・交渉パズルの「仕組み」を確認するための第1章相当のサンプルデータを保持する。

// 相性表(感情タグ × NPCタイプ) - 全依頼共通の1枚のルール表
const AFFINITY_TABLE = {
  '孤独な貧困層': { weak: '安心', hate: '恐怖' },
  'ギルド幹部・権力者': { weak: '誇り', hate: '恥' },
  '裏市場の人間': { weak: '愛情', hate: '怒り' },
  '喪失を抱えた市民': { weak: '喜び', hate: '悲しみ' }
};

// 記憶の断片(全章共通プール) - 8種の感情タグ
const MEMORY_FRAGMENTS = [
  { tag: '喜び', title: '祭りの夜、はじけた笑い声' },
  { tag: '悲しみ', title: '誰もいない部屋で見た夕焼け' },
  { tag: '安心', title: '毛布にくるまれた、静かな夜' },
  { tag: '恐怖', title: '暗闇で聞いた、知らない足音' },
  { tag: '誇り', title: 'やっと自分の名で呼ばれた日' },
  { tag: '恥', title: '皆の前でついた、小さな嘘' },
  { tag: '愛情', title: '手を握ってくれた、誰かの温もり' },
  { tag: '怒り', title: '奪われたものへの、消えない怒り' }
];

// 灰塵市の3区画
const DISTRICTS = [
  {
    id: 'slum',
    name: '貧困街',
    description: '灰の裏通り。表通りから見捨てられた者たちの生きる場所。',
    field: { width: 7, height: 5, playerStart: { x: 1, y: 2 }, npcPos: { x: 5, y: 2 } },
    npc: {
      id: 'nora',
      name: 'ノラ',
      role: '靴職人',
      type: '孤独な貧困層',
      intro: '……あんたが噂の記憶商人? 別に、誰でもよかったんだけどね。',
      negotiationIntro: '妹を飢えさせないために犯した過ち……この記憶を、買い取ってほしい。ただし、信用できる相手にしか渡さない。',
      reactions: {
        good: ['……悪くない。少しは、信じてもいいかもね。', 'そう、それは……ちょっとだけ、救われる話ね。'],
        bad: ['……何それ。あんたも所詮、他人事なんだ。', 'その話は……やめて。今は聞きたくない。'],
        neutral: ['……ふうん。', 'それで?']
      }
    }
  },
  {
    id: 'guild',
    name: 'ギルド本部',
    description: '商人ギルド管理区。規則と記録がすべてを支配する。',
    field: { width: 7, height: 5, playerStart: { x: 1, y: 2 }, npcPos: { x: 5, y: 2 } },
    npc: {
      id: 'vice',
      name: 'ヴァイス',
      role: '記録官',
      type: 'ギルド幹部・権力者',
      intro: '規則に従っていただければ、取引は円滑に進みます。',
      negotiationIntro: '個人の記憶売買記録は規則により非公開です。……ですが、相応の信頼を示していただければ、話は別かもしれません。',
      reactions: {
        good: ['……なるほど。あなたには一定の敬意を払いましょう。', 'その心構え、記録に値します。'],
        bad: ['……見苦しい。私の立場を、辱めるおつもりか。', 'それ以上は、規則違反として扱いますよ。'],
        neutral: ['……そうですか。', '続けてください。']
      }
    }
  },
  {
    id: 'backalley',
    name: '裏路地',
    description: '非合法の市場。値札のない記憶が、闇に流れる場所。',
    field: { width: 7, height: 5, playerStart: { x: 1, y: 2 }, npcPos: { x: 5, y: 2 } },
    npc: {
      id: 'reim',
      name: 'レイム',
      role: '記憶ブローカー',
      type: '裏市場の人間',
      intro: '……どこかで見た顔だったんだけどな。まあいいさ、商談といこうか。',
      negotiationIntro: 'こっちの世界に、綺麗事は通じない。だが……本物の情ってやつは、たまに見せてくれると嬉しいね。',
      reactions: {
        good: ['……ほぉ、悪くねえ。少しは信用してやるよ。', 'そういうの、嫌いじゃないぜ。'],
        bad: ['……ちっ、ふざけてんのか。', 'その怒り、こっちに向けるなよ。'],
        neutral: ['……で?', 'それで終わりか?']
      }
    }
  }
];

// 信頼度ゲージの調整パラメータ
const GAUGE_START = 50;
const GAUGE_MAX = 100;
const GAUGE_MIN = 0;
const GAUGE_WEAK_DELTA = 20;
const GAUGE_HATE_DELTA = -20;
const GAUGE_NEUTRAL_DELTA = 5;
const RESULT_THRESHOLD_SUCCESS = 80;
const RESULT_THRESHOLD_PARTIAL = 50;
