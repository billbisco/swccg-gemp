package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set5.dark.Card5_153;
import com.gempukku.swccgo.cards.set4.dark.Card4_144;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;

public class Card15_248 extends Card5_153 {
    private static final ImbalanceActions IMBALANCE = new ImbalanceActions();
    public Card15_248() {
        setLore(""); setGameText("Target a location. Scomp Links there cannot be used for remainder of turn. OR If opponent is about to scan or otherwise look through your hand (unless using Grimtaash), opponent continues but must lose 4 Force plus the card allowing the scan. OR If opponent just retrieved Force, opponent must lose X Force, where X = one-half the number of cards retrieved (round up)."); setAlternateDestiny(4);
        setTitleComponents(new TitleComponent("Imbalance",Uniqueness.UNRESTRICTED),new TitleComponent("Shocking Revelation",Uniqueness.UNIQUE)); addIcon(Icon.CLOUD_CITY,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Imbalance & Shocking Revelation"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) {
        return IMBALANCE.actions(playerId,game,result,self);
    }
    private static final class ImbalanceActions extends Card4_144 {
        List<PlayInterruptAction> actions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) { return super.getGameTextOptionalAfterActions(playerId,game,result,self); }
    }
}
