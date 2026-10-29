package com.gempukku.swccgo.cards.set15;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;
public class ReflectionsGoldMergedComponentTests {
    @Test public void BlastDoorControlsAndReactorTerminalHasIndependentPrintedDots() {
        var card=new com.gempukku.swccgo.cards.set15.dark.Card15_175();
        assertEquals(List.of(new TitleComponent("Blast Door Controls",Uniqueness.UNIQUE),new TitleComponent("Reactor Terminal",Uniqueness.UNRESTRICTED)),card.getTitleComponents());
    }
    @Test public void BlastDoorControlsAndReactorTerminalAllowsUnlimitedComponentInEitherOrder() {
        for(boolean comboFirst:new boolean[]{true,false}) {
            var dark=new HashMap<String,String>(); dark.put("combo","15_175"); dark.put("terminal","1_228"); dark.put("terminal2","1_228"); dark.put("blast","1_210"); dark.put("control","15_215");
            var scn=new VirtualTableScenario(new HashMap<>(),dark,40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open); scn.StartGame();
            for(String n:List.of("combo","terminal","terminal2","blast","control")) scn.MoveCardsToDSHand(scn.GetDSCard(n));
            scn.SkipToDSTurn(Phase.DEPLOY);
            scn.DSPlayCardAndPassResponses(scn.GetDSCard(comboFirst?"combo":"terminal")); if(scn.LSGetDecision()!=null) scn.LSPass();
            scn.DSPlayCardAndPassResponses(scn.GetDSCard(comboFirst?"terminal":"combo")); if(scn.LSGetDecision()!=null) scn.LSPass();
            assertNotNull(scn.DSGetDecision()); assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("blast")));
            scn.DSPlayCardAndPassResponses(scn.GetDSCard("terminal2"));
            assertTrue(scn.GetDSCard("terminal2").getZone().isInPlay());
            if(scn.LSGetDecision()!=null) scn.LSPass(); assertNotNull(scn.DSGetDecision()); assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("blast")));
        }
    }
    @Test public void AllOtherCorrectedCombosMatchIndependentImageSymbols() {
        assertEquals(List.of(new TitleComponent("Dark Collaboration",Uniqueness.UNIQUE),new TitleComponent("Charming To The Last",Uniqueness.UNRESTRICTED)),new com.gempukku.swccgo.cards.set15.dark.Card15_230().getTitleComponents());
        assertEquals(List.of(new TitleComponent("Overwhelmed",Uniqueness.UNRESTRICTED),new TitleComponent("A Dark Time For The Rebellion",Uniqueness.UNIQUE)),new com.gempukku.swccgo.cards.set15.dark.Card15_269().getTitleComponents());
        assertEquals(List.of(new TitleComponent("Point Man",Uniqueness.UNRESTRICTED),new TitleComponent("Slip Sliding Away",Uniqueness.UNIQUE)),new com.gempukku.swccgo.cards.set15.dark.Card15_273().getTitleComponents());
        assertEquals(List.of(new TitleComponent("Put All Sections On Alert",Uniqueness.UNIQUE),new TitleComponent("Full Scale Alert",Uniqueness.UNRESTRICTED)),new com.gempukku.swccgo.cards.set15.dark.Card15_275().getTitleComponents());
        assertEquals(List.of(new TitleComponent("Shut Him Up Or Shut Him Down",Uniqueness.UNIQUE),new TitleComponent("Voyeur",Uniqueness.RESTRICTED_2)),new com.gempukku.swccgo.cards.set15.dark.Card15_280().getTitleComponents());
        assertEquals(List.of(new TitleComponent("Something Hit Us!",Uniqueness.UNRESTRICTED),new TitleComponent("Why Didn't You Tell Me?",Uniqueness.UNIQUE)),new com.gempukku.swccgo.cards.set15.dark.Card15_282().getTitleComponents());
        assertEquals(List.of(new TitleComponent("The Circle Is Now Complete",Uniqueness.UNRESTRICTED),new TitleComponent("Vader's Obsession",Uniqueness.UNIQUE)),new com.gempukku.swccgo.cards.set15.dark.Card15_290().getTitleComponents());
        assertEquals(List.of(new TitleComponent("Trade Federation Tactics",Uniqueness.UNIQUE),new TitleComponent("We're Hit, Artoo",Uniqueness.RESTRICTED_2)),new com.gempukku.swccgo.cards.set15.dark.Card15_294().getTitleComponents());
        assertEquals(List.of(new TitleComponent("Trooper Assault",Uniqueness.UNRESTRICTED),new TitleComponent("Tactical Support",Uniqueness.UNIQUE)),new com.gempukku.swccgo.cards.set15.dark.Card15_297().getTitleComponents());
    }
    private void playVoyeur(VirtualTableScenario scn,com.gempukku.swccgo.game.PhysicalCardImpl card) {
        scn.DSPlayCard(card,"Reveal opponent's Used Pile"); scn.DSChooseOption("Rebel Barrier"); scn.PassAllResponses();
        scn.DSDismissRevealedCards(); scn.LSDismissRevealedCards(); scn.PassAllResponses();
        scn.DSChooseOption("Used"); scn.PassAllResponses();
        if(scn.LSGetDecision()!=null) scn.LSPass();
    }
    @Test public void ShutHimUpAndVoyeurAllowsSecondVoyeurButNotThirdInEitherPlayOrder() {
        for(boolean comboFirst:new boolean[]{true,false}) {
            var dark=new HashMap<String,String>(); dark.put("combo","15_280"); dark.put("voyeur","4_153"); dark.put("third","4_153"); dark.put("control","15_215");
            var scn=new VirtualTableScenario(new HashMap<>(),dark,40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open); scn.StartGame();
            for(String name:List.of("combo","voyeur","third","control")) scn.MoveCardsToDSHand(scn.GetDSCard(name));
            scn.SkipToDSTurn(Phase.CONTROL); scn.MoveCardsToTopOfLSUsedPile(scn.GetLSFiller(1)); scn.SkipToPhase(Phase.DEPLOY);
            playVoyeur(scn,scn.GetDSCard(comboFirst?"combo":"voyeur"));
            playVoyeur(scn,scn.GetDSCard(comboFirst?"voyeur":"combo"));
            assertNotNull(scn.DSGetDecision()); assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("control")));
            assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("third")));
            scn.MoveCardsToDSHand(scn.GetDSCard("combo"));
            assertTrue(scn.game().getModifiersQuerying().isPlayingCardTitleTurnLimitReached(scn.gameState(),scn.GetDSCard("combo")));
            scn.SkipToPhase(Phase.ACTIVATE);
            assertFalse(scn.game().getModifiersQuerying().isPlayingCardTitleTurnLimitReached(scn.gameState(),scn.GetDSCard("third")));
        }
    }
    @Test public void PointManAndSlipSlidingAwayOnlyConsumesUniqueComponentTurnLimit() {
        for(boolean comboFirst:new boolean[]{true,false}) {
            var dark=new HashMap<String,String>(); dark.put("combo","15_273"); dark.put("slip","5_154"); dark.put("point","5_147"); dark.put("control","15_215");
            var scn=new VirtualTableScenario(new HashMap<>(),dark,40,40,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open); scn.StartGame();
            for(String n:List.of("combo","slip","point","control")) scn.MoveCardsToDSHand(scn.GetDSCard(n));
            scn.SkipToDSTurn(Phase.CONTROL); scn.EnsureDSForcePile(3); var first=scn.GetDSCard(comboFirst?"combo":"slip");
            scn.DSPlayCard(first,"Relocate card to bottom of Deck or Pile"); scn.DSChooseCard(scn.GetTopOfDSForcePile()); scn.PassAllResponses();
            if(scn.LSGetDecision()!=null) scn.LSPass();
            var second=scn.GetDSCard(comboFirst?"slip":"combo");
            assertNotNull(scn.DSGetDecision()); assertFalse(scn.DSCardPlayAvailable(second));
            var query=scn.game().getModifiersQuerying();
            assertFalse(query.isPlayingCardTitleTurnLimitReached(scn.gameState(),scn.GetDSCard("point")));
            assertTrue(query.isPlayingCardTitleTurnLimitReached(scn.gameState(),second));
            scn.SkipToLSTurn(Phase.CONTROL); scn.LSPass(); assertTrue(scn.DSCardPlayAvailable(second));
        }
    }
}
