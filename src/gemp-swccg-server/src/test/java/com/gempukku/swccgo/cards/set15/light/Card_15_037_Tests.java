package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_037_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_37","luke","1_19")),new HashMap<>(Map.of("workshop","6_163","vader","1_168")));
    }
    @Test public void IMustBeAllowedToSpeakAndGooNeeTayStatsAndKeywordsAreCorrect() {
        var scn=scenario();var card=scn.GetLSCard("combo").getBlueprint();
        assertEquals("I Must Be Allowed To Speak & Goo Nee Tay",card.getTitle());assertEquals(List.of(Title.I_Must_Be_Allowed_To_Speak,Title.Goo_Nee_Tay),card.getTitles());
        assertEquals(3f,card.getDestiny(),scn.epsilon);assertEquals(Uniqueness.UNIQUE,card.getUniqueness());assertEquals(0,card.getCredits());assertEquals("",card.getLore());assertFalse(card.isHorizontal());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());assertTrue(card.isImmuneToCardTitle(Title.Alter));
        scn.BlueprintIconCheck(card,List.of(Icon.EFFECT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of(Keyword.DEPLOYS_ON_SITE));scn.BlueprintPersonaCheck(card,List.of());
    }
    private float cost(VirtualTableScenario scn,PhysicalCard card,PhysicalCard site) {
        return scn.game().getModifiersQuerying().getDeployCost(scn.gameState(),card,card,site,false,null,false,0,null,false);
    }
    @Test public void IMustBeAllowedToSpeakAndGooNeeTayDeploysLukeWithoutPresenceThenRelocatesAndTaxesOnlyHere() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var luke=scn.GetLSCard("luke");var site=scn.GetDSCard("workshop");var vader=scn.GetDSCard("vader");
        scn.StartGame();scn.MoveLocationToTable(site);scn.MoveCardsToHand(combo,luke);scn.EnsureLSForcePile(6);scn.SkipToLSTurn(Phase.DEPLOY);
        float before=cost(scn,vader,site);scn.LSPlayCard(combo);scn.LSChooseCard(site);scn.PassAllResponses();
        assertEquals(site,combo.getAttachedTo());assertEquals(before,cost(scn,vader,site),scn.epsilon);
        scn.DSPass();scn.LSPlayCard(luke);scn.LSChooseCard(site);scn.PassAllResponses();
        assertEquals(luke,combo.getAttachedTo());assertEquals(Float.MAX_VALUE,scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(),luke),scn.epsilon);
        assertEquals(before+2,cost(scn,vader,site),scn.epsilon);
        assertEquals(scn.GetDeployCost(vader),cost(scn,vader,scn.GetDSStartingLocation()),scn.epsilon);
        assertEquals(scn.GetDeployCost(scn.GetDSFiller(1)),cost(scn,scn.GetDSFiller(1),site),scn.epsilon);
    }
    @Test public void IMustBeAllowedToSpeakAndGooNeeTayImmunityEndsOffTatooineButDeployTaxFollowsLuke() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var luke=scn.GetLSCard("luke");var vader=scn.GetDSCard("vader");var site=scn.GetLSStartingLocation();
        scn.StartGame();scn.MoveCardsToLocation(site,luke);scn.AttachCardsTo(luke,combo);
        assertEquals(3f,scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(),luke),scn.epsilon);
        assertEquals(scn.GetDeployCost(vader)+2,cost(scn,vader,site),scn.epsilon);
    }
}
