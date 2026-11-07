package com.gempukku.swccgo.cards.set1240.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_096_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("shoo", "1240_96");
                }},
                new HashMap<>() {{
                    put("wampa", "3_93");
                    put("mynock", "4_110");
                    put("iceplains", "3_148");
                    put("northridge", "3_149");
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
    public void ShooShooVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("shoo").getBlueprint();

        assertEquals("Shoo! Shoo!", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Sense));
    }

    @Test
    public void ShooShooVRelocatesOpponentsFerociousCreatureToRelatedSite() {
        var scn = GetScenario();
        var shoo = scn.GetLSCard("shoo");
        var wampa = scn.GetDSCard("wampa");
        var iceplains = scn.GetDSCard("iceplains");
        var northridge = scn.GetDSCard("northridge");

        scn.StartGame();
        scn.MoveLocationToTable(iceplains);
        scn.MoveLocationToTable(northridge);
        scn.MoveCardsToLocation(iceplains, wampa);
        scn.MoveCardsToLSHand(shoo);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardActionAvailable(shoo, "Relocate opponent's ferocious creature"));
        scn.LSUseCardAction(shoo, "Relocate opponent's ferocious creature");
        if (scn.LSHasCardChoiceAvailable(wampa)) {
            scn.LSChooseCard(wampa);
        }
        if (scn.LSHasCardChoiceAvailable(northridge)) {
            scn.LSChooseCard(northridge);
        }
        scn.PassAllResponses();
        assertAtLocation(northridge, wampa);
    }

    @Test
    public void ShooShooVDoesNotRelocateAParasite() {
        var scn = GetScenario();
        var shoo = scn.GetLSCard("shoo");
        var mynock = scn.GetDSCard("mynock");
        var iceplains = scn.GetDSCard("iceplains");
        var northridge = scn.GetDSCard("northridge");

        scn.StartGame();
        scn.MoveLocationToTable(iceplains);
        scn.MoveLocationToTable(northridge);
        scn.MoveCardsToLocation(iceplains, mynock);
        scn.MoveCardsToLSHand(shoo);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardActionAvailable(shoo, "Relocate opponent's ferocious creature"));
    }

    @Test
    public void ShooShooVInitiatesAttackOnlyDuringMovePhase() {
        var scn = GetScenario();
        var shoo = scn.GetLSCard("shoo");
        var wampa = scn.GetDSCard("wampa");
        var iceplains = scn.GetDSCard("iceplains");

        scn.StartGame();
        scn.MoveLocationToTable(iceplains);
        scn.MoveCardsToLocation(iceplains, wampa);
        scn.MoveCardsToLSHand(shoo);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(shoo, "Initiate an attack against a creature"));

        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.LSCardActionAvailable(shoo, "Initiate an attack against a creature"));
    }

    private VirtualTableScenario GetReegeskScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("shoo", "1240_96");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("reegesk", "1210_18");
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
    public void ShooShooVCancelsReegeskStealAndReegeskIsLost() {
        var scn = GetReegeskScenario();
        var shoo = scn.GetLSCard("shoo");
        var xwing = scn.GetLSCard("xwing");
        var reegesk = scn.GetDSCard("reegesk");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, reegesk);
        scn.MoveCardsToTopOfLSLostPile(xwing);
        scn.MoveCardsToLSHand(shoo);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();

        assertTrue(scn.DSCardActionAvailable(reegesk, "Steal card from Lost Pile"));
        scn.DSUseCardAction(reegesk, "Steal card from Lost Pile");

        assertTrue(scn.LSCardPlayAvailable(shoo, "Cancel Reegesk steal"));
        scn.LSPlayCard(shoo, "Cancel Reegesk steal");
        scn.PassAllResponses();

        assertTrue(scn.GetLSLostPile().contains(xwing));
        assertEquals(scn.LS, xwing.getOwner());
        assertTrue(scn.GetDSLostPile().contains(reegesk));
    }

    @Test
    public void ShooShooVNotUsedReegeskStealSucceeds() {
        var scn = GetReegeskScenario();
        var shoo = scn.GetLSCard("shoo");
        var xwing = scn.GetLSCard("xwing");
        var reegesk = scn.GetDSCard("reegesk");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, reegesk);
        scn.MoveCardsToTopOfLSLostPile(xwing);
        scn.MoveCardsToLSHand(shoo);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();

        scn.DSUseCardAction(reegesk, "Steal card from Lost Pile");
        assertTrue(scn.LSCardPlayAvailable(shoo, "Cancel Reegesk steal"));
        scn.PassAllResponses();
        scn.DSChooseCard(xwing);
        scn.PassAllResponses();

        assertEquals(Zone.HAND, xwing.getZone());
        assertEquals(scn.DS, xwing.getOwner());
        assertEquals(Zone.HAND, shoo.getZone());
        assertAtLocation(site, reegesk);
    }
}
