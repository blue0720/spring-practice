package com.example.demo.config;

import com.example.demo.dto.ChapterForm;
import com.example.demo.dto.DistrictForm;
import com.example.demo.dto.MemoryFragmentForm;
import com.example.demo.dto.NpcForm;
import com.example.demo.dto.NpcReactionLineForm;
import com.example.demo.entity.Chapter;
import com.example.demo.entity.EmotionTag;
import com.example.demo.entity.Npc;
import com.example.demo.entity.ReactionType;
import com.example.demo.repository.ChapterRepository;
import com.example.demo.service.AdminChapterService;
import com.example.demo.service.AdminDistrictService;
import com.example.demo.service.AdminMemoryFragmentService;
import com.example.demo.service.AdminNpcService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 企画書で確定している第1〜3章のコンテンツ(章・区画・NPC・反応テキスト・記憶断片)を
 * 初回起動時にDBへ投入する(チケット07)。チケット06で実装した Admin*Service を経由して
 * 作成するため、管理画面から手入力した場合と同じ手順・同じ入口でデータが構成される。
 *
 * chapterテーブルが空の場合のみ実行する(冪等)。管理画面で編集・削除した後の再投入は行わない。
 */
@Component
@RequiredArgsConstructor
public class ContentSeeder implements ApplicationRunner {

    private final ChapterRepository chapterRepository;
    private final AdminChapterService adminChapterService;
    private final AdminNpcService adminNpcService;
    private final AdminDistrictService adminDistrictService;
    private final AdminMemoryFragmentService adminMemoryFragmentService;

    @Override
    public void run(ApplicationArguments args) {
        if (chapterRepository.count() > 0) {
            return;
        }

        Chapter chapter1 = seedChapter(1, "第1章 依頼", """
                独立系の記憶商人ヴィオラの日常と、交渉パズルの基本を描く章。貧困街の靴職人ノラから
                「妹を飢えさせないために犯した過ち」の記憶を買い取る依頼を通じて、ヴィオラは
                買い取った記憶の感情の色合いに奇妙な既視感を覚える。相棒セレーネの意味深な言葉が、
                ヴィオラ自身の記憶に「不自然な空白」があることを匂わせ、章の終わりに彼女は
                自分自身の過去を探り始めることを決意する。""");

        Chapter chapter2 = seedChapter(2, "第2章 値札のない記憶", """
                商人としての依頼をこなしながら、ヴィオラは自分の過去の断片を集め始める。ノラの記憶の
                買い手である記憶保管人テオドールとの取引を通じ、記憶の売買が単純な搾取ではないことを
                知る一方、記録官ヴァイスには「個人の記憶売買記録は規則により非公開」と調査を拒まれる。
                正規の手段を断たれ裏路地のレイムを頼ると、レイムから「その記憶を取り戻したら、今の
                あんたじゃいられなくなるかもよ」という警告を受ける。ヴィオラはそれでも前へ進むことを
                選び、最後の鍵が相棒セレーネ自身にあることに気づいていく。""");

        Chapter chapter3 = seedChapter(3, "第3章 代償と選択", """
                最終交渉の相手は、これまでの誰とも違う——相棒セレーネ自身。相性表を駆使した駆け引き
                ではなく、ヴィオラがどこまで本音をさらけ出せるかが問われる、感情のクライマックスと
                なる。

                【真相】セレーネの正体は、ヴィオラがかつて売った「妹を守るために犠牲を払った記憶」
                そのものが、長い年月を経て意識を持つに至った存在だった。そして妹は生きており、
                ヴィオラの犠牲のおかげで安全に育ち、今は別の生活・別の家族の中で幸せに暮らしている。
                ただし、ヴィオラのことは覚えていない。

                【エンディング分岐】ヴィオラは妹に会いに行く/正体を明かすことはせず、遠くから妹の
                「今の幸せ」を見届けるに留める。記憶を取り戻すか否かの選択と共に、セレーネ(=かつて
                の記憶)はその役目を終えて静かに消えていく、あるいはヴィオラの一部として溶け込む
                かたちで着地する。再会という直接的なカタルシスではなく、「知ること」「受け入れる
                こと」そのものが救いになる、というダークだが静かに温かいラストへ至る。""");

        Npc nora = seedNpc("ノラ", "靴職人", EmotionTag.安心, EmotionTag.恐怖, chapter1.getId(), """
                ……あんたが噂の記憶商人? 別に、誰でもよかったんだけどね。

                妹を飢えさせないために犯した過ち……この記憶を、買い取ってほしい。ただし、信用できる
                相手にしか渡さない。""",
                List.of(
                        reaction(ReactionType.GOOD, "……悪くない。少しは、信じてもいいかもね。"),
                        reaction(ReactionType.GOOD, "そう、それは……ちょっとだけ、救われる話ね。"),
                        reaction(ReactionType.BAD, "……何それ。あんたも所詮、他人事なんだ。"),
                        reaction(ReactionType.BAD, "その話は……やめて。今は聞きたくない。"),
                        reaction(ReactionType.NEUTRAL, "……ふうん。"),
                        reaction(ReactionType.NEUTRAL, "それで?")));

        Npc vice = seedNpc("ヴァイス", "記録官", EmotionTag.誇り, EmotionTag.恥, chapter2.getId(), """
                規則に従っていただければ、取引は円滑に進みます。

                個人の記憶売買記録は規則により非公開です。……ですが、相応の信頼を示していただければ、
                話は別かもしれません。""",
                List.of(
                        reaction(ReactionType.GOOD, "……なるほど。あなたには一定の敬意を払いましょう。"),
                        reaction(ReactionType.GOOD, "その心構え、記録に値します。"),
                        reaction(ReactionType.BAD, "……見苦しい。私の立場を、辱めるおつもりか。"),
                        reaction(ReactionType.BAD, "それ以上は、規則違反として扱いますよ。"),
                        reaction(ReactionType.NEUTRAL, "……そうですか。"),
                        reaction(ReactionType.NEUTRAL, "続けてください。")));

        Npc reim = seedNpc("レイム", "記憶ブローカー", EmotionTag.愛情, EmotionTag.怒り, chapter2.getId(), """
                ……どこかで見た顔だったんだけどな。まあいいさ、商談といこうか。

                こっちの世界に、綺麗事は通じない。だが……本物の情ってやつは、たまに見せてくれると
                嬉しいね。

                (警告)……その記憶を取り戻したら、今のあんたじゃいられなくなるかもよ。""",
                List.of(
                        reaction(ReactionType.GOOD, "……ほぉ、悪くねえ。少しは信用してやるよ。"),
                        reaction(ReactionType.GOOD, "そういうの、嫌いじゃないぜ。"),
                        reaction(ReactionType.BAD, "……ちっ、ふざけてんのか。"),
                        reaction(ReactionType.BAD, "その怒り、こっちに向けるなよ。"),
                        reaction(ReactionType.NEUTRAL, "……で?"),
                        reaction(ReactionType.NEUTRAL, "それで終わりか?")));

        seedNpc("テオドール", "記憶保管人", EmotionTag.喜び, EmotionTag.悲しみ, chapter2.getId(), """
                苦しみや貧困の記憶を、娯楽や搾取のためではなく、名もなき人々の記録と弔いのために
                買い取り、この忘却院で保管している。……ヴァイス殿の紹介で来たのだろう? 記憶を売る、
                あるいは買うということが、必ずしも誰かを踏みにじる行為とは限らない。そのことを、
                少しだけ知ってもらえたら嬉しい。""",
                List.of(
                        reaction(ReactionType.GOOD, "……ふむ、悪くない話だ。誰かの喜びも、記録する価値がある。"),
                        reaction(ReactionType.GOOD, "その明るさ、この忘却院には珍しい。少し救われるよ。"),
                        reaction(ReactionType.BAD, "……その悲しみは、私には重すぎる。今は、そっとしておいてくれないか。"),
                        reaction(ReactionType.BAD, "古い傷を、無遠慮に暴くものではないよ。"),
                        reaction(ReactionType.NEUTRAL, "……そうか。"),
                        reaction(ReactionType.NEUTRAL, "続けて構わないよ。")));

        // セレーネとの最終交渉の扱い(通常の相性表ロジックを流用するか専用画面にするか)は
        // 要件定義書の未決事項1のため、weakTag/hateTagは意図的に未設定のままにしている。
        seedNpc("セレーネ", "相棒(最終交渉)", null, null, chapter3.getId(), """
                ……知りたい? けれど知ることが、あなたを守るとは限らないわ。

                これは、これまでの誰との交渉とも違う。駆け引きではなく、あなたがどこまで本音を
                さらけ出せるか——それが、問われる対話よ。""",
                List.of(
                        reaction(ReactionType.GOOD, "……そう。それが、あなたの本当の気持ちなのね。"),
                        reaction(ReactionType.GOOD, "……ありがとう。それだけで、少し救われる気がするわ。"),
                        reaction(ReactionType.BAD, "……まだ、目を逸らしているのね。"),
                        reaction(ReactionType.BAD, "……無理に、心を鎧うことはないのに。"),
                        reaction(ReactionType.NEUTRAL, "……そう。"),
                        reaction(ReactionType.NEUTRAL, "続けて。")));

        // 脇役・モブ: 第1章冒頭の雰囲気づけ用。会話ノルマ(交渉)はないため反応テキストは持たない。
        seedNpc("常連の老人", "常連客(モブ)", null, null, chapter1.getId(), """
                ……おや、また来たのかい。今日も変わらず、店先に座らせてもらうよ。この街は変わらんが、
                あんたの目つきは、少し変わった気がするな。""",
                List.of());

        adminDistrictService.save(districtForm("貧困街", "灰の裏通り。表通りから見捨てられた者たちの生きる場所。",
                chapter1.getId(), nora.getId()));
        adminDistrictService.save(districtForm("ギルド本部", "商人ギルド管理区。規則と記録がすべてを支配する。",
                chapter2.getId(), vice.getId()));
        adminDistrictService.save(districtForm("裏路地", "非合法の市場。値札のない記憶が、闇に流れる場所。",
                chapter2.getId(), reim.getId()));

        // 全依頼共通の相性表(4NPCタイプ×8感情タグ)に対応する記憶断片プール。
        // 「愛情」タグの断片は、第1章の既視感の伏線(妹を守るための記憶)を兼ねる。
        seedMemoryFragment(EmotionTag.喜び, "祭りの夜、はじけた笑い声");
        seedMemoryFragment(EmotionTag.悲しみ, "誰もいない部屋で見た夕焼け");
        seedMemoryFragment(EmotionTag.安心, "毛布にくるまれた、静かな夜");
        seedMemoryFragment(EmotionTag.恐怖, "暗闇で聞いた、知らない足音");
        seedMemoryFragment(EmotionTag.誇り, "やっと自分の名で呼ばれた日");
        seedMemoryFragment(EmotionTag.恥, "皆の前でついた、小さな嘘");
        seedMemoryFragment(EmotionTag.愛情, "誰かを守るために差し出した、名もなき覚悟");
        seedMemoryFragment(EmotionTag.怒り, "奪われたものへの、消えない怒り");
    }

    private Chapter seedChapter(int number, String title, String description) {
        ChapterForm form = new ChapterForm();
        form.setNumber(number);
        form.setTitle(title);
        form.setDescription(description);
        return adminChapterService.save(form);
    }

    private Npc seedNpc(String name, String role, EmotionTag weakTag, EmotionTag hateTag, Long chapterId,
            String introText, List<NpcReactionLineForm> reactionLines) {
        NpcForm form = new NpcForm();
        form.setName(name);
        form.setRole(role);
        form.setWeakTag(weakTag);
        form.setHateTag(hateTag);
        form.setChapterId(chapterId);
        form.setIntroText(introText);
        form.setReactionLines(new ArrayList<>(reactionLines));
        return adminNpcService.save(form);
    }

    private DistrictForm districtForm(String name, String role, Long chapterId, Long npcId) {
        DistrictForm form = new DistrictForm();
        form.setName(name);
        form.setRole(role);
        form.setChapterId(chapterId);
        form.setNpcId(npcId);
        return form;
    }

    private void seedMemoryFragment(EmotionTag tag, String title) {
        MemoryFragmentForm form = new MemoryFragmentForm();
        form.setTag(tag);
        form.setTitle(title);
        adminMemoryFragmentService.save(form);
    }

    private NpcReactionLineForm reaction(ReactionType type, String text) {
        NpcReactionLineForm form = new NpcReactionLineForm();
        form.setType(type);
        form.setText(text);
        return form;
    }
}
