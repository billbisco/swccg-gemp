package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1271_034_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("leia", "1271_34"); put("camp", "1_131"); put("saber", "3_71");
            put("jediSaber", "1_155"); put("corvette", "1_140"); put("system", "6_87");
        }}, new HashMap<>() {{ put("vader", "1_168"); put("saber", "1_324"); }}, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void PrincessLeiaLostScionStatsAndKeywordsAreCorrect() {
        var scn = GetScenario(); var physical = scn.GetLSCard("leia"); var card = physical.getBlueprint();
        assertEquals("Princess Leia, Lost Scion", card.getTitle()); assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide()); assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(1, card.getDestiny(), scn.epsilon); assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon); assertEquals(5, card.getAbility(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon); assertEquals(1, scn.GetLandspeed(physical));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.REBEL); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.REBEL); add(Icon.PILOT); add(Icon.WARRIOR); add(Icon.CLOUD_CITY); add(Icon.LEGACY_BLOCK_7); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.FEMALE); }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{ add(Persona.LEIA); }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, card.getExpansionSet()); assertEquals(Rarity.V, card.getRarity());
        assertTrue(Filters.Skywalker.accepts(scn.game(), physical));
    }

    @Test
    public void PrincessLeiaLostScionFlipsTheirFireWhenDeployedPresentAtBattlegroundAndLocksFront() {
        var scn = GetScenario(); var camp = scn.GetLSCard("camp"); var leia = scn.GetLSCard("leia");
        scn.StartGame(); scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        var hunt = PutTheirFireOnTable(scn); scn.MoveCardsToLSHand(leia);
        scn.EnsureLSForcePile(10); scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle());
        scn.LSDeployCard(leia); scn.LSChooseCard(camp); scn.PassAllResponses();
        assertEquals(Title.Hunt_Down_And_Destroy_The_Jedi, hunt.getBlueprint().getTitle());
        assertFalse(GameConditions.canBeFlipped(scn.game(), hunt));
        scn.MoveCardsToLSHand(leia); scn.PassAllResponses();
        assertTrue(GameConditions.canBeFlipped(scn.game(), hunt));
    }

    @Test
    public void PrincessLeiaLostScionDoesNotFlipTheirFireWhileAboardShipAtBattleground() {
        var scn = GetScenario(); var leia = scn.GetLSCard("leia"); var ship = scn.GetLSCard("corvette");
        var system = scn.GetLSCard("system");
        scn.StartGame(); scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(system, ship); scn.BoardAsPilot(ship, leia);
        assertTrue(Filters.battleground.accepts(scn.game(), system));
        assertFalse(GameConditions.isPresentAt(scn.game(), leia, Filters.battleground));
        var hunt = PutTheirFireOnTable(scn); scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(Title.Their_Fire_Has_Gone_Out_Of_The_Universe, hunt.getBlueprint().getTitle());
    }

    @Test
    public void PrincessLeiaLostScionPreservesForfeitWhenHitWhileArmedWithAnakinsLightsaber() {
        var scn = HitLeiaWithVadersLightsaber("saber");
        assertTrue(scn.GetLSCard("leia").isHit());
        assertEquals(8, scn.GetForfeit(scn.GetLSCard("leia")));
    }

    @Test
    public void PrincessLeiaLostScionDoesNotPreserveForfeitWithoutAnakinsLightsaber() {
        var scn = HitLeiaWithVadersLightsaber(null);
        assertTrue(scn.GetLSCard("leia").isHit()); assertEquals(0, scn.GetForfeit(scn.GetLSCard("leia")));
    }

    @Test
    public void PrincessLeiaLostScionDoesNotPreserveForfeitWithADifferentLightsaber() {
        var scn = HitLeiaWithVadersLightsaber("jediSaber");
        assertTrue(scn.GetLSCard("leia").isHit()); assertEquals(0, scn.GetForfeit(scn.GetLSCard("leia")));
    }

    @Test
    public void PrincessLeiaLostScionHasImmunityToAttritionLessThanFour() {
        var scn = GetScenario(); var leia = scn.GetLSCard("leia");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), leia);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), leia), scn.epsilon);
    }

    @Test
    public void PrincessLeiaLostScionCanDeployAnakinsLightsaberAndProtectionEndsWhenItLeaves() {
        var scn = GetScenario(); var leia = scn.GetLSCard("leia"); var saber = scn.GetLSCard("saber");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), leia);
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.ForfeitModifier(leia, leia, -3));
        assertEquals(5, scn.GetForfeit(leia));
        scn.MoveCardsToLSHand(saber); scn.EnsureLSForcePile(5); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(saber); scn.LSChooseCard(leia); scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(leia, saber)); assertEquals(8, scn.GetForfeit(leia));
        scn.MoveCardsToLSHand(saber);
        assertEquals(5, scn.GetForfeit(leia));
    }

    private PhysicalCardImpl PutTheirFireOnTable(VirtualTableScenario scn) {
        var objective = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.DS, VirtualTableScenario._cardLibrary, "7_297");
        scn.gameState().addCardToZone(objective, Zone.SIDE_OF_TABLE, scn.DS, true, false, false);
        scn.gameState().flipCard(scn.game(), objective, true);
        return objective;
    }

    private VirtualTableScenario HitLeiaWithVadersLightsaber(String weaponName) {
        var scn = GetScenario(); var leia = scn.GetLSCard("leia"); var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber"); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, leia, vader); scn.AttachCardsTo(vader, saber);
        if (weaponName != null) scn.AttachCardsTo(leia, scn.GetLSCard(weaponName));
        scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureDSForcePile(5); scn.DSInitiateBattle(site); scn.PassAllResponses();
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(6), scn.GetDSDestiny(5));
        scn.DSUseCardAction(saber, "Fire"); scn.DSChooseCard(leia);
        scn.PassWeaponFireWithDestinyDraw(2); scn.PassAllResponses();
        return scn;
    }
}
