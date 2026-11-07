package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_058_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("cunning", "1250_58"); put("luke", "1_19"); put("jedi", "1_21"); put("guard", "1_26");
            put("ordinary", "1_60"); put("oldholo", "9_49"); put("leia", "1_17");
        }}, new HashMap<>() {{
            put("vader", "1_168"); put("sidious", "9_109"); put("holo", "12_139"); put("alter", "1_234");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void CunningHasFinalCloudCityMetadataAndDeploysOnTable() {
        var s = scenario(); var card = s.GetLSCard("cunning"); var bp = card.getBlueprint();
        assertEquals("A Jedi's Cunning", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(5, bp.getDestiny(), s.epsilon);
        assertEquals("Luke's sudden reversal of fortune allowed him to turn the tables on Vader.", bp.getLore());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
        s.StartGame(); s.MoveCardsToHand(card); s.SkipToLSTurn(Phase.DEPLOY);
        s.LSDeployCard(card); s.PassAllResponses(); assertEquals(Zone.SIDE_OF_TABLE, card.getZone());
    }

    private VirtualTableScenario startBattle(String fighter, String condition) {
        var s = scenario(); s.StartGame(); var site = s.GetDSStartingLocation();
        s.MoveCardsToLSSideOfTable(s.GetLSCard("cunning"));
        s.MoveCardsToLocation(site, s.GetLSCard(fighter), s.GetDSCard("vader"));
        if (fighter.equals("guard")) s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("jedi"));
        if ("sidious".equals(condition)) s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetDSCard("sidious"));
        if ("holo".equals(condition)) s.MoveCardsToDSSideOfTable(s.GetDSCard("holo"));
        if ("oldholo".equals(condition)) s.MoveCardsToLSSideOfTable(s.GetLSCard("oldholo"));
        s.DSActivateForceCheat(3); s.SkipToDSTurn(Phase.BATTLE);
        s.DSInitiateBattle(site); s.PassAllResponses();
        // The draw-count fixture supplies two extra native battle destinies; the real power segment resolves them.
        s.game().getModifiersEnvironment().addUntilEndOfBattleModifier(new AddsBattleDestinyModifier(s.GetDSCard("vader"), 2));
        return s;
    }

    private void draw(VirtualTableScenario s, int count) {
        assertEquals(3, s.GetDSBattleDestinyCount());
        assertEquals(count == 2 ? 2 : Integer.MAX_VALUE, s.GetDSBattleDestinyLimit());
        s.PrepareDSDestiny(3); s.PrepareDSDestiny(4); s.PrepareDSDestiny(5); s.PrepareLSDestiny(0);
        s.SkipToPowerSegment();
        assertTrue(s.GetCurrentDecision().getText(), s.DSDecisionAvailable("battle destiny"));
        s.DSChooseYes();
        for (int i = 0; i < 35 && s.GetCurrentDecision().getText().contains("Optional responses"); i++) {
            if (s.GetDSTotalDestiny() > 0) break;
            s.PlayerPass(s.GetDecidingPlayer());
        }
        // Premiere Vader adds one to each of the actual drawn values (5, 4, and, if legal, 3).
        assertEquals(count == 2 ? 11 : 15, s.GetDSTotalDestiny());
        assertTrue(s.GetDSUsedPile().containsAll(List.of(s.GetDSDestiny(5), s.GetDSDestiny(4))));
        assertEquals(count == 3, s.GetDSUsedPile().contains(s.GetDSDestiny(3)));
    }

    @Test
    public void SidiousAnywhereCapsActualOpponentDrawsAtFriendlyJediSite() { draw(startBattle("jedi", "sidious"), 2); }

    @Test
    public void EpisodeOneHologramAloneAlsoCapsActualDraws() { draw(startBattle("jedi", "holo"), 2); }

    @Test
    public void NonEpisodeOneHologramDoesNotCapActualDraws() { draw(startBattle("jedi", "oldholo"), 3); }

    @Test
    public void SidiousDoesNotCapBattleAtAnotherSiteWithoutTheJedi() { draw(startBattle("guard", "sidious"), 3); }

    @Test
    public void LukePresentCapsActualDrawsWithoutSidiousOrHologram() { draw(startBattle("luke", null), 2); }

    @Test
    public void WithoutEitherConditionNonLukeJediDoesNotCapDraws() { draw(startBattle("jedi", null), 3); }

    @Test
    public void ActualAlterCannotCancelCunningButCancelsOrdinaryEffect() {
        var s = scenario(); s.StartGame(); var cunning = s.GetLSCard("cunning"); var ordinary = s.GetLSCard("ordinary");
        s.MoveCardsToLSSideOfTable(cunning); s.AttachCardsTo(s.GetLSStartingLocation(), ordinary);
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("vader"));
        var alter = s.GetDSCard("alter"); s.MoveCardsToHand(alter); s.SkipToDSTurn(Phase.CONTROL); s.PrepareDSDestiny(3);
        s.DSPlayCard(alter, "cancel Effect");
        if (s.DSDecisionAvailable("Choose Effect")) {
            assertFalse(s.DSHasCardChoiceAvailable(cunning)); s.DSChooseCard(ordinary);
        }
        if (s.DSDecisionAvailable("Choose a highest-ability character")) s.DSChooseCard(s.GetDSCard("vader"));
        s.PassDestinyDrawResponses(); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(ordinary)); assertEquals(Zone.SIDE_OF_TABLE, cunning.getZone());
    }

    /** Obi-Wan (Jedi, immune < 5) and Leia (non-Jedi, immune < 2) with a cancel-immunity modifier from the given card. */
    private float[] immunityAfterCancel(boolean sidious, boolean cancelFromOpponent) {
        var s = scenario(); s.StartGame(); var site = s.GetLSStartingLocation();
        var obiWan = s.GetLSCard("jedi"); var leia = s.GetLSCard("leia");
        s.MoveCardsToLSSideOfTable(s.GetLSCard("cunning"));
        s.MoveCardsToLocation(site, obiWan, leia);
        if (sidious) s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("sidious"));
        PhysicalCard canceler = cancelFromOpponent ? s.GetDSCard("vader") : s.GetLSCard("ordinary");
        s.ApplyAdHocModifier(new CancelImmunityToAttritionModifier(canceler, Filters.or(Filters.sameCardId(obiWan), Filters.sameCardId(leia))));
        var q = s.game().getModifiersQuerying();
        return new float[] {q.getImmunityToAttritionLessThan(s.gameState(), obiWan), q.getImmunityToAttritionLessThan(s.gameState(), leia)};
    }

    @Test
    public void WithSidiousOpponentMayNotCancelYourJedisImmunity() {
        var immunity = immunityAfterCancel(true, true);
        assertEquals(5, immunity[0], 0.01);
        // Leia is not a Jedi, so the opponent's cancel still applies to her.
        assertEquals(0, immunity[1], 0.01);
    }

    @Test
    public void WithoutSidiousOrHologramOpponentStillCancelsJediImmunity() {
        assertEquals(0, immunityAfterCancel(false, true)[0], 0.01);
    }

    @Test
    public void YourOwnCardsMayStillCancelYourJedisImmunity() {
        assertEquals(0, immunityAfterCancel(true, false)[0], 0.01);
    }

    @Test
    public void WithoutAnyCancelJediKeepsImmunity() {
        var s = scenario(); s.StartGame(); var obiWan = s.GetLSCard("jedi");
        s.MoveCardsToLSSideOfTable(s.GetLSCard("cunning"));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), obiWan);
        assertEquals(5, s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), obiWan), 0.01);
    }
}
