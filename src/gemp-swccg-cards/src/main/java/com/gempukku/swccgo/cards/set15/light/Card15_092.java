package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.cards.set9.light.Card9_049;
import com.gempukku.swccgo.cards.set9.light.Card9_052;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import java.util.ArrayList;
import java.util.List;

public class Card15_092 extends Card9_052 {
    public Card15_092() {
        setLore("");addIcon(Icon.DEATH_STAR_II,0);addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Insertion Planning","Critical Error Revealed");
        setGameText("If your scout is in battle at an exterior planet site, subtract 3 from opponent's total battle destiny. OR Target opponent's character aboard an open vehicle moving to your scout's site. Draw destiny. If destiny +2 > ability, target goes to Used Pile. OR If you have a leader at your war room, peek at top card of opponent's Reserve Deck. You may place it on bottom of that Reserve Deck.");
    }
    @Override public String getTitle() {return "Insertion Planning & Critical Error Revealed";}
    @Override public Float getDestiny() {return 4f;}
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}
    private static final CriticalErrorActions CRITICAL_ERROR=new CriticalErrorActions();
    private static final class CriticalErrorActions extends Card9_049 {
        List<PlayInterruptAction> actions(String playerId,SwccgGame game,PhysicalCard self) {
            return super.getGameTextTopLevelActions(playerId,game,self);
        }
    }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self) {
        List<PlayInterruptAction> actions=new ArrayList<>();
        List<PlayInterruptAction> insertion=super.getGameTextTopLevelActions(playerId,game,self);
        if(insertion!=null)actions.addAll(insertion);
        List<PlayInterruptAction> critical=CRITICAL_ERROR.actions(playerId,game,self);
        if(critical!=null)actions.addAll(critical);
        return actions;
    }
}
