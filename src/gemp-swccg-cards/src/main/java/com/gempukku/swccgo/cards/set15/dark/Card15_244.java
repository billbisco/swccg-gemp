package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set6.dark.Card6_154;
import com.gempukku.swccgo.cards.set6.dark.Card6_160;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

public class Card15_244 extends Card6_154 {
    private static final ComponentActions COMPONENT = new ComponentActions();
    public Card15_244() {
        setLore(""); setGameText("If Boba Fett, or your character with Mandalorian Armor is present during the weapons phase of a battle, target one opponent's character present. Draw Destiny: (0-1) no effect; (2-3) character immediately captured; (4-5) character is hit; (6+) character immediately lost. OR Use 3 Force to search your Reserve Deck and take one Effect of any kind into hand; reshuffle.");
        setCardSubtype(CardSubtype.USED); setAlternateDestiny(2);
        addComboCardTitles("Hidden Weapons","Twi'lek Advisor");
        addIcon(Icon.JABBAS_PALACE,0);
        addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Hidden Weapons & Twi'lek Advisor"; }
    @Override public Float getDestiny() { return 2f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextTopLevelActions(playerId, game, self);
        if(first != null) actions.addAll(first);
        List<PlayInterruptAction> second = COMPONENT.actions(playerId, game, self);
        if(second != null) actions.addAll(second);
        actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
        return actions;
    }
    private static final class ComponentActions extends Card6_160 {
        List<PlayInterruptAction> actions(String playerId, SwccgGame game, PhysicalCard self) {
            return super.getGameTextTopLevelActions(playerId, game, self);
        }
    }
}
