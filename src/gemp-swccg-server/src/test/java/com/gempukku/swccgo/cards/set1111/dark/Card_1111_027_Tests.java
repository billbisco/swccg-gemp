package com.gempukku.swccgo.cards.set1111.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1111_027_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ben", "7_4");
                    put("farm", "1_132");
                }},
                new HashMap<>() {{
                    put("lukeCall", "1111_27");
                    put("stormtrooper", "1_194");
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
    public void LukeLuuuukeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("lukeCall").getBlueprint();

        assertEquals("Luke? Luuuuke!", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.UTINNI, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_11, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LukeLuuuukeDeploysOnYourNonDroidAtABattlegroundSiteAndTargetsAJedi() {
        var scn = GetScenario();
        var lukeCall = scn.GetDSCard("lukeCall");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var ben = scn.GetLSCard("ben");

        scn.StartGame();
        var dsSite = scn.GetDSStartingLocation();
        var farm = scn.GetLSCard("farm");
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(dsSite, stormtrooper);
        scn.MoveCardsToLocation(farm, ben);
        scn.MoveCardsToDSHand(lukeCall);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSCardPlayAvailable(lukeCall));
        scn.DSPlayCard(lukeCall);
        if (scn.DSHasCardChoiceAvailable(stormtrooper)) {
            scn.DSChooseCard(stormtrooper);
        }
        if (scn.DSHasCardChoiceAvailable(ben)) {
            scn.DSChooseCard(ben);
        }
        scn.PassAllResponses();

        assertEquals(stormtrooper, lukeCall.getAttachedTo());
    }

    @Test
    public void LukeLuuuukeHostMayNotMove() {
        var scn = GetScenario();
        var lukeCall = scn.GetDSCard("lukeCall");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var ben = scn.GetLSCard("ben");

        scn.StartGame();
        var dsSite = scn.GetDSStartingLocation();
        var farm = scn.GetLSCard("farm");
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(dsSite, stormtrooper);
        scn.MoveCardsToLocation(farm, ben);
        scn.AttachCardsTo(stormtrooper, lukeCall);
        lukeCall.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, ben, Filters.Jedi);

        assertTrue(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), stormtrooper));
    }

    @Test
    public void LukeLuuuukeLimitsForceLossFromForceDrainsAtTheJediLocationToOne() {
        var scn = GetScenario();
        var lukeCall = scn.GetDSCard("lukeCall");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var ben = scn.GetLSCard("ben");

        scn.StartGame();
        var dsSite = scn.GetDSStartingLocation();
        var farm = scn.GetLSCard("farm");
        var lsSite = scn.GetLSStartingLocation();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(dsSite, stormtrooper);
        scn.MoveCardsToLocation(lsSite, ben);
        scn.AttachCardsTo(stormtrooper, lukeCall);
        lukeCall.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, ben, Filters.Jedi);

        scn.SkipToLSTurn(Phase.CONTROL);
        int lifeBefore = scn.GetDSLifeForceRemaining();
        assertTrue(scn.LSForceDrainAvailable(lsSite));
        scn.LSForceDrainAt(lsSite);
        scn.PassForceDrainStartResponses();
        if (scn.DSDecisionAvailable("Choose Force to lose")) {
            if (scn.GetDSForcePileCount() > 0) {
                scn.DSChooseCard(scn.GetTopOfDSForcePile());
            } else {
                scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
            }
        }
        scn.PassForceDrainEndResponses();

        assertTrue(scn.GetDSLifeForceRemaining() < lifeBefore);
        assertTrue(lifeBefore - scn.GetDSLifeForceRemaining() <= 1);
    }

    @Test
    public void LukeLuuuukeIsCanceledWhenReachedByTarget() {
        var scn = GetScenario();
        var lukeCall = scn.GetDSCard("lukeCall");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var ben = scn.GetLSCard("ben");

        scn.StartGame();
        var dsSite = scn.GetDSStartingLocation();
        var farm = scn.GetLSCard("farm");
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(dsSite, stormtrooper);
        scn.MoveCardsToLocation(farm, ben);
        scn.AttachCardsTo(stormtrooper, lukeCall);
        lukeCall.setTargetedCard(TargetId.UTINNI_EFFECT_TARGET_1, 0, ben, Filters.Jedi);

        scn.MoveCardsToLocation(dsSite, ben);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, lukeCall);
    }
}
