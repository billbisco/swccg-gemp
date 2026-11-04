package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("obiwan", "1_21");
                    put("communing", "216_24");
                }},
                new HashMap<>() {{
                    put("junkyard", "1283_14");
                    put("watto", "11_65");
                    put("vader", "1_168");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void TatooineWattosJunkyardVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("junkyard").getBlueprint();
        assertEquals(Title.Wattos_Junkyard, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.EPISODE_I);
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TatooineWattosJunkyardVDeploysWattoHereFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var junkyard = scn.GetDSCard("junkyard");
        var watto = scn.GetDSCard("watto");
        scn.StartGame();
        scn.MoveLocationToTable(junkyard);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(watto);
        assertTrue(scn.DSCardActionAvailable(junkyard, "Deploy Watto from Reserve Deck"));
        scn.DSUseCardAction(junkyard, "Deploy Watto from Reserve Deck");
        scn.DSChooseCard(watto);
        scn.PassAllResponses();
        assertAtLocation(junkyard, watto);
        assertFalse(scn.GetDSAvailableActions().contains("Deploy Watto from Reserve Deck"));
    }

    private void battleAtJunkyard(VirtualTableScenario scn, boolean wattoHere) {
        var junkyard = scn.GetDSCard("junkyard");
        var watto = scn.GetDSCard("watto");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(junkyard);
        scn.MoveCardsToLocation(junkyard, vader, luke);
        if (wattoHere) {
            scn.MoveCardsToLocation(junkyard, watto);
        }
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(junkyard);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(3);
        scn.PrepareDSDestiny(3);
        scn.SkipToEndOfPowerSegment(true);
    }

    @Test
    public void TatooineWattosJunkyardVOpponentMayNotDrawBattleDestinyHereWhileWattoHere() {
        var scn = GetScenario();
        battleAtJunkyard(scn, true);
        assertEquals(0, scn.GetLSTotalDestiny());
    }

    @Test
    public void TatooineWattosJunkyardVOpponentDrawsBattleDestinyWithoutWatto() {
        var scn = GetScenario();
        battleAtJunkyard(scn, false);
        assertEquals(3, scn.GetLSTotalDestiny());
    }

    private void obiWanCommuning(VirtualTableScenario scn) {
        var communing = scn.GetLSCard("communing");
        var obiwan = scn.GetLSCard("obiwan");
        scn.MoveCardsToLSSideOfTable(communing);
        scn.StackCardsOn(communing, obiwan);
    }

    @Test
    public void TatooineWattosJunkyardVMayNotBeConvertedWhileObiWanCommuning() {
        var scn = GetScenario();
        var junkyard = scn.GetDSCard("junkyard");
        scn.StartGame();
        scn.MoveLocationToTable(junkyard);
        assertFalse(scn.game().getModifiersQuerying().cannotBeConverted(scn.game().getGameState(), junkyard));
        obiWanCommuning(scn);
        assertTrue(scn.game().getModifiersQuerying().isCommuning(scn.game().getGameState(), com.gempukku.swccgo.filters.Filters.ObiWan));
        assertTrue(scn.game().getModifiersQuerying().cannotBeConverted(scn.game().getGameState(), junkyard));
    }

    private boolean darkAttritionResetToZeroAtJunkyard(boolean communing) {
        var scn = GetScenario();
        var junkyard = scn.GetDSCard("junkyard");
        var watto = scn.GetDSCard("watto");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(junkyard);
        scn.MoveCardsToLocation(junkyard, vader, watto, luke);
        if (communing) {
            obiWanCommuning(scn);
        }
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(junkyard);
        scn.PassAllResponses();
        var gs = scn.game().getGameState();
        for (var modifier : scn.game().getModifiersQuerying().getModifiersAffectingCard(gs, ModifierType.UNMODIFIABLE_ATTRITION, junkyard)) {
            if (modifier.getSource(gs) == junkyard
                    && "Attrition against dark side =0".equals(modifier.getText(gs, scn.game().getModifiersQuerying(), junkyard))) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void TatooineWattosJunkyardVResetsAttritionAgainstDarkToZeroWhileObiWanCommuningAndWattoHere() {
        assertFalse(darkAttritionResetToZeroAtJunkyard(false));
        assertTrue(darkAttritionResetToZeroAtJunkyard(true));
    }
}
