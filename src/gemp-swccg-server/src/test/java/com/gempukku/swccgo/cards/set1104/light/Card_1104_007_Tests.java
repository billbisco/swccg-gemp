package com.gempukku.swccgo.cards.set1104.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1104_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("frostbite", "1104_7");
                    put("hoth", "3_55");
                    put("mpg", "3_61");
                    put("ridge", "3_62");
                    put("luke", "1_19");
                    put("rogue1", "3_66");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("tie", "1_304");
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
    public void FrostbiteStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("frostbite").getBlueprint();

        assertEquals("Frostbite", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_LOCATION);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void FrostbiteDeploysOnHothSystemWhenYouControlTwoExteriorHothSites() {
        var scn = GetScenario();
        var frostbite = scn.GetLSCard("frostbite");
        var hoth = scn.GetLSCard("hoth");
        var mpg = scn.GetLSCard("mpg");
        var ridge = scn.GetLSCard("ridge");
        var luke = scn.GetLSCard("luke");
        var warrior = scn.GetLSFiller(1);

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveLocationToTable(mpg);
        scn.MoveLocationToTable(ridge);
        scn.MoveCardsToLocation(mpg, luke);
        scn.MoveCardsToLocation(ridge, warrior);
        scn.MoveCardsToLSHand(frostbite);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(frostbite));
        scn.LSPlayCard(frostbite);
        scn.LSChooseCard(hoth);
        scn.PassAllResponses();
        assertEquals(hoth, frostbite.getAttachedTo());
    }

    @Test
    public void FrostbiteIsImmuneToAlterWhileYouOccupyFirstMarker() {
        var scn = GetScenario();
        var frostbite = scn.GetLSCard("frostbite");
        var hoth = scn.GetLSCard("hoth");
        var mpg = scn.GetLSCard("mpg");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveLocationToTable(mpg);
        scn.MoveCardsToLocation(mpg, luke);
        scn.AttachCardsTo(hoth, frostbite);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), frostbite, Title.Alter));
    }

    @Test
    public void FrostbiteIsLostWhenOpponentControlsHothSystem() {
        var scn = GetScenario();
        var frostbite = scn.GetLSCard("frostbite");
        var hoth = scn.GetLSCard("hoth");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.AttachCardsTo(hoth, frostbite);
        scn.MoveCardsToLocation(hoth, tie);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, frostbite);
    }

    @Test
    public void FrostbiteRetrievesOneForcePerHothSiteYouControlWithAUniqueCombatVehicle() {
        var scn = GetScenario();
        var frostbite = scn.GetLSCard("frostbite");
        var hoth = scn.GetLSCard("hoth");
        var mpg = scn.GetLSCard("mpg");
        var luke = scn.GetLSCard("luke");
        var rogue1 = scn.GetLSCard("rogue1");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveLocationToTable(mpg);
        scn.MoveCardsToLocation(mpg, luke, rogue1);
        scn.AttachCardsTo(hoth, frostbite);
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSFiller(2));
        int before = scn.GetLSLifeForceRemaining();
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(frostbite, "Retrieve 1 Force"));
        scn.LSUseCardAction(frostbite, "Retrieve 1 Force");
        scn.PassAllResponses();
        assertEquals(before + 1, scn.GetLSLifeForceRemaining());
    }
}
