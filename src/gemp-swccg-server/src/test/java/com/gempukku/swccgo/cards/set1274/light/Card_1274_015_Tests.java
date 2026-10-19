package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("mace", "1274_15");
                    put("obi", "1_21");
                    put("yoda", "4_2");
                    put("blaster", "1_152");
                    put("luke", "1_19");
                    put("junk", "1_17");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("trooper", "1_194");
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
    public void MaceWinduMasterOfTheOrderStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("mace").getBlueprint();

        assertEquals("Mace Windu, Master Of The Order", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(7, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(7, card.getAbility(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.JEDI_MASTER);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.JEDI_COUNCIL_MEMBER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.MACE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.CORUSCANT);
            add(Icon.JEDI_MASTER);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MaceWinduDestinyIsOneOrNumberOfJediOnTableToAMaximumOfThree() {
        var scn = GetScenario();
        var mace = scn.GetLSCard("mace");
        var obi = scn.GetLSCard("obi");
        var yoda = scn.GetLSCard("yoda");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        assertEquals(1, scn.GetDestiny(mace));
        scn.MoveCardsToLocation(site, mace);
        assertEquals(1, scn.GetDestiny(mace));
        scn.MoveCardsToLocation(site, obi);
        assertEquals(2, scn.GetDestiny(mace));
        scn.MoveCardsToLocation(site, yoda);
        assertEquals(3, scn.GetDestiny(mace));
    }

    @Test
    public void MaceWinduForfeitMayNotBeReduced() {
        var scn = GetScenario();
        var mace = scn.GetLSCard("mace");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, mace);
        assertEquals(8, scn.GetForfeit(mace));
        scn.ApplyAdHocModifier(new ForfeitModifier(mace, Filters.sameCardId(mace), -3));
        assertEquals(8, scn.GetForfeit(mace));
    }

    @Test
    public void MaceWinduTotalWeaponDestinyIsPlusOne() {
        var scn = GetScenario();
        var mace = scn.GetLSCard("mace");
        var blaster = scn.GetLSCard("blaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, mace, trooper);
        scn.AttachCardsTo(mace, blaster);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(4);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(1);
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(trooper));
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();
        assertEquals(2, lastTotalWeaponDestiny(scn));
    }

    @Test
    public void MaceWinduMayRetrieveOneForceWhenADarkJediIsJustLost() {
        var scn = GetScenario();
        var mace = scn.GetLSCard("mace");
        var luke = scn.GetLSCard("luke");
        var junk = scn.GetLSCard("junk");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, mace, luke, vader);
        scn.MoveCardsToTopOfLSLostPile(junk);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToEndOfPowerSegment(false);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSBattleDamagePayment());
        scn.DSChooseCard(vader);
        assertTrue(scn.LSCardActionAvailable(mace, "Retrieve 1 Force"));
        scn.LSUseCardAction(mace, "Retrieve 1 Force");
        scn.PassAllResponses();
        assertTrue(junk.getZone() == Zone.USED_PILE || junk.getZone() == Zone.TOP_OF_USED_PILE
                || junk.getZone() == Zone.HAND);
        assertTrue(vader.getZone() == Zone.LOST_PILE || vader.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void MaceWinduIsImmuneToAttrition() {
        var scn = GetScenario();
        var mace = scn.GetLSCard("mace");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, mace);
        assertEquals(Float.MAX_VALUE, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), mace), 1f);
    }

    private int lastTotalWeaponDestiny(VirtualTableScenario scn) {
        Integer last = null;
        String log = String.join("\n", scn.gameState().getLastMessages());
        for (String msg : scn.gameState().getLastMessages()) {
            String lower = msg.toLowerCase();
            for (String prefix : new String[] {"total weapon destiny is ", "total destiny: "}) {
                int idx = lower.lastIndexOf(prefix);
                if (idx >= 0) {
                    String rest = msg.substring(idx + prefix.length()).trim();
                    last = (int) Float.parseFloat(rest.split("[^0-9.]")[0]);
                }
            }
        }
        assertTrue("Expected a total weapon destiny log, got: " + log, last != null);
        return last;
    }
}
