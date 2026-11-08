package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card_1230_077_Tests;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Original Orrimaarko (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_77. */
public class Card_2230_077_Tests extends Card_1230_077_Tests {
    @Override
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("orri", "2230_77");
                    put("current", "1230_77");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317"); // Imperial Blaster
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

    // The inherited tests that hold for both versions (stats, the -2 while alone) run again on this version; the ones
    // that do not are overridden below.

    @Override
    @Test
    public void OrrimaarkoHasTheErrataText() {
        var scn = GetScenario();
        var card = scn.GetLSCard("orri").getBlueprint();
        assertEquals("Orrimaarko", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.REBEL); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.SCOUT); add(Keyword.LEADER); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.REBEL); add(Icon.WARRIOR); add(Icon.ENDOR); add(Icon.LEGACY_BLOCK_3); }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals("Once per game, may take a rifle and/or an Interrupt with 'scout' in game text into hand from Reserve Deck; reshuffle. Unless Undercover, while alone at an exterior site, opponent's weapon destiny draws here are -2. Immune to attrition < 4.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1230_77", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2012, 8, 27), version.supersededOn());
        assertEquals("Orrimaarko (V)", GameUtils.getFullName(card));
    }

    @Override
    @Test
    public void OrrimaarkoWeaponDestinyClauseAppliesEvenWhileUndercover() {
        // Original text: "Unless Undercover, ..."
        var scn = GetScenario();
        assertEquals(0, weaponDestinyMinusTwoHere(scn, scn.GetLSCard("orri"), true));
    }

    @Override
    @Test
    public void OrrimaarkoAndHisOriginalVersionAreTheSameUniqueCard() {
        for (boolean currentOnTable : List.of(true, false)) {
            for (boolean otherOut : List.of(false, true)) {
                var scn = GetScenario();
                var onTable = scn.GetLSCard(currentOnTable ? "current" : "orri");
                var inHand = scn.GetLSCard(currentOnTable ? "orri" : "current");
                scn.StartGame();
                if (otherOut)
                    scn.MoveCardsToLocation(scn.GetLSStartingLocation(), onTable);
                scn.MoveCardsToLSHand(inHand);
                scn.EnsureLSForcePile(8);
                scn.SkipToLSTurn(Phase.DEPLOY);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.LSDeployAvailable(inHand));
            }
        }
    }
}
