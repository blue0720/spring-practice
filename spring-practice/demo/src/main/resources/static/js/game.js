// 忘却の取引 - 画面遷移・交渉パズル・フィールド探索の制御
// 交渉パズルのコアロジック(ゲージ計算・相性判定)はここに置き、サーバー側には持たせない。
(function () {
  'use strict';

  const state = {
    currentDistrict: null,
    currentChapter: 1,
    clearedDistrictIds: new Set(),
    fieldPos: { x: 0, y: 0 },
    fieldKeydownHandler: null,
    gauge: GAUGE_START,
    usedTags: new Set(),
    negotiationLog: []
  };

  function $(id) {
    return document.getElementById(id);
  }

  // ---------- 進行状況の保存・読込 ----------
  function csrfHeaders() {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;
    const headers = { 'Content-Type': 'application/json' };
    headers[header] = token;
    return headers;
  }

  function loadProgress() {
    return fetch('/api/progress', { headers: { Accept: 'application/json' } })
      .then(function (res) {
        if (!res.ok) throw new Error('進行状況の取得に失敗しました');
        return res.json();
      })
      .then(function (data) {
        state.currentChapter = data.currentChapter;
        state.clearedDistrictIds = new Set(data.clearedDistrictIds);
      })
      .catch(function (err) {
        console.error(err);
      });
  }

  function saveProgress(lastEncounter) {
    const body = {
      currentChapter: state.currentChapter,
      clearedDistrictIds: Array.from(state.clearedDistrictIds),
      lastEncounter: lastEncounter || null
    };
    return fetch('/api/progress', {
      method: 'POST',
      headers: csrfHeaders(),
      body: JSON.stringify(body)
    }).catch(function (err) {
      console.error(err);
    });
  }

  function showScreen(id) {
    document.querySelectorAll('.screen').forEach(function (el) {
      el.classList.toggle('is-active', el.id === id);
    });
  }

  // ---------- タイトル画面 ----------
  function initTitle() {
    $('btn-start').addEventListener('click', function () {
      showScreen('screen-map');
      renderMap();
    });
  }

  // ---------- マップ画面(区画選択) ----------
  function renderMap() {
    const list = $('map-district-list');
    list.innerHTML = '';
    DISTRICTS.forEach(function (district) {
      const card = document.createElement('button');
      card.type = 'button';
      card.className = 'district-card';
      const cleared = state.clearedDistrictIds.has(district.id);
      card.innerHTML =
        '<h3>' + district.name + (cleared ? ' <span class="cleared-mark">交渉済</span>' : '') + '</h3>' +
        '<p>' + district.description + '</p>';
      card.addEventListener('click', function () {
        enterField(district);
      });
      list.appendChild(card);
    });

    const endingBtn = $('btn-goto-ending');
    endingBtn.hidden = state.clearedDistrictIds.size < DISTRICTS.length;
  }

  // ---------- フィールド画面(自由歩行) ----------
  function enterField(district) {
    state.currentDistrict = district;
    state.fieldPos = { x: district.field.playerStart.x, y: district.field.playerStart.y };
    showScreen('screen-field');
    $('field-district-name').textContent = district.name;
    renderField();

    if (state.fieldKeydownHandler) {
      document.removeEventListener('keydown', state.fieldKeydownHandler);
    }
    state.fieldKeydownHandler = function (e) {
      const deltas = {
        ArrowUp: { x: 0, y: -1 },
        ArrowDown: { x: 0, y: 1 },
        ArrowLeft: { x: -1, y: 0 },
        ArrowRight: { x: 1, y: 0 }
      };
      const delta = deltas[e.key];
      if (!delta) return;
      e.preventDefault();
      movePlayer(delta.x, delta.y);
    };
    document.addEventListener('keydown', state.fieldKeydownHandler);
  }

  function renderField() {
    const district = state.currentDistrict;
    const grid = $('field-grid');
    grid.style.gridTemplateColumns = 'repeat(' + district.field.width + ', 1fr)';
    grid.innerHTML = '';

    for (let y = 0; y < district.field.height; y++) {
      for (let x = 0; x < district.field.width; x++) {
        const cell = document.createElement('div');
        cell.className = 'field-cell';
        if (x === district.field.npcPos.x && y === district.field.npcPos.y) {
          cell.classList.add('field-cell-npc');
          cell.innerHTML = SPRITES.render(district.npc.type);
        }
        if (x === state.fieldPos.x && y === state.fieldPos.y) {
          cell.classList.add('field-cell-player');
          cell.innerHTML = SPRITES.render('viola');
        }
        grid.appendChild(cell);
      }
    }
  }

  function movePlayer(dx, dy) {
    const district = state.currentDistrict;
    const nextX = Math.max(0, Math.min(district.field.width - 1, state.fieldPos.x + dx));
    const nextY = Math.max(0, Math.min(district.field.height - 1, state.fieldPos.y + dy));
    state.fieldPos = { x: nextX, y: nextY };
    renderField();

    if (nextX === district.field.npcPos.x && nextY === district.field.npcPos.y) {
      openDialogue(district);
    }
  }

  // ---------- 会話画面 ----------
  function openDialogue(district) {
    if (state.fieldKeydownHandler) {
      document.removeEventListener('keydown', state.fieldKeydownHandler);
    }
    showScreen('screen-dialogue');
    $('dialogue-npc-name').textContent = district.npc.name + '(' + district.npc.role + ')';
    $('dialogue-text').textContent = district.npc.intro;
  }

  function initDialogueButtons() {
    $('btn-negotiate').addEventListener('click', function () {
      startNegotiation(state.currentDistrict);
    });
    $('btn-dialogue-back').addEventListener('click', function () {
      showScreen('screen-map');
      renderMap();
    });
  }

  // ---------- 交渉パズル画面 ----------
  function affinityCategory(npcType, tag) {
    const affinity = AFFINITY_TABLE[npcType];
    if (affinity.weak === tag) return 'good';
    if (affinity.hate === tag) return 'bad';
    return 'neutral';
  }

  function gaugeDelta(category) {
    if (category === 'good') return GAUGE_WEAK_DELTA;
    if (category === 'bad') return GAUGE_HATE_DELTA;
    return GAUGE_NEUTRAL_DELTA;
  }

  function startNegotiation(district) {
    state.gauge = GAUGE_START;
    state.usedTags = new Set();
    state.negotiationLog = [district.npc.name + '「' + district.npc.negotiationIntro + '」'];

    showScreen('screen-negotiation');
    $('negotiation-npc-name').textContent = district.npc.name;
    $('enemy-sprite').innerHTML = SPRITES.render(district.npc.type);
    $('enemy-sprite').className = 'enemy-sprite';
    renderGauge();
    renderFragmentHand();
    renderNegotiationLog();
  }

  function renderGauge() {
    const fill = $('gauge-fill');
    fill.style.width = state.gauge + '%';
    $('gauge-value').textContent = state.gauge;
  }

  function renderFragmentHand() {
    const hand = $('fragment-hand');
    hand.innerHTML = '';
    MEMORY_FRAGMENTS.forEach(function (fragment) {
      const card = document.createElement('button');
      card.type = 'button';
      card.className = 'fragment-card fragment-tag-' + fragment.tag;
      card.innerHTML = '<span class="fragment-seal">' + fragment.tag + '</span><span class="fragment-title">' + fragment.title + '</span>';
      const used = state.usedTags.has(fragment.tag);
      card.disabled = used;
      if (used) card.classList.add('is-used');
      card.addEventListener('click', function () {
        playFragment(fragment);
      });
      hand.appendChild(card);
    });
  }

  function renderNegotiationLog() {
    const log = $('negotiation-log');
    log.innerHTML = state.negotiationLog.map(function (line) {
      return '<p>' + line + '</p>';
    }).join('');
    log.scrollTop = log.scrollHeight;
  }

  function playFragment(fragment) {
    if (state.usedTags.has(fragment.tag)) return;
    const district = state.currentDistrict;
    const category = affinityCategory(district.npc.type, fragment.tag);
    const delta = gaugeDelta(category);

    state.usedTags.add(fragment.tag);
    state.gauge = Math.max(GAUGE_MIN, Math.min(GAUGE_MAX, state.gauge + delta));

    const lines = district.npc.reactions[category];
    const reaction = lines[Math.floor(Math.random() * lines.length)];
    state.negotiationLog.push('『' + fragment.title + '』を差し出した。');
    state.negotiationLog.push(district.npc.name + '「' + reaction + '」');

    flashEnemy(category);
    renderGauge();
    renderFragmentHand();
    renderNegotiationLog();

    if (state.gauge >= GAUGE_MAX || state.gauge <= GAUGE_MIN || state.usedTags.size >= MEMORY_FRAGMENTS.length) {
      endNegotiation();
    }
  }

  // 記憶を差し出したときの敵リアクション(DQ風の点滅・のけぞり)
  function flashEnemy(category) {
    const sprite = $('enemy-sprite');
    if (!sprite) return;
    const cls = 'is-hit-' + category; // good / bad / neutral
    sprite.classList.remove('is-hit-good', 'is-hit-bad', 'is-hit-neutral');
    // リフローを挟んでアニメーションを確実に再生させる
    void sprite.offsetWidth;
    sprite.classList.add(cls);
    setTimeout(function () { sprite.classList.remove(cls); }, 480);
  }

  function endNegotiation() {
    const gauge = state.gauge;
    let outcome, outcomeLabel;
    if (gauge >= RESULT_THRESHOLD_SUCCESS) {
      outcome = 'success';
      outcomeLabel = '成功';
    } else if (gauge >= RESULT_THRESHOLD_PARTIAL) {
      outcome = 'partial';
      outcomeLabel = '部分的成功';
    } else {
      outcome = 'failure';
      outcomeLabel = '失敗';
    }

    if (outcome !== 'failure') {
      state.clearedDistrictIds.add(state.currentDistrict.id);
    }

    showScreen('screen-result');
    $('result-outcome').textContent = outcomeLabel;
    $('result-outcome').className = 'result-outcome result-outcome-' + outcome;
    $('result-gauge').textContent = gauge;

    saveProgress({
      districtId: state.currentDistrict.id,
      npcName: state.currentDistrict.npc.name,
      outcome: outcome.toUpperCase(),
      gauge: gauge
    });
  }

  function initNegotiationButtons() {
    $('btn-negotiation-end').addEventListener('click', endNegotiation);
  }

  function initResultButtons() {
    $('btn-result-back').addEventListener('click', function () {
      showScreen('screen-map');
      renderMap();
    });
  }

  // ---------- エンディング画面 ----------
  function initMapButtons() {
    $('btn-goto-ending').addEventListener('click', function () {
      showScreen('screen-ending');
    });
  }

  function initEndingButtons() {
    $('btn-ending-restart').addEventListener('click', function () {
      state.clearedDistrictIds.clear();
      showScreen('screen-title');
    });
  }

  function initLogoutConfirm() {
    $('logout-form').addEventListener('submit', function (e) {
      if (!window.confirm('ログアウトしますか?')) {
        e.preventDefault();
      }
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    initTitle();
    initDialogueButtons();
    initNegotiationButtons();
    initResultButtons();
    initMapButtons();
    initEndingButtons();
    initLogoutConfirm();
    const titleHero = $('title-hero');
    if (titleHero) titleHero.innerHTML = SPRITES.render('viola');
    showScreen('screen-title');
    loadProgress();
  });
})();
