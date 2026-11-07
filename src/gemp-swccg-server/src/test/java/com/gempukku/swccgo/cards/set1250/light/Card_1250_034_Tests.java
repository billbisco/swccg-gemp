package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

public class Card_1250_034_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("bail", "1250_34"); put("leia", "1_17"); put("luke", "1_19"); put("trooper", "1_28");
            put("panaka", "12_2"); put("valorum", "12_30"); put("senator", "12_28"); put("political", "12_40");
            put("senate", "12_75"); put("platform", "1113_3"); put("cantina", "1_128");
            put("retrieve1", "1_28"); put("retrieve2", "1_28");
        }}, new HashMap<>() {{
            put("vader", "1_168"); put("senator", "12_110"); put("political", "12_127");
        }}, 25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void BailOrganaMetadataMatchesPrinted35Image() {
        var scn = scenario(); var bp = scn.GetLSCard("bail").getBlueprint();
        assertEquals("Bail Organa", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon); assertEquals(2, bp.getDeployCost(), scn.epsilon);
        assertEquals(3, bp.getPower(), scn.epsilon); assertEquals(3, bp.getAbility(), scn.epsilon);
        assertEquals(2, bp.getPolitics(), scn.epsilon); assertEquals(5, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.REBEL); add(CardType.REPUBLIC); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{
            add(Icon.REBEL); add(Icon.REPUBLIC); add(Icon.SPECIAL_EDITION); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5);
        }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.LEADER); add(Keyword.SENATOR); }});
        scn.BlueprintPersonaCheck(bp, new ArrayList<>() {{ add(Persona.BAIL); }});
    }

    @Test
    public void BailOrganaHasExactlyRebellionAgenda() {
        var scn = scenario(); scn.StartGame(); var bail = scn.GetLSCard("bail");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), bail);
        assertEquals(List.of(Agenda.REBELLION), scn.game().getModifiersQuerying().getAgendas(scn.gameState(), bail));
    }

    @Test
    public void BailOrganaAddsOneActualBattleDestinyWithLeia() { checkBattle(true, false, 7); }
    @Test
    public void BailOrganaDoesNotAddBattleDestinyWhenLeiaAtDifferentSite() { checkBattle(false, false, 0); }
    @Test
    public void BailOrganaDoesNotAddDestinyWithAnotherRebelInsteadOfLeia() { checkBattle(false, true, 3); }

    private void checkBattle(boolean withLeia, boolean otherRebel, int total) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("bail"), scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(withLeia ? site : scn.GetDSStartingLocation(), scn.GetLSCard("leia"));
        if (otherRebel) scn.MoveCardsToLocation(site, scn.GetLSCard("luke"));
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(3); if (withLeia) scn.PrepareLSDestiny(4); scn.PrepareDSDestiny(1);
        scn.SkipToPowerSegment();
        int draws = withLeia ? 2 : otherRebel ? 1 : 0;
        assertEquals(draws, scn.GetLSBattleDestinyCount());
        if (draws > 0) {
            assertTrue(scn.LSDecisionAvailable("battle destiny?"));
            scn.LSChooseYes();
            for (int i = 0; i < draws; i++) scn.PassDestinyDrawResponses();
        }
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertEquals(total, scn.GetLSTotalDestiny());
    }

    @Test
    public void BailOrganaDeploymentLosesOnlyYourCoruscantSenatorsAndPoliticalEffects() {
        var scn = scenario(); scn.StartGame(); var bail = scn.GetLSCard("bail");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("senator"), scn.GetLSCard("leia"), scn.GetLSCard("panaka"), scn.GetLSCard("valorum"), scn.GetDSCard("senator"));
        assertEquals(5, scn.GetLSCard("valorum").getBlueprint().getPolitics(), scn.epsilon);
        assertFalse(scn.GetLSCard("valorum").getBlueprint().hasKeyword(Keyword.SENATOR));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("political"));
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("political"));
        scn.MoveCardsToHand(bail); scn.LSActivateForceCheat(6); scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount(); scn.LSDeployCard(bail); scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose card to be lost")) { scn.LSChooseCard(scn.GetLSCard("senator")); scn.PassAllResponses(); }
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("senator")));
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("political")));
        assertEquals(force - 2, scn.GetLSForcePileCount());
        assertEquals(scn.GetLSStartingLocation(), scn.GetLSCard("leia").getAtLocation());
        assertEquals(scn.GetLSStartingLocation(), scn.GetLSCard("panaka").getAtLocation());
        assertEquals(scn.GetLSStartingLocation(), scn.GetLSCard("valorum").getAtLocation());
        assertEquals(scn.GetLSStartingLocation(), scn.GetDSCard("senator").getAtLocation());
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("political").getZone());
    }

    @Test
    public void BailOrganaAlsoLosesCoruscantSenatorDeployedAfterHim() {
        var scn = scenario(); scn.StartGame(); var senator = scn.GetLSCard("senator");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("bail")); scn.MoveCardsToHand(senator);
        scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(senator); scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(senator));
    }

    @Test
    public void BailOrganaSenateBoostRespectsUniqueRebelAbilityBoundaryAndExpiresWhenLeaving() {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); scn.MoveLocationToTable(senate);
        var bail = scn.GetLSCard("bail"); var leia = scn.GetLSCard("leia"); var luke = scn.GetLSCard("luke");
        var trooper = scn.GetLSCard("trooper"); var panaka = scn.GetLSCard("panaka");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), leia, luke, trooper, panaka);
        assertEquals(3, scn.GetPower(leia)); assertEquals(4, luke.getBlueprint().getAbility(), scn.epsilon);
        scn.MoveCardsToLocation(senate, bail);
        assertEquals(5, scn.GetPower(leia)); assertEquals(3, scn.GetPower(luke));
        assertEquals(1, scn.GetPower(trooper)); assertEquals(4, scn.GetPower(panaka));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), bail); assertEquals(3, scn.GetPower(leia));
    }

    @Test
    public void BailOrganaSenateLandingPlatformDoesNotCountAsSenate() {
        var scn = scenario(); scn.StartGame(); var platform = scn.GetLSCard("platform"); scn.MoveLocationToTable(platform);
        scn.MoveCardsToLocation(platform, scn.GetLSCard("bail")); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leia"));
        assertEquals(3, scn.GetPower(scn.GetLSCard("leia")));
    }

    @Test
    public void BailOrganaAtSenateRetrievesAfterOpponentActuallyLosesForceToRebelDrain() { checkRetrieval(true, true, true, false); }
    @Test
    public void BailOrganaMayDeclineRetrievalAfterRebelDrain() { checkRetrieval(true, true, false, false); }
    @Test
    public void BailOrganaDoesNotRetrieveAfterDrainByNonRebelRepublicCharacter() { checkRetrieval(true, false, false, false); }
    @Test
    public void BailOrganaAwayFromSenateDoesNotRetrieve() { checkRetrieval(false, true, false, false); }
    @Test
    public void BailOrganaDoesNotOfferRetrievalWithEmptyLostPile() { checkRetrieval(true, true, false, true); }

    private boolean retrievalOffered(VirtualTableScenario scn) {
        return scn.GetADParam(scn.LS, "cardId") != null && scn.GetADParam(scn.LS, "actionText") != null
                && scn.LSCardActionAvailable(scn.GetLSCard("bail"), "Retrieve 1 Force");
    }

    private void checkRetrieval(boolean atSenate, boolean rebel, boolean accept, boolean emptyLost) {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(atSenate ? senate : scn.GetLSStartingLocation(), scn.GetLSCard("bail"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard(rebel ? "trooper" : "panaka"));
        scn.SkipToLSTurn(Phase.CONTROL);
        if (!emptyLost) scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("retrieve1"));
        var target = scn.GetLSCard("retrieve1"); int life = scn.GetLSLifeForceRemaining();
        scn.LSForceDrainAt(scn.GetDSStartingLocation()); scn.DSPass(); scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal()); assertFalse(retrievalOffered(scn));
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        assertEquals(atSenate && rebel && !emptyLost, retrievalOffered(scn));
        if (accept) scn.LSUseCardAction(scn.GetLSCard("bail"), "Retrieve 1 Force");
        scn.PassAllResponses();
        assertEquals(life + (accept ? 1 : 0), scn.GetLSLifeForceRemaining());
        assertEquals(accept, scn.GetLSUsedPile().contains(target));
        if (!emptyLost) assertEquals(!accept, scn.GetLSLostPile().contains(target));
    }
    @Test
    public void BailOrganaMayRespondToEachUnitActuallyLostToTwoForceRebelDrain() {
        var scn = scenario(); scn.StartGame(); var senate = scn.GetLSCard("senate"); var cantina = scn.GetLSCard("cantina");
        scn.MoveLocationToTable(senate); scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLocation(senate, scn.GetLSCard("bail"));
        scn.MoveCardsToLocation(cantina, scn.GetLSCard("trooper")); scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("retrieve1"), scn.GetLSCard("retrieve2"));
        int life = scn.GetLSLifeForceRemaining();
        scn.LSForceDrainAt(cantina); scn.DSPass(); scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
        for (int unit = 0; unit < 2; unit++) {
            scn.DSChooseCard(scn.GetTopOfDSForcePile());
            assertTrue(retrievalOffered(scn));
            scn.LSUseCardAction(scn.GetLSCard("bail"), "Retrieve 1 Force"); scn.PassAllResponses();
            assertEquals(life + unit + 1, scn.GetLSLifeForceRemaining());
        }
        assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("retrieve1")));
        assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("retrieve2")));
        assertFalse(scn.IsActiveForceDrain());
    }

}
