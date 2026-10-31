package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.common.CardSubtype;
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

public class Card_1112_001_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("saber", "1112_1");
                    put("leia", "1_17");
                    put("jedi", "9_24");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("rifle", "1_312");
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
    public void AnakinsLightsaberStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("saber").getBlueprint();

        assertEquals("Anakin's Lightsaber", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.ANAKINS_LIGHTSABER);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LIGHTSABER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.HOTH);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AnakinsLightsaberDeploysOnLeiaAndNotOnJediLuke() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var leia = scn.GetLSCard("leia");
        var jedi = scn.GetLSCard("jedi");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, jedi);
        scn.MoveCardsToLSHand(saber);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(saber));
        scn.LSDeployCard(saber);
        assertTrue(scn.LSHasCardChoiceAvailable(leia));
        assertFalse(scn.LSHasCardChoiceAvailable(jedi));
    }

    @Test
    public void AnakinsLightsaberMayAddOneToForceDrainWherePresent() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        scn.AttachCardsTo(leia, saber);
        scn.EnsureDSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL);
        int beforeLost = scn.GetDSLostPileCount();
        assertTrue(scn.LSForceDrainAvailable(site));
        scn.LSForceDrainAt(site);
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(saber, "Add 1 to Force drain"));
        scn.LSUseCardAction(saber, "Add 1 to Force drain");
        scn.PassForceUseResponses();
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        assertTrue(scn.IsActiveForceDrain());
        assertEquals(2, scn.GetForceDrainTotal());
        assertTrue(scn.DSDecisionAvailable("Choose Force to lose"));
        scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
        scn.PassForceDrainPendingResponses();
        assertTrue(scn.DSDecisionAvailable("Choose Force to lose"));
        scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
        scn.PassForceDrainPendingResponses();
        assertEquals(2, scn.GetDSLostPileCount() - beforeLost);
    }

    @Test
    public void AnakinsLightsaberHitsCharacterWhenTwoDestinyExceedDefenseValueAndResetsForfeitToZero() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var leia = scn.GetLSCard("leia");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, stormtrooper);
        scn.AttachCardsTo(leia, saber);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(saber, "Fire"));
        scn.PrepareLSDestiny(1);
        scn.PrepareLSDestiny(1);
        scn.LSUseCardAction(saber, "Fire");
        scn.LSChooseCard(stormtrooper);
        scn.PassForceUseResponses();
        scn.PassWeaponFireWithDestinyDraw(2);
        scn.PassAllResponses();
        assertTrue(stormtrooper.isHit());
        assertEquals(0, scn.GetForfeit(stormtrooper));
    }

    @Test
    public void AnakinsLightsaberDoesNotHitWhenTwoDestinyDoNotExceedDefenseValue() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var leia = scn.GetLSCard("leia");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, vader);
        scn.AttachCardsTo(leia, saber);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(0);
        scn.PrepareLSDestiny(0);
        scn.LSUseCardAction(saber, "Fire");
        scn.LSChooseCard(vader);
        scn.PassForceUseResponses();
        scn.PassWeaponFireWithDestinyDraw(2);
        scn.PassAllResponses();
        assertFalse(vader.isHit());
    }

    @Test
    public void AnakinsLightsaberMayCancelAJustDrawnWeaponDestinyHereInsteadOfFiring() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var leia = scn.GetLSCard("leia");
        var rifle = scn.GetDSCard("rifle");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, stormtrooper);
        scn.AttachCardsTo(leia, saber);
        scn.AttachCardsTo(stormtrooper, rifle);
        scn.EnsureLSForcePile(2);
        scn.EnsureDSForcePile(2);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.PrepareDSDestiny(5);
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(leia);
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.LSCardActionAvailable(saber, "Cancel weapon destiny instead of firing"));
        scn.LSUseCardAction(saber, "Cancel weapon destiny instead of firing");
        scn.PassAllResponses();
        assertFalse(leia.isHit());
    }
}
