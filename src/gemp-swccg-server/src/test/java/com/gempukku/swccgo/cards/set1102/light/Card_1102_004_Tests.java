package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1102_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("brawl", "1102_4");
                    put("luke", "1_19");
                    put("lsblaster", "1_152");
                    put("cantina", "1_128");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("dsblaster", "1_317");
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
    public void CantinaBrawlStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("brawl").getBlueprint();

        assertEquals("Cantina Brawl", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CantinaBrawlPlaysWhenBattleIsInitiatedAtAnInteriorSiteWithArmedCharacters() {
        var scn = GetScenario();
        var brawl = scn.GetLSCard("brawl");
        var luke = scn.GetLSCard("luke");
        var lsblaster = scn.GetLSCard("lsblaster");
        var trooper = scn.GetDSCard("trooper");
        var dsblaster = scn.GetDSCard("dsblaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.AttachCardsTo(luke, lsblaster);
        scn.AttachCardsTo(trooper, dsblaster);
        scn.MoveCardsToLSHand(brawl);
        scn.SkipToLSTurn(Phase.BATTLE);

        scn.LSInitiateBattle(site);
        scn.PassForceUseResponses();
        assertTrue(scn.LSCardPlayAvailable(brawl) || scn.LSActionAvailable("Add"));
        if (scn.LSCardPlayAvailable(brawl, "Add")) {
            scn.LSPlayCard(brawl, "Add");
        } else if (scn.LSCardPlayAvailable(brawl)) {
            scn.LSPlayCard(brawl);
        } else {
            scn.LSChooseAction("Add");
        }
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, brawl);
    }

    @Test
    public void CantinaBrawlPlaysAtTheCantina() {
        var scn = GetScenario();
        var brawl = scn.GetLSCard("brawl");
        var luke = scn.GetLSCard("luke");
        var lsblaster = scn.GetLSCard("lsblaster");
        var cantina = scn.GetLSCard("cantina");
        var trooper = scn.GetDSCard("trooper");
        var dsblaster = scn.GetDSCard("dsblaster");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(cantina, luke, trooper);
        scn.AttachCardsTo(luke, lsblaster);
        scn.AttachCardsTo(trooper, dsblaster);
        scn.MoveCardsToLSHand(brawl);
        scn.SkipToLSTurn(Phase.BATTLE);

        scn.LSInitiateBattle(cantina);
        scn.PassForceUseResponses();
        assertTrue(scn.LSCardPlayAvailable(brawl));
        scn.LSPlayCard(brawl);
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, brawl);
    }
}
