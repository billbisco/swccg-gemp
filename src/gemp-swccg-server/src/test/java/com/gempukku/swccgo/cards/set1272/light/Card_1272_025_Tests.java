package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hope", "1272_25");
                    put("page", "8_19");
                    put("daughter", "8_8");
                    put("luke", "1_19");
                    put("sentry", "8_11");
                    put("rifle", "8_84");
                    put("backdoor", "8_69");
                    put("wonder", "1109_11");
                }},
                new HashMap<>() {{
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
    public void IHopeShesAllRightVStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hope").getBlueprint();

        assertEquals(Title.I_Hope_Shes_All_Right, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void IHopeShesAllRightVIsImmuneToAlter() {
        var scn = GetScenario();
        var hope = scn.GetLSCard("hope");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(hope);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), hope, Title.Alter));
    }

    @Test
    public void IHopeShesAllRightVScoutsAreDefenseValueAndForfeitPlusTwo() {
        var scn = GetScenario();
        var hope = scn.GetLSCard("hope");
        var page = scn.GetLSCard("page");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        //test1: your scout is defense value +2
        //test2: your scout is forfeit +2
        //test3: non-scouts are not affected
        scn.StartGame();
        scn.MoveCardsToLocation(site, page, luke);
        assertEquals(3, scn.GetDefense(page));
        assertEquals(4, scn.GetForfeit(page));
        scn.MoveCardsToLSSideOfTable(hope);
        assertEquals(5, scn.GetDefense(page)); //test1
        assertEquals(6, scn.GetForfeit(page)); //test2
        assertEquals(4, scn.GetDefense(luke)); //test3
        assertEquals(7, scn.GetForfeit(luke)); //test3
    }

    @Test
    public void IHopeShesAllRightVScoutDefenseValueAndForfeitMayNotBeIncreasedAboveSix() {
        var scn = GetScenario();
        var hope = scn.GetLSCard("hope");
        var daughter = scn.GetLSCard("daughter");
        var site = scn.GetLSStartingLocation();

        //test1: defense value 5 is only increased to 6
        //test2: forfeit 8 is not increased
        scn.StartGame();
        scn.MoveCardsToLocation(site, daughter);
        assertEquals(5, scn.GetDefense(daughter));
        scn.MoveCardsToLSSideOfTable(hope);
        assertEquals(6, scn.GetDefense(daughter)); //test1
        assertEquals(8, scn.GetForfeit(daughter)); //test2
    }

    @Test
    public void IHopeShesAllRightVYourEffectsDeployOnBackDoorForFree() {
        var scn = GetScenario();
        var hope = scn.GetLSCard("hope");
        var backdoor = scn.GetLSCard("backdoor");
        var wonder = scn.GetLSCard("wonder");

        //test1: no Force is used to deploy the Effect on Back Door
        scn.StartGame();
        scn.MoveLocationToTable(backdoor);
        scn.MoveCardsToLSSideOfTable(hope);
        scn.MoveCardsToLSHand(wonder);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSDeployAvailable(wonder));
        scn.LSDeployCard(wonder);
        assertTrue(scn.LSHasCardChoiceAvailable(backdoor));
        scn.LSChooseCard(backdoor);
        scn.PassAllResponses();
        assertEquals(backdoor, wonder.getAttachedTo());
        assertEquals(force, scn.GetLSForcePileCount()); //test1
    }

    @Test
    public void EffectDeployingOnBackDoorCostsForceWithoutIHopeShesAllRightV() {
        var scn = GetScenario();
        var backdoor = scn.GetLSCard("backdoor");
        var wonder = scn.GetLSCard("wonder");

        scn.StartGame();
        scn.MoveLocationToTable(backdoor);
        scn.MoveCardsToLSHand(wonder);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(wonder);
        assertTrue(scn.LSHasCardChoiceAvailable(backdoor));
        scn.LSChooseCard(backdoor);
        scn.PassAllResponses();
        assertEquals(backdoor, wonder.getAttachedTo());
        assertEquals(force - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void IHopeShesAllRightVOncePerTurnMayUseTwoForceToTakeEndorSiteWeaponOrEwokIntoHand() {
        var scn = GetScenario();
        var hope = scn.GetLSCard("hope");
        var sentry = scn.GetLSCard("sentry");
        var rifle = scn.GetLSCard("rifle");
        var backdoor = scn.GetLSCard("backdoor");
        var luke = scn.GetLSCard("luke");

        //test1: an [Endor] battleground site, an [Endor] weapon, or a card with 'Ewok' in lore may be chosen
        //test2: other cards may not be chosen
        //test3: costs 2 Force
        //test4: once per turn
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(hope);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        scn.MoveCardsToTopOfLSReserveDeck(sentry, rifle, backdoor, luke);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(hope, "Take card"));
        scn.LSUseCardAction(hope, "Take card");
        scn.PassAllResponses();
        assertTrue(lsReserveSearchSelectable(scn, sentry)); //test1
        assertTrue(lsReserveSearchSelectable(scn, rifle)); //test1
        assertTrue(lsReserveSearchSelectable(scn, backdoor)); //test1
        assertFalse(lsReserveSearchSelectable(scn, luke)); //test2
        scn.ChooseCardBPFromSelection(scn.LS, sentry);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, sentry); //test1
        assertEquals(force - 2, scn.GetLSForcePileCount()); //test3
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(hope, "Take card")); //test4
    }

    private boolean lsReserveSearchSelectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        String[] blueprintIds = scn.LSGetADParam("blueprintId");
        String[] selectable = scn.LSGetADParam("selectable");
        if (blueprintIds == null || selectable == null)
            return false;
        String id = card.getBlueprintId(true);
        for (int i = 0; i < blueprintIds.length; i++) {
            if (id.equals(blueprintIds[i]) && "true".equals(selectable[i]))
                return true;
        }
        return false;
    }
}
