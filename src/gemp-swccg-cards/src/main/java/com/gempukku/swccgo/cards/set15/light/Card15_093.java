package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.cards.set13.light.Card13_026;
import com.gempukku.swccgo.cards.set13.light.Card13_010;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;

public class Card15_093 extends Card13_026 {
    private static final ClingingActions CLINGING=new ClingingActions();
    public Card15_093() {
        setLore("");addIcon(Icon.REFLECTIONS_III,0);addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Jedi Leap","Clinging To The Edge");
        setGameText("During your deploy phase, lose 1 Force to relocate Episode I Obi-Wan to an adjacent Naboo site. OR Raise your converted interior Theed Palace site to the top. OR If opponent's Dark Jedi with at least one combat card just initiated lightsaber combat against your Jedi with none, draw 3 destiny and choose 2 to use for lightsaber combat destiny. You may take other card into hand, or return it to top of Reserve Deck.");
    }
    @Override public String getTitle() {return "Jedi Leap & Clinging To The Edge";}
    @Override public Float getDestiny() {return 5f;}
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) {
        return CLINGING.actions(playerId,game,result,self);
    }
    private static class ClingingActions extends Card13_010 {
        List<PlayInterruptAction> actions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) {return super.getGameTextOptionalAfterActions(playerId,game,result,self);}
    }
}
