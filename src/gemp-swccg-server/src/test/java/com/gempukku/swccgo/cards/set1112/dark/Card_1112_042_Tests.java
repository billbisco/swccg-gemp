package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_042_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("try", "1112_42");
                    put("room", "1240_51");
                    put("emperor", "9_109");
                    put("evax", "1240_12");
                }},
                10,
                10,
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
    public void ThereIsNoTryStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("try").getBlueprint();

        assertEquals("There Is No Try & Oppressive Enforcement", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.REFLECTIONS_II);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.getGameText().startsWith("Deploy on Conference Room."));
        assertTrue(card.getGameText().contains("(Immune to Alter.)"));
    }

    @Test
    public void ThereIsNoTryMakesEmperorACouncilMemberWithInfluencePlusOne() {
        var scn = GetScenario();
        var effect = scn.GetDSCard("try");
        var room = scn.GetDSCard("room");
        var emperor = scn.GetDSCard("emperor");
        var evax = scn.GetDSCard("evax");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.AttachCardsTo(room, effect);
        scn.MoveCardsToLocation(room, emperor, evax);

        assertTrue(scn.HasKeyword(emperor, Keyword.IMPERIAL_COUNCIL_MEMBER));
        assertTrue(scn.HasKeyword(evax, Keyword.IMPERIAL_COUNCIL_MEMBER));
        assertEquals(1, scn.game().getModifiersQuerying().getInfluence(scn.gameState(), emperor), scn.epsilon);
        assertEquals(3, scn.GetIconCount(room, Icon.DARK_FORCE));
    }

    @Test
    public void ThereIsNoTryDeploysOnlyOnConferenceRoom() {
        var scn = GetPlayScenario();
        var effect = scn.GetDSCard("try");
        var room = scn.GetDSCard("room");
        var market = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveCardsToDSHand(effect);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(effect));
        scn.DSPlayCard(effect);
        assertTrue(scn.DSHasCardChoiceAvailable(room));
        assertFalse(scn.DSHasCardChoiceAvailable(market));
        scn.DSChooseCard(room);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(room, effect));
    }

    @Test
    public void ThereIsNoTryCancelsRevolutionBeingPlayed() {
        var scn = GetPlayScenario();
        var effect = scn.GetDSCard("try");
        var room = scn.GetDSCard("room");
        var revolution = scn.GetLSCard("revolution");
        var market = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.AttachCardsTo(room, effect);
        scn.MoveCardsToLSHand(revolution);
        scn.SkipToLSTurn(com.gempukku.swccgo.common.Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(revolution));
        scn.LSPlayCard(revolution);
        scn.LSChooseCard(market);
        scn.PassAllResponses();
        assertTrue(revolution.getZone() == com.gempukku.swccgo.common.Zone.LOST_PILE
                || revolution.getZone() == com.gempukku.swccgo.common.Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void ThereIsNoTryCancelsRevolutionOnTable() {
        var scn = GetPlayScenario();
        var effect = scn.GetDSCard("try");
        var room = scn.GetDSCard("room");
        var revolution = scn.GetLSCard("revolution");
        var market = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.AttachCardsTo(market, revolution);
        scn.MoveCardsToDSHand(effect);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.DEPLOY);
        scn.DSPlayCard(effect);
        scn.DSChooseCard(room);
        scn.PassAllResponses();
        assertTrue(revolution.getZone() == com.gempukku.swccgo.common.Zone.LOST_PILE
                || revolution.getZone() == com.gempukku.swccgo.common.Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void ThereIsNoTryBansAliensExceptBountyHuntersAndMaraJadeAndBansEpisodeI() {
        var scn = GetPlayScenario();
        var effect = scn.GetDSCard("try");
        var room = scn.GetDSCard("room");
        var garindan = scn.GetDSCard("garindan");
        var bossk = scn.GetDSCard("bossk");
        var mara = scn.GetDSCard("mara");
        var aks = scn.GetDSCard("aks");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.AttachCardsTo(room, effect);
        scn.MoveCardsToDSHand(garindan, bossk, mara, aks);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(garindan));
        assertTrue(scn.DSCardPlayAvailable(bossk));
        assertTrue(scn.DSCardPlayAvailable(mara));
        assertFalse(scn.DSCardPlayAvailable(aks));
    }

    @Test
    public void ThereIsNoTryMakesYourAdmiralsCouncilMembersNotOpponents() {
        var scn = GetPlayScenario();
        var effect = scn.GetDSCard("try");
        var room = scn.GetDSCard("room");
        var motti = scn.GetDSCard("motti");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.AttachCardsTo(room, effect);
        scn.MoveCardsToLocation(room, motti, luke);
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.KeywordModifier(luke, Keyword.ADMIRAL));
        assertTrue(scn.HasKeyword(motti, Keyword.IMPERIAL_COUNCIL_MEMBER));
        assertEquals(1, scn.game().getModifiersQuerying().getInfluence(scn.gameState(), motti), scn.epsilon);
        assertFalse(scn.HasKeyword(luke, Keyword.IMPERIAL_COUNCIL_MEMBER));
    }

    @Test
    public void ThereIsNoTryTakesPoliticalEffectIntoHandDuringControl() {
        var scn = GetPlayScenario();
        var effect = scn.GetDSCard("try");
        var room = scn.GetDSCard("room");
        var blown = scn.GetDSCard("blown");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.AttachCardsTo(room, effect);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.CONTROL);
        scn.EnsureDSForcePile(2);
        scn.MoveCardsToTopOfDSReserveDeck(blown);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(scn.DSCardActionAvailable(effect, "Take Laser Gate"));
        scn.DSUseCardAction(effect, "Take Laser Gate");
        scn.PassCardAndForceUseResponses();
        if (scn.DSHasCardChoiceAvailable(blown)) {
            scn.DSChooseCard(blown);
        }
        scn.PassAllResponses();
        assertEquals(com.gempukku.swccgo.common.Zone.HAND, blown.getZone());
    }

    @Test
    public void ThereIsNoTryDeploysStackedCardFromChosenPoliticalEffect() {
        var scn = GetPlayScenario();
        var effect = scn.GetDSCard("try");
        var room = scn.GetDSCard("room");
        var blown = scn.GetDSCard("blown");
        var crush = scn.GetDSCard("crush");
        var evax = scn.GetDSCard("evax");
        var yularen = scn.GetDSCard("yularen");
        var market = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.AttachCardsTo(room, effect);
        scn.MoveCardsToDSSideOfTable(blown, crush);
        scn.StackCardsOn(blown, evax);
        scn.StackCardsOn(crush, yularen);
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(effect, "Deploy a card stacked"));
        scn.DSUseCardAction(effect, "Deploy a card stacked");
        assertTrue(scn.DSHasCardChoiceAvailable(blown));
        assertTrue(scn.DSHasCardChoiceAvailable(crush));
        scn.DSChooseCard(crush);
        scn.PassAllResponses();
        scn.DSChooseCard(market);
        scn.PassAllResponses();
        assertAtLocation(market, yularen);
    }

    @Test
    public void ThereIsNoTryPlacesCanceledPoliticalEffectInUsedPile() {
        var scn = GetPlayScenario();
        var effect = scn.GetDSCard("try");
        var room = scn.GetDSCard("room");
        var blown = scn.GetDSCard("blown");
        var alter = scn.GetLSCard("alter");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.AttachCardsTo(room, effect);
        scn.MoveCardsToDSSideOfTable(blown);
        scn.MoveCardsToLocation(room, luke);
        scn.MoveCardsToLSHand(alter);
        scn.SkipToLSTurn(com.gempukku.swccgo.common.Phase.CONTROL);
        scn.PrepareLSDestiny(1);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue("Alter not offered. actions=" + scn.GetLSAvailableActions(),
                scn.LSCardActionAvailable(alter));
        scn.LSUseCardAction(alter);
        scn.LSChooseCard(blown);
        scn.LSChooseCard(luke);
        scn.PassAllResponses();
        assertTrue(blown.getZone() == com.gempukku.swccgo.common.Zone.USED_PILE
                || blown.getZone() == com.gempukku.swccgo.common.Zone.TOP_OF_USED_PILE);
    }

    private VirtualTableScenario GetPlayScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("revolution", "1_62");
                    put("alter", "12_054");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("try", "1112_42");
                    put("room", "1240_51");
                    put("emperor", "9_109");
                    put("evax", "1240_12");
                    put("yularen", "1240_4");
                    put("motti", "1_164");
                    put("blown", "1240_21");
                    put("crush", "1240_22");
                    put("garindan", "1_177");
                    put("bossk", "4_092");
                    put("mara", "110_10");
                    put("aks", "12_097");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }
}
