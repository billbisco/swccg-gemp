package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_093_Tests {
	private static final StartingSetup LetThemMakeTheFirstMove = new StartingSetup() {
		@Override
		public HashMap<String, String> Cards() {
			return new HashMap<>() {{
				put("obj", "13_73");
				put("core", "13_77"); // Theed Palace Generator Core
				put("generator", "13_76"); // Theed Palace Generator
				put("hatred", "13_65"); // Deep Hatred
			}};
		}

		@Override
		public void Setup(VirtualTableScenario scn) {
			if (scn.DSDecisionAvailable("Choose starting objective") || scn.DSDecisionAvailable("Choose your starting")) {
				scn.DSChooseCard(scn.GetDSCard("obj"));
			}
			if (scn.DSDecisionAvailable("Choose Theed Palace Generator Core")) {
				scn.DSChooseCard(scn.GetDSCard("core"));
			}
			if (scn.DSDecisionAvailable("Choose Theed Palace Generator to deploy")
					|| scn.DSDecisionAvailable("Choose Theed Palace Generator")) {
				scn.DSChooseCard(scn.GetDSCard("generator"));
			}
			if (scn.DSDecisionAvailable("Choose Deep Hatred")) {
				scn.DSChooseCard(scn.GetDSCard("hatred"));
			}
			if (scn.DSDecisionAvailable("On which side")) {
				scn.DSChoose("Left");
			}
		}
	};

    private VirtualTableScenario scenario(boolean combat) {
        return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_93","obi","13_33","generator","13_31","core","13_32")),new HashMap<>(Map.of("maul","11_54","combat","1_174","convertedCore","13_77")),40,40,StartingSetup.DefaultLSGroundLocation,combat?LetThemMakeTheFirstMove:StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    @Test public void JediLeapAndClingingToTheEdgeStatsAndKeywordsAreCorrect() {
        var scn=scenario(false);var card=scn.GetLSCard("combo").getBlueprint();assertEquals("Jedi Leap & Clinging To The Edge",card.getTitle());assertEquals(List.of("Jedi Leap","Clinging To The Edge"),card.getTitles());assertEquals(Uniqueness.UNIQUE,card.getUniqueness());
        assertEquals(5f,card.getDestiny(),scn.epsilon);assertEquals(CardSubtype.LOST,card.getCardSubtype());assertEquals(0,card.getCredits());assertEquals("",card.getLore());assertFalse(card.isHorizontal());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());
        scn.BlueprintCardTypeCheck(card,List.of(CardType.INTERRUPT));scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.EPISODE_I,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
    @Test public void JediLeapAndClingingToTheEdgeRelocatesEpisodeIObiWanForOneLostForce() {
        var scn=scenario(false);var combo=scn.GetLSCard("combo");var obi=scn.GetLSCard("obi");var core=scn.GetLSCard("core");var generator=scn.GetLSCard("generator");scn.StartGame();scn.MoveLocationToTable(generator);scn.MoveLocationToTable(core);scn.MoveCardsToLocation(core,obi);scn.MoveCardsToHand(combo);scn.SkipToLSTurn(Phase.DEPLOY);
        int lost=scn.GetLSLostPileCount();scn.LSPlayCard(combo,"Relocate Obi-Wan");scn.LSChooseCard(obi);scn.LSChooseCard(generator);scn.PassAllResponses();scn.LSChooseCard(scn.GetTopOfLSReserveDeck());scn.PassAllResponses();assertTrue(scn.CardsAtLocation(generator,obi));assertEquals(lost+2,scn.GetLSLostPileCount());assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void JediLeapAndClingingToTheEdgeRaisesConvertedInteriorTheedPalaceSite() {
        var scn=scenario(false);var combo=scn.GetLSCard("combo");var core=scn.GetLSCard("core");scn.StartGame();scn.MoveLocationToTable(core);scn.MoveLocationToTable(scn.GetDSCard("convertedCore"));scn.MoveCardsToHand(combo);scn.SkipToLSTurn(Phase.CONTROL);assertEquals(Zone.CONVERTED_LOCATIONS,core.getZone());scn.LSPlayCard(combo,"Raise a converted");scn.LSChooseCard(scn.GetDSCard("convertedCore"));scn.PassAllResponses();assertEquals(Zone.LOCATIONS,core.getZone());assertTrue(scn.GetLSLostPile().contains(combo));
    }
    private void combat(boolean takeHand,boolean hasOwnCombat) {
        var scn=scenario(true);scn.StartGame();var combo=scn.GetLSCard("combo");var obi=scn.GetLSCard("obi");var maul=scn.GetDSCard("maul");var obj=scn.GetDSCard("obj");var card=scn.GetDSCard("combat");
        scn.gameState().flipCard(scn.game(),obj,true);scn.MoveCardsToLocation(scn.GetDSCard("core"),obi,maul);scn.StackCardsFaceDownOn(maul,card);card.setCombatCard(true);scn.MoveCardsToHand(combo);
        if(hasOwnCombat) {var own=scn.GetLSFiller(1);scn.StackCardsFaceDownOn(obi,own);own.setCombatCard(true);}
        scn.SkipToDSTurn(Phase.MOVE);var first=scn.GetLSDestiny(5);var second=scn.GetLSDestiny(6);var leftover=scn.GetLSDestiny(7);scn.MoveCardsToTopOfLSReserveDeck(leftover,second,first);scn.DSUseCardAction(obj,"Initiate lightsaber combat");scn.DSChooseCard(maul);scn.DSChooseCard(obi);
        assertEquals(!hasOwnCombat,scn.LSCardPlayAvailable(combo));if(hasOwnCombat)return;
        scn.LSPlayCard(combo);scn.PassAllResponses();scn.LSChooseCards(first,second);scn.LSChoose(takeHand?"Take into hand":"Return to top of Reserve Deck");scn.PassAllResponses();
        if(takeHand)assertEquals(Zone.HAND,leftover.getZone());else assertEquals(leftover,scn.GetTopOfLSReserveDeck());
        assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void JediLeapAndClingingToTheEdgeChoosesTwoDestiniesAndTakesOtherIntoHand() {combat(true,false);}
    @Test public void JediLeapAndClingingToTheEdgeChoosesTwoDestiniesAndReturnsOtherToReserveTop() {combat(false,false);}
    @Test public void JediLeapAndClingingToTheEdgeRequiresJediWithoutCombatCard() {combat(false,true);}
}
