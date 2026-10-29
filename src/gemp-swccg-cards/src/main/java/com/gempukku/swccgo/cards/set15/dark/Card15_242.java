package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set9.dark.Card9_136;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import java.util.ArrayList;
import java.util.List;

public class Card15_242 extends Card9_136 {
    public Card15_242() {
        setLore(""); setGameText("Target any character (even a captive) present with Emperor. USED: Target is defense value -4 for remainder of turn. LOST: Emperor loses immunity to attrition and is defense value -2 for remainder of turn. Draw Destiny. Target lost if destiny +1 > defense value. OR If Emperor on Death Star II, take Vader into hand from a location you control (cards on him go to owner's hand)."); setAlternateDestiny(4);
        addComboCardTitles(Title.Force_Lightning,"Rise, My Friend");
        addIcon(Icon.DEATH_STAR_II,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Force Lightning & Rise, My Friend"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self) {
        List<PlayInterruptAction> actions=new ArrayList<>(super.getGameTextTopLevelActions(playerId,game,self));
        Filter vader=Filters.and(Filters.Vader,Filters.at(Filters.controls(playerId)));
        if (GameConditions.canSpot(game,self,Filters.and(Filters.Emperor,Filters.on(Title.Death_Star_II)))
                && GameConditions.canTarget(game,self,vader)) {
            final PlayInterruptAction action=new PlayInterruptAction(game,self,CardSubtype.LOST);
            action.setText("Take Vader into hand");
            action.appendTargeting(new TargetCardOnTableEffect(action,playerId,"Choose Vader",vader) {
                @Override protected void cardTargeted(int group,PhysicalCard target) {
                    action.addAnimationGroup(target);
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new ReturnCardToHandFromTableEffect(action,targetingAction.getPrimaryTargetCard(group),Zone.HAND));
                        }
                    });
                }
            });
            actions.add(action);
        }
        return actions;
    }
}
