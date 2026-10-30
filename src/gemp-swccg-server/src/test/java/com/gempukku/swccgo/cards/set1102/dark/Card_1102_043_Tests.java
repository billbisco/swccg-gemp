package com.gempukku.swccgo.cards.set1102.dark;

import com.gempukku.swccgo.common.CardSubtype;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1102_043_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("sandcrawler", "1102_43");
                    put("jawa", "1_182");
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
    public void SandcrawlerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("sandcrawler").getBlueprint();

        assertEquals("Sandcrawler", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getArmor(), scn.epsilon);
        assertNull(card.getManeuver());
        assertEquals(2, card.getLandspeed(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(7, card.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        assertEquals(CardSubtype.TRANSPORT, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
            add(Keyword.SANDCRAWLER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.PILOT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SandcrawlerPermanentDriverProvidesNoAbility() {
        var scn = GetScenario();
        var sandcrawler = scn.GetDSCard("sandcrawler");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sandcrawler);
        assertEquals(0, scn.GetBattleDestinyAbility(sandcrawler));
    }

    @Test
    public void SandcrawlerMakesJawasAtSameSitePowerPlusOneAndForfeitPlusTwo() {
        var scn = GetScenario();
        var sandcrawler = scn.GetDSCard("sandcrawler");
        var jawa = scn.GetDSCard("jawa");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jawa, stormtrooper);
        assertEquals(1, scn.GetPower(jawa));
        assertEquals(1, scn.GetForfeit(jawa));
        int stormPower = scn.GetPower(stormtrooper);
        int stormForfeit = scn.GetForfeit(stormtrooper);
        scn.MoveCardsToLocation(site, sandcrawler);
        assertEquals(2, scn.GetPower(jawa));
        assertEquals(3, scn.GetForfeit(jawa));
        assertEquals(stormPower, scn.GetPower(stormtrooper));
        assertEquals(stormForfeit, scn.GetForfeit(stormtrooper));
    }

    @Test
    public void SandcrawlerDeploysAJawaHereFromReserveDeckDuringDeploy() {
        var scn = GetScenario();
        var sandcrawler = scn.GetDSCard("sandcrawler");
        var jawa = scn.GetDSCard("jawa");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sandcrawler);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(1);
        scn.EnsureLSForcePile(1);
        scn.MoveCardsToTopOfDSReserveDeck(jawa);
        assertTrue(scn.DSCardActionAvailable(sandcrawler, "Deploy a Jawa here"));
        scn.DSUseCardAction(sandcrawler, "Deploy a Jawa here");
        scn.DSChooseCard(jawa);
        if (scn.DSHasCardChoiceAvailable(site)) {
            scn.DSChooseCard(site);
        } else if (scn.DSHasCardChoiceAvailable(sandcrawler)) {
            scn.DSChooseCard(sandcrawler);
        }
        scn.PassAllResponses();
        assertTrue(jawa.getAtLocation() == site || jawa.getAttachedTo() == sandcrawler);
    }
}
