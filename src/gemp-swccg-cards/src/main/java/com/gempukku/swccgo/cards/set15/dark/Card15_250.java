package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set9.dark.Card9_137;
import com.gempukku.swccgo.cards.set8.dark.Card8_148;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import java.util.ArrayList;
import java.util.List;

public class Card15_250 extends Card9_137 {
    private static final OlderCodeActions OLDER_CODE = new OlderCodeActions();
    public Card15_250() {
        setLore(""); setGameText("USED: Take one admiral or general into hand from Reserve Deck; reshuffle. OR If your admiral is in battle at a system (or your general is in a battle at a site), you may either add one battle destiny or prevent opponent from drawing more than one battle destiny. LOST: Use 2 Force to 'scan' (reveal) opponent's hand. You may place one starship you find there in opponent's Used Pile."); setCardSubtype(CardSubtype.USED_OR_LOST);
        addComboCardTitles("Imperial Command","It's An Older Code");
        addIcon(Icon.DEATH_STAR_II,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Imperial Command & It's An Older Code"; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self) {
        List<PlayInterruptAction> actions=new ArrayList<>();
        List<PlayInterruptAction> used=super.getGameTextTopLevelActions(playerId,game,self);
        if(used!=null) { used.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED)); actions.addAll(used); }
        List<PlayInterruptAction> lost=OLDER_CODE.actions(playerId,game,self);
        if(lost!=null) { lost.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST)); actions.addAll(lost); }
        return actions;
    }
    private static final class OlderCodeActions extends Card8_148 {
        List<PlayInterruptAction> actions(String playerId,SwccgGame game,PhysicalCard self) { return super.getGameTextTopLevelActions(playerId,game,self); }
    }
}
