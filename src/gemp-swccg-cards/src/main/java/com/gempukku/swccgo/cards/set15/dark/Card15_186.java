package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set5.dark.Card5_120;
import com.gempukku.swccgo.cards.set4.dark.Card4_133;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import java.util.List;

public class Card15_186 extends Card5_120 {
    private static final DarkPathActions DARK_PATH = new DarkPathActions();
    public Card15_186() {
        setLore(""); setGameText("Deploy on table. Whenever you control any two Rebel Base locations, or any one planet site and two systems, the effects of Revolution and all opponent's Force drain bonuses are canceled. Also, once per turn, you may peek at the top three cards of your Reserve Deck. Place two of those three in your Lost pile. (Immune to Alter.)"); setAlternateDestiny(2);
        addComboCardTitles("Imperial Decree","The Dark Path");
        addIcon(Icon.CLOUD_CITY,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Imperial Decree & The Dark Path"; }
    @Override public Float getDestiny() { return 2f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self,int textId) {
        return DARK_PATH.actions(playerId,game,self,textId);
    }
    private static final class DarkPathActions extends Card4_133 {
        List<TopLevelGameTextAction> actions(String playerId,SwccgGame game,PhysicalCard self,int textId) {
            return super.getGameTextTopLevelActions(playerId,game,self,textId);
        }
    }
}
