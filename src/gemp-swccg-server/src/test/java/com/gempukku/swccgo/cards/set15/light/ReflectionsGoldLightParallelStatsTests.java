package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class ReflectionsGoldLightParallelStatsTests {
    private final SwccgCardBlueprintLibrary library=new SwccgCardBlueprintLibrary();
    private final VirtualTableScenario scenario=new VirtualTableScenario(new HashMap<>(),new HashMap<>());
    private void check(int id,int destiny,CardSubtype subtype,boolean episode,int credits,boolean horizontal,Uniqueness... uniqueness) {
        var card=library.getSwccgoCardBlueprint("15_"+id);
        assertEquals(Side.LIGHT,card.getSide()); assertEquals(destiny,card.getDestiny(),0.001);
        assertEquals(subtype,card.getCardSubtype()); assertEquals(credits,card.getCredits()); assertEquals(horizontal,card.isHorizontal());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet()); assertEquals(Rarity.UNKNOWN,card.getRarity()); assertEquals("",card.getLore());
        scenario.BlueprintCardTypeCheck(card,List.of(CardType.INTERRUPT)); scenario.BlueprintKeywordCheck(card,List.of()); scenario.BlueprintPersonaCheck(card,List.of());
        var icons=new ArrayList<>(List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD)); if(episode)icons.add(Icon.EPISODE_I); scenario.BlueprintIconCheck(card,icons);
        assertEquals(Arrays.asList(uniqueness),card.getTitleComponents().stream().map(TitleComponent::uniqueness).toList());
    }
    @Test public void LosingTrackPrintedStatsAndCanonicalStepBackwardIdentity() { check(99,4,CardSubtype.USED_OR_LOST,true,0,false,Uniqueness.UNIQUE,Uniqueness.UNIQUE); assertEquals(List.of("Losing Track","A Step Backward"),library.getSwccgoCardBlueprint("15_99").getTitles()); }
    @Test public void LostInWildernessAndYubYubBothHavePrintedUniqueSymbols() { check(100,5,CardSubtype.USED_OR_LOST,false,0,false,Uniqueness.UNIQUE,Uniqueness.UNIQUE); }
    @Test public void LoyalBodyguardHasTwoUniqueComponents() { check(105,4,CardSubtype.USED,true,0,false,Uniqueness.UNIQUE,Uniqueness.UNIQUE); }
    @Test public void NabrunAndMercenaryAreBothUnrestricted() { check(106,5,CardSubtype.LOST,false,0,false,Uniqueness.UNRESTRICTED,Uniqueness.UNRESTRICTED); }
    @Test public void NarrowEscapeIsUniqueHorizontalAndCostsThreeCredits() { check(107,5,CardSubtype.USED,false,3,true,Uniqueness.UNIQUE); }
    @Test public void NeckAndNeckAndChangingOddsAreUsedOrLost() { check(109,5,CardSubtype.USED_OR_LOST,true,0,false,Uniqueness.UNIQUE,Uniqueness.UNIQUE); }
}
