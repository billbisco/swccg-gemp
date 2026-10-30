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
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1102_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("sandcrawler", "1102_22");
                    put("jawa", "1_012");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
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
        var card = scn.GetLSCard("sandcrawler").getBlueprint();

        assertEquals("Sandcrawler", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
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
        var sandcrawler = scn.GetLSCard("sandcrawler");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sandcrawler);
        assertEquals(0, scn.GetBattleDestinyAbility(sandcrawler));
    }

    @Test
    public void SandcrawlerMakesJawasAtSameSitePowerPlusOneAndForfeitPlusTwo() {
        var scn = GetScenario();
        var sandcrawler = scn.GetLSCard("sandcrawler");
        var jawa = scn.GetLSCard("jawa");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, jawa, luke);
        assertEquals(1, scn.GetPower(jawa));
        assertEquals(1, scn.GetForfeit(jawa));
        int lukePower = scn.GetPower(luke);
        scn.MoveCardsToLocation(site, sandcrawler);
        assertEquals(2, scn.GetPower(jawa));
        assertEquals(3, scn.GetForfeit(jawa));
        assertEquals(lukePower, scn.GetPower(luke));
    }

    @Test
    public void SandcrawlerDeploysAJawaHereFromReserveDeckDuringDeploy() {
        var scn = GetScenario();
        var sandcrawler = scn.GetLSCard("sandcrawler");
        var jawa = scn.GetLSCard("jawa");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sandcrawler);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(1);
        scn.EnsureDSForcePile(1);
        scn.MoveCardsToTopOfLSReserveDeck(jawa);
        assertTrue(scn.LSCardActionAvailable(sandcrawler, "Deploy a Jawa here"));
        scn.LSUseCardAction(sandcrawler, "Deploy a Jawa here");
        scn.LSChooseCard(jawa);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        } else if (scn.LSHasCardChoiceAvailable(sandcrawler)) {
            scn.LSChooseCard(sandcrawler);
        }
        scn.PassAllResponses();
        assertTrue(jawa.getAtLocation() == site || jawa.getAttachedTo() == sandcrawler);
    }
}
