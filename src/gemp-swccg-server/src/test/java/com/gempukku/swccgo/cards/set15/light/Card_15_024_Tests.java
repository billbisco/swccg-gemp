package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_024_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("beggar","15_24","jess","6_21","talz","1_31")),new HashMap<>());
    }
    @Test public void BeggarAndRevolutionStatsAndKeywordsAreCorrect() {
        var scn=GetScenario();var card=scn.GetLSCard("beggar").getBlueprint();
        assertEquals("Beggar & Revolution",card.getTitle());assertEquals(List.of(new TitleComponent(Title.Beggar,Uniqueness.UNIQUE),new TitleComponent(Title.Revolution,Uniqueness.UNRESTRICTED)),card.getTitleComponents());
        assertEquals(3f,card.getDestiny(),scn.epsilon);assertEquals(0,card.getCredits());assertFalse(card.isHorizontal());assertEquals("",card.getLore());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());
        scn.BlueprintCardTypeCheck(card,List.of(CardType.EFFECT));scn.BlueprintIconCheck(card,List.of(Icon.EFFECT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of(Keyword.DEPLOYS_ON_LOCATION));scn.BlueprintPersonaCheck(card,List.of());
    }
    @Test public void BeggarAndRevolutionDeploysOnNonTatooineLocationForThreeAndRotatesIt() {
        var scn=GetScenario();var card=scn.GetLSCard("beggar");var site=scn.GetLSStartingLocation();
        scn.StartGame();scn.MoveCardsToHand(card);scn.SkipToLSTurn(Phase.DEPLOY);int force=scn.GetLSForcePileCount();
        assertFalse(scn.game().getModifiersQuerying().isRotatedLocation(scn.gameState(),site));
        scn.LSPlayCard(card);scn.LSChooseCard(site);scn.PassAllResponses();
        assertEquals(site,card.getAttachedTo());assertEquals(force-3,scn.GetLSForcePileCount());assertTrue(scn.game().getModifiersQuerying().isRotatedLocation(scn.gameState(),site));
    }
    @Test
    public void BeggarAndRevolutionRequiresUsingOpponentForceIfCannotPayWithOwnForce() {
        var scn = GetScenario();

        var beggar = scn.GetLSCard("beggar");
        var jess = scn.GetLSCard("jess");
        var talz = scn.GetLSCard("talz");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.AttachCardsTo(site,beggar);
        scn.MoveCardsToLocation(site,jess,talz);

        scn.SkipToLSTurn(Phase.ACTIVATE);

        assertEquals(2,scn.GetDSForcePileCount());
        assertEquals(0,scn.GetLSForcePileCount());
        assertEquals(0,scn.GetDSUsedPileCount());

        assertTrue(scn.LSCardActionAvailable(jess,"Charm")); //test1
        scn.LSUseCardAction(jess,"Charm");
        scn.LSChooseCard(talz);

        assertTrue(scn.LSDecisionAvailable("opponent's Force to use")); //test2
        assertEquals(1,scn.LSGetChoiceMin()); //test3
        assertEquals(1,scn.LSGetChoiceMax()); //test4
        scn.LSDecided(1);
        scn.PassAllResponses();

        assertEquals(1,scn.GetDSUsedPileCount()); //test5
        scn.DSPass();

        assertTrue(scn.AwaitingLSActivatePhaseActions());
        scn.LSActivateMaxForceAndPass();
        scn.SkipToDSTurn();

        assertEquals(0,scn.GetLSLostPileCount()); //test6
    }

    @Test
    public void BeggarAndRevolutionAllowsUsingOpponentForceMoreThanOnce() {

        var scn = GetScenario();

        var beggar = scn.GetLSCard("beggar");
        var jess = scn.GetLSCard("jess");

        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.AttachCardsTo(site,beggar);
        scn.MoveCardsToLSHand(jess);

        scn.SkipToLSTurn(Phase.DEPLOY);

        scn.DSActivateForceCheat(2);
        scn.LSActivateForceCheat(2);
        assertEquals(4,scn.GetDSForcePileCount());
        assertEquals(5,scn.GetLSForcePileCount());
        assertEquals(0,scn.GetDSUsedPileCount());

        scn.LSDeployCard(jess);
        scn.LSChooseCard(site);

        scn.DSPass(); //Use 3 Force - Optional responses

        assertTrue(scn.LSCardActionAvailable(beggar,"opponent's Force")); //test1
        scn.LSUseCardAction(beggar,"opponent's Force");
        assertEquals(1,scn.LSGetChoiceMin());
        assertEquals(3,scn.LSGetChoiceMax()); //test2
        scn.LSDecided(1);

        scn.DSPass(); //Use 2 Force - Optional responses

        assertTrue(scn.LSCardActionAvailable(beggar,"opponent's Force")); //test3
        scn.LSUseCardAction(beggar,"opponent's Force");
        assertEquals(1,scn.LSGetChoiceMin());
        assertEquals(2,scn.LSGetChoiceMax()); //test4
        scn.LSDecided(1);

        scn.DSPass(); //Use 1 Force - Optional responses

        assertTrue(scn.LSCardActionAvailable(beggar,"opponent's Force"));

        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.CardsAtLocation(site,jess)); //test5
        assertEquals(2,scn.GetDSUsedPileCount()); //test6
        assertEquals(1,scn.GetLSUsedPileCount()); //test7

        scn.SkipToPhase(Phase.DRAW);

        scn.LSPass(); //Choose Draw action or Pass
        scn.DSPass();

        scn.LSPass(); //RECIRCULATED - Optional responses
        scn.DSPass();

        scn.LSPass(); //RECIRCULATED - Optional responses
        scn.DSPass();

        assertTrue(scn.DSDecisionAvailable("ABOUT_TO_BE_LOST_FROM_TABLE"));
        scn.PassAllResponses();

        scn.SkipToDSTurn();

        assertEquals(1,scn.GetLSLostPileCount()); //test8
        assertFalse(scn.game().getModifiersQuerying().isRotatedLocation(scn.gameState(),site));
    }

}
