// sprites.js — 『忘却の取引』の2Dドット絵キャラクター
// 16x16のピクセルマップ + キャラ別パレットからSVGを生成する。外部画像を持たず、
// 全キャラをコードで「作成」している(ポートフォリオでの自作アピールも兼ねる)。
// 公開API: window.SPRITES.render(key) → SVG文字列
(function (global) {
  'use strict';

  // 凡例: . 透明 / K 輪郭 / C マント / D マント影 / H 髪・フード / B 帽子つば
  //        S 肌 / s 肌影 / E 目 / A 金・宝飾 / W 白(襟/ベール) / M 覆面
  const MAPS = {
    // 主人公ヴィオラ: フードをまとう記憶商人
    'viola': [
      "......KKKK......",
      ".....KHHHHK.....",
      "....KHHHHHHK....",
      "...KHHSSSSHHK...",
      "...KHSSSSSSHK...",
      "...KHSESSESHK...",
      "...KHSSSSSSHK...",
      "....KSSAASSK....",
      "...KCCCAACCCK...",
      "..KCCCCAACCCCK..",
      "..KCDCCCCCCDCK..",
      "..KCDCCCCCCDCK..",
      "..KCDCCCCCCDCK..",
      "..KCCCCCCCCCCK..",
      "..KCCCK..KCCCK..",
      "..KKKK...KKKK..."
    ],
    // 孤独な貧困層: ぼろのとんがりフード
    '孤独な貧困層': [
      ".......KK.......",
      "......KHHK......",
      ".....KHHHHK.....",
      "....KHHHHHHK....",
      "...KHHSSSSHHK...",
      "...KHSEsSEsSHK..",
      "...KHSSSSSSSHK..",
      "....KSSssssSK...",
      "...KDDCCCCDDK...",
      "..KDCCCCCCCDK...",
      "..KDCCCDDCCCDK..",
      "..KDCCDDDDCCDK..",
      "..KDCCCDDCCCDK..",
      "..KDDCCCCCCDDK..",
      "..KDDK....KDDK..",
      "..KKK......KKK.."
    ],
    // ギルド幹部・権力者: つば広の帽子・威厳
    'ギルド幹部・権力者': [
      "..KKKKKKKKKKKK..",
      "..KBBBBBBBBBBK..",
      "....KBBBBBBK....",
      "....KBAAAABK....",
      "....KSSSSSSK....",
      "....KSEssEsK....",
      "....KSSssSSK....",
      "...KWCCCCCCWK...",
      "..KCCAADDAACCK..",
      "..KCCADCCDACCK..",
      "..KCCADCCDACCK..",
      "..KCCAADDAACCK..",
      "..KCCCCCCCCCCK..",
      "..KCCCCAACCCCK..",
      "..KCCCK..KCCCK..",
      "..KKKK...KKKK..."
    ],
    // 裏市場の人間: フード + 覆面のブローカー
    '裏市場の人間': [
      ".....KKKKKK.....",
      "....KHHHHHHK....",
      "...KHHHHHHHHK...",
      "..KHHSSSSSSHHK..",
      "..KHSSEssEsSHK..",
      "..KHSMMMMMMSHK..",
      "..KHSMMMMMMSHK..",
      "...KDMMMMMMDK...",
      "..KDDCCCCCCDDK..",
      ".KDCCCAACCCCDK..",
      ".KDCCCAACCCCDK..",
      ".KDCCCCCCCCCDK..",
      ".KDDCCCCCCCDDK..",
      "..KDCCCCCCCCDK..",
      "..KDCCK..KCCDK..",
      "..KKKK...KKKK..."
    ],
    // 喪失を抱えた市民: 伏し目・ベールをまとう
    '喪失を抱えた市民': [
      ".....KKKKKK.....",
      "....KWWWWWWK....",
      "...KWWWWWWWWK...",
      "..KWWSSSSSSWWK..",
      "..KWSSssssSSWK..",
      "..KWSKKssKKSWK..",
      "..KWSSssssSSWK..",
      "...KWSSssSSWK...",
      "...KWWCCCCWWK...",
      "..KWCCCCCCCCWK..",
      "..KWCDCCCCDCWK..",
      "..KWCDCCCCDCWK..",
      "..KWCCCCCCCCWK..",
      "..KWWCCCCCCWWK..",
      "..KKWCCK.KCCWK..",
      "...KKKK...KKK..."
    ]
  };

  // 全キャラ共通色(肌・目・輪郭・金)。キャラ別で上書きする。
  const BASE = { K: '#0b0b14', S: '#eccfa6', s: '#c9a678', E: '#1b1420', A: '#e8c24a', W: '#dfe3f2' };

  const PALETTES = {
    'viola':            { H: '#3a2d4a', C: '#2b6e6a', D: '#1d4c49', A: '#e8c24a' },
    '孤独な貧困層':      { H: '#6b5540', C: '#7a6a52', D: '#574937' },
    'ギルド幹部・権力者': { B: '#1a2b52', C: '#26407a', D: '#182a55', W: '#dfe3f2' },
    '裏市場の人間':      { H: '#242a20', C: '#2e3a2a', D: '#1c241a', M: '#12140f', A: '#8a5cf6' },
    '喪失を抱えた市民':   { W: '#c9c7d8', C: '#8a8698', D: '#6d6a7c', E: '#2a2430' }
  };

  function render(key) {
    const map = MAPS[key] || MAPS['viola'];
    const pal = Object.assign({}, BASE, PALETTES[key] || {});
    let rects = '';
    for (let y = 0; y < map.length; y++) {
      const row = map[y];
      for (let x = 0; x < row.length; x++) {
        const ch = row[x];
        if (ch === '.' || ch === ' ') continue;
        const color = pal[ch];
        if (!color) continue;
        // 1.02でわずかに重ね、タイル間の隙間を消す
        rects += '<rect x="' + x + '" y="' + y + '" width="1.02" height="1.02" fill="' + color + '"/>';
      }
    }
    return '<svg class="pixel-sprite" viewBox="0 0 16 16" preserveAspectRatio="xMidYMid meet" ' +
      'shape-rendering="crispEdges" xmlns="http://www.w3.org/2000/svg">' + rects + '</svg>';
  }

  global.SPRITES = { render: render, has: function (key) { return !!MAPS[key]; } };
})(window);
