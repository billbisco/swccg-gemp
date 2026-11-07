package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_009_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("han", "1_11"); put("demotion", "1_47"); put("cantina", "1_128"); }},
                new HashMap<>() {{ put("jir", "1261_9"); put("yularen", "1_166"); put("vader", "1_168");
                    put("trooper", "1_194"); put("tarkin", "1_179"); put("droid", "1_163"); put("mobile", "1_286"); }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void CommanderDaineJirPrintedStatsIconsAndKeywordsMatchSource() {
        var scn = scenario(); var b = scn.GetDSCard("jir").getBlueprint();
        assertEquals("Commander Daine Jir", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(2, b.getDestiny(), scn.epsilon); assertEquals(3, b.getDeployCost(), scn.epsilon);
        assertEquals(3, b.getPower(), scn.epsilon); assertEquals(3, b.getAbility(), scn.epsilon); assertEquals(5, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.IMPERIAL); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>() {{ add(Keyword.LEADER); add(Keyword.COMMANDER); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.IMPERIAL); add(Icon.WARRIOR); add(Icon.A_NEW_HOPE); add(Icon.LEGACY_BLOCK_6); }});
    }

    private VirtualTableScenario mobile() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("mobile"));
        scn.MoveCardsToLocation(scn.GetDSCard("mobile"), scn.GetDSCard("jir"), scn.GetDSCard("yularen"), scn.GetLSCard("han")); return scn;
    }

    @Test
    public void CommanderDaineJirDemotionImmunityRequiresMobileSiteAndOwnImperialsHere() {
        var scn = mobile(); var jir = scn.GetDSCard("jir"); var yularen = scn.GetDSCard("yularen"); var q = scn.game().getModifiersQuerying();
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), jir, Title.Demotion));
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), yularen, Title.Demotion));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), scn.GetLSCard("han"), Title.Demotion));
        var enemy = com.gempukku.swccgo.cards.legacy.MissionTestSupport.putInHand(scn, 1261091, scn.LS,
                new com.gempukku.swccgo.cards.AbstractImperial(Side.LIGHT, 1, 1, 1, 1, 2, "Enemy Imperial", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1) {});
        scn.MoveCardsToLocation(scn.GetDSCard("mobile"), enemy, scn.GetDSCard("droid"));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), enemy, Title.Demotion));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("droid"), Title.Demotion));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), yularen); assertFalse(q.isImmuneToCardTitle(scn.gameState(), yularen, Title.Demotion));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), jir); assertFalse(q.isImmuneToCardTitle(scn.gameState(), yularen, Title.Demotion));
    }

    @Test
    public void CommanderDaineJirPreventsActualDemotionDeploymentAgainstProtectedImperials() {
        var scn = mobile(); scn.MoveCardsToLSHand(scn.GetLSCard("demotion")); scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("demotion")));
    }

    @Test
    public void CommanderDaineJirElsewhereAllowsActualDemotionOnUnprotectedImperial() {
        var scn = mobile(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("jir"));
        var yularen = scn.GetDSCard("yularen"); var demotion = scn.GetLSCard("demotion");
        scn.MoveCardsToLSHand(demotion); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(demotion); scn.LSChooseCard(yularen); scn.PassAllResponses();
        assertSame(yularen, demotion.getAttachedTo()); assertEquals(0, scn.GetPower(yularen));
    }

    private boolean selectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        var bp = scn.GetADParam(scn.DS, "blueprintId"); var ids = scn.GetADParam(scn.DS, "cardId"); var allowed = scn.GetADParam(scn.DS, "selectable");
        int n = bp != null ? bp.length : ids.length;
        for (int i = 0; i < n; i++) {
            boolean match = bp != null ? bp[i].equals(card.getBlueprintId(true)) : ids[i].equals(String.valueOf(card.getCardId()));
            if (match) return allowed == null || Boolean.parseBoolean(allowed[i]);
        }
        return false;
    }

    @Test
    public void CommanderDaineJirActuallyDownloadsOnlyUniqueNonPilotImperialWarriorForSearchPlusDeployCost() {
        var scn = mobile(); var jir = scn.GetDSCard("jir"); var yularen = scn.GetDSCard("yularen");
        scn.SkipToDSTurn(Phase.DEPLOY); scn.EnsureDSForcePile(10);
        scn.MoveCardsToTopOfDSReserveDeck(yularen, scn.GetDSCard("trooper"), scn.GetDSCard("vader"), scn.GetDSCard("tarkin"));
        int force = scn.GetDSForcePileCount(); int messages = scn.gameState().getLastMessages().size();
        scn.DSUseCardAction(jir, "Deploy unique Imperial warrior"); scn.PassAllResponses();
        assertTrue(selectable(scn, yularen)); assertFalse(selectable(scn, scn.GetDSCard("trooper")));
        assertFalse(selectable(scn, scn.GetDSCard("vader"))); assertFalse(selectable(scn, scn.GetDSCard("tarkin")));
        scn.DSChooseCard(yularen); scn.PassAllResponses();
        // The printed download is not restricted to Jir's site.
        scn.DSChooseCard(scn.GetDSStartingLocation()); scn.PassAllResponses();
        assertEquals(scn.GetDSStartingLocation(), yularen.getAtLocation()); assertEquals(force - 3, scn.GetDSForcePileCount());
        assertTrue(scn.gameState().getLastMessages().subList(messages, scn.gameState().getLastMessages().size()).stream()
                .anyMatch(x -> x.contains("shuffles") && x.contains("Reserve Deck")));
        if (scn.AwaitingLSDeployPhaseActions()) scn.LSPass(); assertFalse(scn.DSCardActionAvailable(jir, "Deploy unique Imperial warrior"));
    }

    @Test
    public void CommanderDaineJirDownloadRequiresMobileSite() {
        var scn = mobile(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("jir"));
        scn.SkipToDSTurn(Phase.DEPLOY); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("jir"), "Deploy unique Imperial warrior"));
    }

    @Test
    public void CommanderDaineJirDownloadRequiresOneForceInFreshActionWindow() {
        var scn = mobile(); scn.SkipToDSTurn(Phase.CONTROL); scn.DrainDSForcePileTo(0); scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(0, scn.GetDSForcePileCount()); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("jir"), "Deploy unique Imperial warrior"));
    }

    @Test
    public void CommanderDaineJirPreventsActualNoOneFlipUntilHeLeavesTableEvenAwayFromMobileSite() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetLSStartingLocation(); var jir = scn.GetDSCard("jir");
        scn.MoveLocationToTable(scn.GetLSCard("cantina")); scn.MoveCardsToLocation(scn.GetLSCard("cantina"), scn.GetLSCard("han"));
        scn.MoveCardsToLocation(site, jir); scn.MoveCardsToDSHand(scn.GetDSCard("trooper"), scn.GetDSCard("yularen"));
        var objective = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.DS, VirtualTableScenario._cardLibrary, "1210_68");
        scn.gameState().addCardToZone(objective, Zone.HAND, scn.DS, true, false, false);
        scn.MoveCardsToDSSideOfTable(objective); objective.setObjectiveDeploymentComplete(true); scn.gameState().flipCard(scn.game(), objective, true);
        assertFalse(GameConditions.canBeFlipped(scn.game(), objective)); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(scn.GetDSCard("trooper")); scn.DSChooseCard(site); scn.PassAllResponses(); assertTrue(objective.isFlipped());
        scn.MoveCardsToDSHand(jir); assertTrue(GameConditions.canBeFlipped(scn.game(), objective));
        if (scn.AwaitingLSDeployPhaseActions()) scn.LSPass();
        scn.DSDeployCard(scn.GetDSCard("yularen")); scn.DSChooseCard(site); scn.PassAllResponses(); assertFalse(objective.isFlipped());
    }
}
