package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_1271_035_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("r2", "1271_35"); put("luke", "1_19"); put("han", "1_11"); put("obi", "1_21");
            put("hothRoom", "3_57"); put("planetBay", "1_129"); put("noScomp", "1_131"); put("corvette", "1_140"); put("system", "6_87");
        }}, new HashMap<>() {{
            put("vader", "1_168"); put("greedo", "2_89"); put("garindan", "1_177"); put("saber", "1_324");
            put("bay", "1_285"); put("room", "1_287"); put("corridor", "1_286"); put("deathstar", "2_143");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void R2D2SubversiveDroidStatsAndKeywordsAreCorrect() {
        var scn = GetScenario(); var physical = scn.GetLSCard("r2"); var card = physical.getBlueprint();
        assertEquals("R2-D2, Subversive Droid", card.getTitle()); assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide()); assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(2, card.getDestiny(), scn.epsilon); assertEquals(5, card.getAlternateDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon); assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon); assertEquals(1, scn.GetLandspeed(physical));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.DROID); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.DROID); add(Icon.A_NEW_HOPE); add(Icon.LEGACY_BLOCK_7); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.SPY); }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{ add(Persona.R2D2); }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{ add(ModelType.ASTROMECH); }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, card.getExpansionSet()); assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void R2D2SubversiveDroidIsCapturedWhenAnImperialTakesControlOfHisSite() {
        var scn = DeployOpponentAtR2Site("vader", false); var r2 = scn.GetLSCard("r2"); var vader = scn.GetDSCard("vader");
        for (int i = 0; i < 30 && !r2.isCaptive(); i++) {
            if (scn.GetDecidingPlayer().equals(scn.DS) && scn.DSCaptureDecisionAvailable()) scn.DSChooseSeizeCaptive();
            else if (scn.GetDecidingPlayer().equals(scn.DS) && scn.DSHasCardChoiceAvailable(vader)) scn.DSChooseCard(vader);
            else if (scn.GetDecidingPlayer().equals(scn.DS) && scn.DSHasCardChoiceAvailable(r2)) scn.DSChooseCard(r2);
            else scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(r2.isCaptive()); assertEquals(vader, r2.getAttachedTo());
    }

    @Test
    public void R2D2SubversiveDroidIsNotCapturedWhenAnAlienControlsHisSite() {
        var scn = DeployOpponentAtR2Site("greedo", false);
        assertTrue(Filters.controls(scn.DS).accepts(scn.game(), scn.GetDSStartingLocation()));
        assertFalse(scn.GetLSCard("r2").isCaptive());
        assertTrue(scn.AwaitingLSDeployPhaseActions() || scn.AwaitingDSDeployPhaseActions());
    }

    @Test
    public void R2D2SubversiveDroidIsNotCapturedByImperialAtAContestedSite() {
        var scn = DeployOpponentAtR2Site("vader", true);
        assertFalse(Filters.controls(scn.DS).accepts(scn.game(), scn.GetDSStartingLocation()));
        assertFalse(scn.GetLSCard("r2").isCaptive());
        assertTrue(scn.AwaitingLSDeployPhaseActions() || scn.AwaitingDSDeployPhaseActions());
    }

    private VirtualTableScenario DeployOpponentAtR2Site(String name, boolean contested) {
        var scn = GetScenario(); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, scn.GetLSCard("r2"));
        if (contested) scn.MoveCardsToLocation(site, scn.GetLSCard("luke"));
        var opponent = scn.GetDSCard(name); scn.MoveCardsToDSHand(opponent);
        scn.EnsureDSForcePile(10); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(opponent); scn.DSChooseCard(site); scn.PassAllResponses();
        return scn;
    }

    @Test
    public void R2D2SubversiveDroidAddsPowerOnlyForHisPlayerAtAdjacentSites() {
        var scn = GetScenario(); var bay = scn.GetDSCard("bay"); var room = scn.GetDSCard("room"); var corridor = scn.GetDSCard("corridor");
        scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("deathstar"));
        scn.MoveLocationToTable(bay); scn.MoveLocationToTable(room); scn.MoveLocationToTable(corridor);
        scn.MoveCardsToLocation(bay, scn.GetLSCard("r2"));
        var adjacent = List.of(room, corridor).stream().filter(site -> scn.IsAdjacentTo(bay, site)).findFirst().orElseThrow();
        var remote = adjacent == room ? corridor : room;
        assertFalse(scn.IsAdjacentTo(bay, remote));
        scn.MoveCardsToLocation(adjacent, scn.GetLSCard("han"), scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(remote, scn.GetLSCard("obi"));
        assertEquals(5, Power(scn, adjacent, scn.LS), scn.epsilon);
        assertEquals(6, Power(scn, adjacent, scn.DS), scn.epsilon);
        assertEquals(5, Power(scn, remote, scn.LS), scn.epsilon);
        assertEquals(1, Power(scn, bay, scn.LS), scn.epsilon);
        scn.MoveCardsToLSHand(scn.GetLSCard("r2"));
        assertEquals(3, Power(scn, adjacent, scn.LS), scn.epsilon);
    }

    @Test
    public void R2D2SubversiveDroidAddsOneToOpponentsDestinyForOneForce() {
        ModifyBattleDestiny(true);
    }

    @Test
    public void R2D2SubversiveDroidSubtractsOneFromOpponentsDestinyForOneForce() {
        ModifyBattleDestiny(false);
    }

    private void ModifyBattleDestiny(boolean add) {
        var scn = BattleDestinyWithR2At("bay", 2); var r2 = scn.GetLSCard("r2");
        int before = scn.GetLSForcePileCount();
        String action = add ? "Add 1 to opponent's destiny" : "Subtract 1 from opponent's destiny";
        assertTrue(scn.LSCardActionAvailable(r2, action)); scn.LSUseCardAction(r2, action);
        scn.PassForceUseResponses(); scn.PassAllResponses(); scn.PassDestinyDrawResponses(); scn.PassResponses("Just drew"); scn.PassAllResponses();
        if (scn.LSDecisionAvailable("battle destiny?")) { scn.LSChooseNo(); scn.PassAllResponses(); }
        assertEquals(before - 1, scn.GetLSForcePileCount());
        // Vader adds 1 to the prepared destiny 6 before R2 modifies it.
        assertEquals(add ? 8 : 6, scn.GetDSTotalDestiny());
    }

    @Test
    public void R2D2SubversiveDroidCanModifyDestinyAtMobileNonBattlegroundSite() {
        var scn = BattleDestinyWithR2At("room", 2);
        assertFalse(Filters.battleground.accepts(scn.game(), scn.GetDSCard("room")));
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("r2"), "Add 1 to opponent's destiny"));
    }

    @Test
    public void R2D2SubversiveDroidCanModifyDestinyAtPlanetaryBattlegroundSite() {
        var scn = BattleDestinyWithR2At("planetBay", 2); var site = scn.GetLSCard("planetBay");
        assertTrue(Filters.battleground.accepts(scn.game(), site)); assertFalse(Filters.mobile_site.accepts(scn.game(), site));
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("r2"), "Add 1 to opponent's destiny"));
    }

    @Test
    public void R2D2SubversiveDroidCannotModifyDestinyWithoutForce() {
        AssertNoDestinyAction(BattleDestinyWithR2At("bay", 0));
    }

    @Test
    public void R2D2SubversiveDroidCannotModifyDestinyWithoutScompLink() {
        AssertNoDestinyAction(BattleDestinyWithR2At("noScomp", 2));
    }

    @Test
    public void R2D2SubversiveDroidCannotModifyDestinyAtNonMobileNonBattlegroundSite() {
        AssertNoDestinyAction(BattleDestinyWithR2At("hothRoom", 2));
    }

    private VirtualTableScenario BattleDestinyWithR2At(String siteName, int force) {
        return BattleDestinyWithR2At(siteName, force, false);
    }

    private VirtualTableScenario BattleDestinyWithR2At(String siteName, int force, boolean protectedDraw) {
        var scn = GetScenario(); scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("deathstar")); scn.MoveLocationToTable(scn.GetDSCard("bay")); scn.MoveLocationToTable(scn.GetDSCard("room"));
        var sourceSite = siteName.equals("noScomp") || siteName.equals("hothRoom") || siteName.equals("planetBay") ? scn.GetLSCard(siteName) : scn.GetDSCard(siteName);
        if (siteName.equals("hothRoom") || siteName.equals("noScomp") || siteName.equals("planetBay")) scn.MoveLocationToTable(sourceSite);
        scn.MoveCardsToLocation(sourceSite, scn.GetLSCard("r2"));
        var site = scn.GetDSStartingLocation(); scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.EnsureDSForcePile(5); scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureLSForcePile(force); scn.DrainLSForcePileTo(force);
        if (protectedDraw) scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.MayNotModifyBattleDestinyModifier(scn.GetDSCard("vader"), scn.DS, scn.LS));
        scn.DSInitiateBattle(site); scn.PassAllResponses(); scn.PrepareDSDestiny(6); scn.SkipToPowerSegment(); scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("battle destiny?")); scn.DSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD"); scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        if (scn.GetDecidingPlayer().equals(scn.DS)) scn.DSPass();
        assertEquals(scn.LS, scn.GetDecidingPlayer());
        return scn;
    }

    @Test
    public void R2D2SubversiveDroidCannotModifyOwnDestinyAfterDecliningOpponentsDraw() {
        var scn = BattleDestinyWithR2At("bay", 2);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("r2"), "Add 1 to opponent's destiny"));
        scn.PassAllResponses(); scn.PassDestinyDrawResponses(); scn.PassResponses("Just drew"); scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("battle destiny?")); scn.PrepareLSDestiny(5); scn.LSChooseYes();
        StopAtLightDestinyResponse(scn);
        AssertNoDestinyAction(scn);
    }

    @Test
    public void R2D2SubversiveDroidCannotModifyProtectedDestiny() {
        var control = BattleDestinyWithR2At("bay", 2);
        assertTrue(control.LSCardActionAvailable(control.GetLSCard("r2"), "Add 1 to opponent's destiny"));
        var scn = BattleDestinyWithR2At("bay", 2, true);
        assertTrue(scn.game().getModifiersQuerying().mayNotModifyDestinyDraw(scn.gameState(), scn.LS));
        AssertNoDestinyAction(scn);
    }

    @Test
    public void R2D2SubversiveDroidCannotModifySecondDestinyInSameTurn() {
        var scn = GetScenario(); var r2 = scn.GetLSCard("r2"); var saber = scn.GetDSCard("saber");
        scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("deathstar")); scn.MoveLocationToTable(scn.GetDSCard("bay"));
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), r2);
        var site = scn.GetDSStartingLocation(); scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.AttachCardsTo(scn.GetDSCard("vader"), saber);
        scn.EnsureDSForcePile(5); scn.SkipToDSTurn(Phase.BATTLE); scn.EnsureLSForcePile(2); scn.DrainLSForcePileTo(2);
        scn.DSInitiateBattle(site); scn.PassAllResponses();
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(6), scn.GetDSDestiny(5));
        scn.DSUseCardAction(saber, "Fire"); scn.DSChooseCard(scn.GetLSCard("luke")); scn.PassResponses("Fire ");
        StopAtLightDestinyResponse(scn);
        var firstCard = scn.gameState().getTopDrawDestinyState().getDrawDestinyEffect().getDrawnDestinyCard();
        assertTrue(scn.LSCardActionAvailable(r2, "Add 1 to opponent's destiny")); scn.LSUseCardAction(r2, "Add 1 to opponent's destiny");
        boolean secondDrawSeen = false;
        for (int n = 0; n < 100; n++) {
            var draw = scn.gameState().getTopDrawDestinyState();
            if (draw != null && draw.getDrawDestinyEffect().getDrawnDestinyCard() != null
                    && draw.getDrawDestinyEffect().getDrawnDestinyCard() != firstCard
                    && scn.GetDecidingPlayer().equals(scn.LS)) {
                AssertNoDestinyAction(scn); secondDrawSeen = true; break;
            }
            assertTrue(scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(secondDrawSeen); assertEquals(1, scn.GetLSForcePileCount());
    }

    private void StopAtLightDestinyResponse(VirtualTableScenario scn) {
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD"); scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        if (scn.GetDecidingPlayer().equals(scn.DS)) scn.DSPass();
        assertEquals(scn.LS, scn.GetDecidingPlayer());
    }

    private void AssertNoDestinyAction(VirtualTableScenario scn) {
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("r2"), "Add 1 to opponent's destiny"));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("r2"), "Subtract 1 from opponent's destiny"));
    }

    private float Power(VirtualTableScenario scn, PhysicalCardImpl site, String player) {
        return scn.game().getModifiersQuerying().getTotalPowerAtLocation(scn.gameState(), site, player, false, false);
    }
}
