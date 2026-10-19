package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("yularen", "1240_4");
                    put("aloo", "1230_18");
                    put("room", "1240_51");
                    put("trooper", "1_194");
                }},
                10,
                10,
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
    public void ColonelYularenStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("yularen").getBlueprint();

        assertEquals("Colonel Wullf Yularen", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(2, card.getInfluence(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.IMPERIAL_COUNCIL_MEMBER);
            add(Keyword.LEADER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.YULAREN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ColonelYularenMakesISBAgentsForfeitPlusOne() {
        var scn = GetScenario();
        var yularen = scn.GetDSCard("yularen");
        var trooper = scn.GetDSCard("trooper");
        var room = scn.GetDSCard("room");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToLocation(room, yularen, trooper);

        assertTrue(scn.game().getModifiersQuerying().hasAgenda(scn.gameState(), yularen, Agenda.LOGISTICS));
        assertEquals(2, scn.GetForfeit(trooper));

        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.KeywordModifier(yularen, trooper, Keyword.ISB_AGENT));
        assertEquals(3, scn.GetForfeit(trooper));
    }

    @Test
    public void ColonelYularenSubtractsOneFromOpponentDestinyWithSimAlooAtConferenceRoom() {
        var scn = GetDestinyScenario();
        var yularen = scn.GetDSCard("yularen");
        var aloo = scn.GetDSCard("aloo");
        var room = scn.GetDSCard("room");
        var luke = scn.GetLSCard("luke");
        var bay = scn.GetDSCard("bay");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(bay);
        scn.MoveCardsToLocation(room, yularen, aloo);
        scn.MoveCardsToLocation(bay, luke, scn.GetDSCard("trooper"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(1);
        scn.LSInitiateBattle(bay);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PrepareLSDestiny(3);
        scn.LSChooseYes();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        assertTrue("Yularen destiny subtract missing. decision=" + scn.DSGetDecision(),
                scn.DSCardActionAvailable(yularen, "Subtract 1 from opponent's destiny"));
        scn.DSUseCardAction(yularen, "Subtract 1 from opponent's destiny");
        scn.PassAllResponses();
        assertEquals(2, scn.GetLSTotalDestiny());
    }

    @Test
    public void ColonelYularenDoesNotOfferDestinySubtractWithoutSimAloo() {
        var scn = GetDestinyScenario();
        var yularen = scn.GetDSCard("yularen");
        var room = scn.GetDSCard("room");
        var luke = scn.GetLSCard("luke");
        var bay = scn.GetDSCard("bay");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(bay);
        scn.MoveCardsToLocation(room, yularen);
        scn.MoveCardsToLocation(bay, luke, scn.GetDSCard("trooper"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(1);
        scn.LSInitiateBattle(bay);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PrepareLSDestiny(3);
        scn.LSChooseYes();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(yularen, "Subtract 1 from opponent's destiny"));
    }

    @Test
    public void ColonelYularenDoesNotOfferDestinySubtractWhenNotAtConferenceRoom() {
        var scn = GetDestinyScenario();
        var yularen = scn.GetDSCard("yularen");
        var aloo = scn.GetDSCard("aloo");
        var bay = scn.GetDSCard("bay");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(bay);
        scn.MoveCardsToLocation(bay, yularen, aloo, luke);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(1);
        scn.LSInitiateBattle(bay);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.PrepareLSDestiny(3);
        scn.LSChooseYes();
        scn.DSPass();
        scn.LSPass();
        scn.DSPass();
        scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(yularen, "Subtract 1 from opponent's destiny"));
    }

    private VirtualTableScenario GetDestinyScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("yularen", "1240_4");
                    put("aloo", "1230_18");
                    put("room", "1240_51");
                    put("trooper", "1_194");
                    put("bay", "1_291");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }
}
