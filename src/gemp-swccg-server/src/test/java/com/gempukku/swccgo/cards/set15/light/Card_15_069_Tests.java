package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_069_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("deflection","15_69","luke","108_3","rebeltrooper","1_28","jawa","1_12")),new HashMap<>(Map.of("stormtrooper1","1_194","stormtrooper2","1_194","blaster","1_317","jabba","6_109","vader","1_168")));
    }
    @Test public void ChokeAndBlasterDeflectionStatsAndKeywordsAreCorrect() {
        var scn=GetScenario();var card=scn.GetLSCard("deflection").getBlueprint();assertEquals("Choke & Blaster Deflection",card.getTitle());assertEquals(List.of(new TitleComponent("Choke",Uniqueness.UNRESTRICTED),new TitleComponent(Title.Blaster_Deflection,Uniqueness.UNIQUE)),card.getTitleComponents());
        assertEquals(3f,card.getDestiny(),scn.epsilon);assertEquals(CardSubtype.USED_OR_LOST,card.getCardSubtype());assertEquals(0,card.getCredits());assertEquals("",card.getLore());assertFalse(card.isHorizontal());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());
        scn.BlueprintCardTypeCheck(card,List.of(CardType.INTERRUPT));scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
    @Test
    public void ChokeAndBlasterDeflectionUsedCancelsTargeting() {
        var scn = GetScenario();

        var deflection = scn.GetLSCard("deflection");
        var luke = scn.GetLSCard("luke");
        scn.MoveCardsToHand(deflection);

        var site = scn.GetLSStartingLocation();

        var stormtrooper1 = scn.GetDSCard("stormtrooper1");
        var blaster = scn.GetDSCard("blaster");

        scn.StartGame();

        scn.MoveCardsToLocation(site, luke, stormtrooper1);
        scn.AttachCardsTo(stormtrooper1, blaster);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(blaster));
        scn.DSUseCardAction(blaster);
        scn.DSChooseCard(luke);
        scn.LSPass(); //Use 1 Force - Optional responses
        scn.DSPass();
        assertTrue(scn.LSPlayUsedInterruptAvailable(deflection));
        scn.LSPlayUsedInterrupt(deflection);
        scn.PassAllResponses();
        assertFalse(luke.isHit());assertTrue(scn.GetLSUsedPile().contains(deflection));
    }

    @Test
    public void ChokeAndBlasterDeflectionLostRetargetsNonFreeBlaster() {
        // Regression coverage for https://github.com/PlayersCommittee/gemp-swccg-public/issues/859 (Imperial Blaster / non-free path)

        var scn = GetScenario();

        var deflection = scn.GetLSCard("deflection");
        var luke = scn.GetLSCard("luke");
        var rebeltrooper = scn.GetLSCard("rebeltrooper");
        scn.MoveCardsToHand(deflection);

        var site = scn.GetLSStartingLocation();

        var stormtrooper1 = scn.GetDSCard("stormtrooper1");
        var stormtrooper2 = scn.GetDSCard("stormtrooper2");
        var blaster = scn.GetDSCard("blaster");

        scn.StartGame();

        scn.MoveCardsToLocation(site, luke, rebeltrooper, stormtrooper1,stormtrooper2);
        scn.AttachCardsTo(stormtrooper1, blaster);

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(blaster));
        scn.DSUseCardAction(blaster);
        scn.DSChooseCard(luke);
        scn.LSPass(); //Use 1 Force - Optional responses
        scn.DSPass();
        assertTrue(scn.GetLSForcePileCount() >= 3);
        assertTrue(scn.LSPlayLostInterruptAvailable(deflection));
        int force=scn.GetLSForcePileCount();scn.PrepareDSDestiny(7);scn.LSPlayLostInterrupt(deflection);
        assertTrue(scn.LSDecisionAvailable("Choose character to re-target from"));
        assertTrue(scn.LSHasCardChoiceAvailable(luke));
        assertFalse(scn.LSHasCardChoicesAvailable(rebeltrooper));
        assertFalse(scn.LSHasCardChoicesAvailable(stormtrooper1));
        assertFalse(scn.LSHasCardChoicesAvailable(stormtrooper2));
        scn.LSChooseCard(luke);
        assertTrue(scn.LSDecisionAvailable("Choose character to re-target to"));
        assertTrue(scn.LSHasCardChoicesAvailable(stormtrooper1, stormtrooper2));
        assertFalse(scn.LSHasCardChoiceAvailable(rebeltrooper));
        scn.LSChooseCard(stormtrooper2);

        scn.PassAllResponses();assertFalse(luke.isHit());assertTrue(stormtrooper2.isHit());assertEquals(force-3,scn.GetLSForcePileCount());assertTrue(scn.GetLSLostPile().contains(deflection));
    }


    @Test public void ChokeAndBlasterDeflectionCancelsUniqueAlienTextUntilEndOfTurn() {
        var scn=GetScenario();var combo=scn.GetLSCard("deflection");var jabba=scn.GetDSCard("jabba");var jawa=scn.GetLSCard("jawa");scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),jabba,jawa);scn.MoveCardsToHand(combo);scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(),jabba));scn.LSPlayCard(combo,"Cancel alien");assertFalse(scn.LSHasCardChoiceAvailable(jawa));scn.LSChooseCard(jabba);scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(),jabba));assertTrue(scn.GetLSLostPile().contains(combo));scn.SkipToPhase(Phase.DRAW);scn.PassDrawActions();scn.PassResponses("RECIRCULATED");scn.PassAllResponses();assertFalse(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(),jabba));
    }
    @Test public void ChokeAndBlasterDeflectionCannotCancelAlienTextOutsideControl() {
        var scn=GetScenario();scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("jabba"));scn.MoveCardsToHand(scn.GetLSCard("deflection"));scn.SkipToLSTurn(Phase.DEPLOY);assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("deflection"),"Cancel alien"));
    }
    @Test public void ChokeAndBlasterDeflectionCancelsRemainingAttritionAfterAlienForfeit() {
        var scn=GetScenario();var combo=scn.GetLSCard("deflection");var jawa=scn.GetLSCard("jawa");var site=scn.GetDSStartingLocation();scn.StartGame();scn.MoveCardsToLocation(site,jawa,scn.GetLSCard("luke"),scn.GetDSCard("vader"));scn.MoveCardsToHand(combo);scn.SkipToLSTurn(Phase.BATTLE);scn.PrepareDSDestiny(6);scn.PrepareLSDestiny(0);scn.LSInitiateBattle(site);scn.SkipToDamageSegment(true);
        assertTrue(scn.GetUnpaidLSAttrition()>scn.GetForfeit(jawa));scn.LSChooseCard(jawa);
        for(int i=0;i<24;i++) {if(scn.LSGetDecision()!=null && scn.LSCardPlayAvailable(combo,"Cancel remaining"))break;if(scn.LSGetDecision()!=null)scn.LSPass();else scn.DSPass();}
        scn.LSPlayCard(combo,"Cancel remaining");scn.PassAllResponses();assertEquals(0,scn.GetUnpaidLSAttrition());assertTrue(scn.GetLSLostPile().contains(combo));
    }
}
